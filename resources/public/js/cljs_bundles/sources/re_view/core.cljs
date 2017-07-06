(ns re-view.core
  (:refer-clojure :exclude [partial])
  (:require-macros [re-view.core])
  (:require [re-db.core :as re-db]
            [re-db.d :as d]
            [re-db.patterns :as patterns :include-macros true]
            [re-view.render-loop :as render-loop]
            [re-view-hiccup.core :as hiccup]
            [clojure.string :as string]
            [goog.dom :as gdom]
            [goog.object :as gobj]
            [re-view.util :as v-util]
            [re-view.view-spec :as vspec]
            [cljsjs.react]))

(def schedule! render-loop/schedule!)
(def force-update render-loop/force-update)
(def force-update! render-loop/force-update!)
(def flush! render-loop/flush!)

(def ^:dynamic *trigger-state-render* true)

(goog-define INSTRUMENT! false)

(defn dom-node
  "Return DOM node for component"
  [component]
  (.findDOMNode js/ReactDOM component))

(defn mounted?
  "Returns true if component is still mounted to the DOM.
  This is necessary to avoid updating unmounted components."
  [this]
  (not (true? (aget this "unmounted"))))

(defn reactive-render
  "Wrap a render function to force-update the component when re-db patterns accessed during evaluation are invalidated."
  [f]
  (fn []
    (this-as this
      (let [{:keys [patterns value]} (patterns/capture-patterns (apply f this (aget this "re$view" "children")))
            prev-patterns (aget this "re$view" "dbPatterns")]
        (when-not (= prev-patterns patterns)
          (when-let [un-sub (aget this "reactiveUnsubscribe")] (un-sub))

          (aset this "reactiveUnsubscribe" (when-not (empty? patterns)
                                             (d/listen patterns #(force-update this))))
          (aset this "re$view" "dbPatterns" patterns))
        value))))

(def kmap
  "Mapping of convenience keys to React lifecycle method keys."
  #:life {:constructor        "constructor"
          :initial-state      "$getInitialState"
          :will-mount         "componentWillMount"
          :did-mount          "componentDidMount"
          :will-receive-props "componentWillReceiveProps"
          :will-receive-state "componentWillReceiveState"
          :should-update      "shouldComponentUpdate"
          :will-update        "componentWillUpdate"
          :did-update         "componentDidUpdate"
          :will-unmount       "componentWillUnmount"
          :render             "render"})

(defn compseq
  "Compose fns to execute sequentially over the same arguments"
  [& fns]
  (fn [& args]
    (doseq [f fns]
      (apply f args))))

(defn collect
  "Merge a list of method maps, preserving special behavour of :should-update and wrapping methods with the same key to execute sequentially."
  [methods]
  (let [methods (apply merge-with (fn [a b] (if (vector? a) (conj a b) [a b])) methods)]
    (reduce-kv (fn [m method-k fns]
                 (cond-> m
                         (vector? fns) (assoc method-k (if (keyword-identical? method-k :life/should-update)
                                                         (apply v-util/any-pred fns)
                                                         (apply compseq fns))))) methods methods)))

(defn wrap-methods
  "Wrap a component's methods, binding arguments and specifying lifecycle update behaviour."
  [method-k f]
  (if-not (fn? f)
    f
    (case method-k
      (:life/initial-state
        :key
        :life/constructor) f
      :life/render (reactive-render f)
      :life/will-receive-props
      (fn [props]
        (binding [*trigger-state-render* false]
          (f (js-this) props)))
      (:life/will-mount
        :life/will-unmount
        :life/will-receive-state
        :life/will-update)
      (fn []
        (binding [*trigger-state-render* false]
          (apply f (js-this) (aget (js-this) "re$view" "children"))))
      (:life/did-mount
        :life/did-update)
      (fn []
        (apply f (js-this) (aget (js-this) "re$view" "children")))
      (fn [& args]
        (apply f (js-this) args)))))

(defn init-state
  "Return a state atom for component. The component will update when it changes."
  [this initial-state]
  (let [a (atom initial-state)]
    (doto (aget this "re$view")
      (aset "state" a)
      (aset "prevState" initial-state))
    (add-watch a :state-changed (fn [_ _ old-state new-state]
                                  (when (not= old-state new-state)
                                    (aset this "re$view" "prevState" old-state)
                                    (when-let [^js/Function will-receive (aget this "componentWillReceiveState")]
                                      (.call will-receive this))
                                    (when *trigger-state-render*
                                      (force-update this)))))
    a))

(defn init-props
  "When a component is instantiated, bind element methods and populate initial props."
  [this $props]
  (if $props
    (do (aset this "re$view" (doto (gobj/clone (aget $props "class"))
                               (aset "props" (aget $props "props"))
                               (aset "children" (aget $props "children"))))
        (when-let [instance-keys (aget $props "instance")]
          (doseq [k (gobj/getKeys instance-keys)]
            (let [f (aget instance-keys k)]
              (aset this k (if (fn? f) (fn [& args]
                                         (apply f this args)) f))))))
    (aset this "re$view" #js {"props"    nil
                              "children" nil}))
  this)

(defn wrap-lifecycle-methods
  "Augment lifecycle methods with default behaviour."
  [methods]
  (->> (collect [{:life/will-receive-props (fn [this props]
                                             (let [{prev-props :view/props prev-children :view/children :as this} this]
                                               (let [next-props (aget props "props")]
                                                 (aset this "re$view" "props" next-props)
                                                 (aset this "re$view" "prevProps" prev-props)
                                                 (aset this "re$view" "children" (aget props "children"))
                                                 (aset this "re$view" "prevChildren" prev-children))))}
                 methods
                 {:life/should-update (fn [{:keys [view/props
                                                   view/prev-props
                                                   view/children
                                                   view/prev-children]}]
                                        (or (not= props prev-props)
                                            (not= children prev-children)))
                  :life/will-unmount  (fn [{:keys [view/state] :as this}]
                                        (aset this "unmounted" true)
                                        (when-let [un-sub (aget this "reactiveUnsubscribe")]
                                          (un-sub))
                                        (some-> state (remove-watch :state-changed)))
                  :life/did-update    (fn [this]
                                        (let [re$view (aget this "re$view")
                                              state (aget re$view "state")]
                                          (doto re$view
                                            (cond-> state (aset "prevState" @state))
                                            (aset "prevProps" (aget re$view "props")))))}])
       (reduce-kv (fn [m method-k method]
                    (assoc m method-k (wrap-methods method-k method))) {})))

(defn ensure-state [this]
  (when-not (gobj/containsKey (aget this "re$view") "state")
    (init-state this nil)))

(defn class-key [k]
  (and (keyword? k)
       (case (namespace k)
         "view" (v-util/camelCase k)
         "spec" (str "spec__" (v-util/camelCase k))
         nil)))

(defn specify-protocols
  "Implement ILookup protocol to read prop keys and `view`-namespaced keys on a component."
  [o]
  (specify! o
    ILookup
    (-lookup
      ([this k]
       (if-let [re-view-var (class-key k)]
         (do (when (= re-view-var "state") (ensure-state this))
             (aget this "re$view" re-view-var))
         (get (aget this "re$view" "props") k)))
      ([this k not-found]
       (if (or (contains? (aget this "re$view" "props") k)
               (.hasOwnProperty (aget this "re$view") (class-key k)))
         (get this k)
         not-found)))))

(defn swap-silently!
  "Swap a component's state atom without forcing an update (render)"
  [& args]
  (binding [*trigger-state-render* false]
    (apply swap! args)))

(defn- mock
  "Initialize an unmounted element, from which props and instance methods can be read."
  [element]
  (doto #js {}
    (init-props (aget element "props"))
    (specify-protocols)))

(defn element-get
  "'Get' from an unmounted element"
  [element k]
  (or (some-> (aget element "type") (aget (v-util/camelCase k)))
      (get (mock element) k)))

(defn element-constructor
  "Body of constructor function for ReView component."
  [this $props]
  (init-props this $props)
  (when-let [initial-state (aget this "$getInitialState")]
    (init-state this (cond-> initial-state
                             (fn? initial-state) (apply this (aget this "re$view" "children")))))
  this)

(specify-protocols (.-prototype js/React.Component))

(defn react-component
  "Extend React.Component with lifecycle methods of a view"
  [lifecycle-methods]
  (doto (fn ReView [$props]
          (element-constructor (js-this) $props))
    (aset "prototype" (->> lifecycle-methods
                           (reduce-kv (fn [m k v]
                                        (doto m (aset (get kmap k) v))) (new js/React.Component))))))

(defn factory
  "Return a function which returns a React element when called with props and children."
  [constructor class-keys instance-keys]
  (let [{{defaults :props/defaults
          :as      prop-spec} :spec/props
         children-spec        :spec/children
         :as                  class-keys} (-> class-keys
                                              (update :spec/props vspec/normalize-props-map)
                                              (update :spec/children vspec/resolve-spec-vector))
        class-keys (reduce-kv (fn [m k v]
                                (doto m (aset (class-key k) v))) #js {} class-keys)
        class-react-key (aget constructor "key")
        display-name (aget constructor "displayName")]
    (fn [props & children]
      (let [[props children] (cond (or (map? props)
                                       (nil? props)) [props children]
                                   (and (object? props)
                                        (not (.isValidElement js/React props))) [(js->clj props :keywordize-keys true) children]
                                   :else [nil (cons props children)])
            props (cond->> props defaults (merge defaults))
            key (or (get props :key)
                    (when class-react-key
                      (cond (string? class-react-key) class-react-key
                            (keyword? class-react-key) (get props class-react-key)
                            (fn? class-react-key) (apply class-react-key (assoc props :view/children children) children)
                            :else (throw (js/Error "Invalid key supplied to component"))))
                    display-name)]

        (when (true? INSTRUMENT!)
          (vspec/validate-props display-name prop-spec props)
          (vspec/validate-children display-name children-spec children))

        (.createElement js/React constructor #js {"key"      key
                                                  "ref"      (get props :ref)
                                                  "props"    (dissoc props :ref)
                                                  "children" children
                                                  "instance" instance-keys
                                                  "class"    class-keys})))))

(defn ^:export view*
  "Returns a React component factory for supplied lifecycle methods.
   Expects a single map of functions, or any number of key-function pairs,

   (component {:render (fn [this] [:div ...])})

   -or-

   (component

     :get-initial-state
     (fn [this] {:apple-state :ripe})

     :render
     (fn [this] [:div ...]))

   See other functions in this namespace for how to work with props and state.
   Result of :render function is automatically passed through hiccup/element,
   unless it is already a valid React element.
   "
  [{:keys [lifecycle-keys
           class-keys
           instance-keys
           react-keys] :as re-view-base}]
  (let [class (->> (wrap-lifecycle-methods lifecycle-keys)
                   (react-component))]
    (doseq [[k v] (seq react-keys)]
      (aset class (v-util/camelCase k) v))
    (-> (factory class class-keys instance-keys)
        (doto (aset "re$view$base" re-view-base)))))

(defn render-to-dom
  "Render view to element, which should be a DOM element or id of element on page."
  [component element]
  (.render js/ReactDOM component (cond->> element
                                          (string? element)
                                          (.getElementById js/document))))

(defn partial
  "Partially apply props and optional class-keys to base view. Props specified at runtime will overwrite those given here.
  `re$view$base` property is retained on preserved."
  ([base props]
   (-> (fn [& args]
         (let [[user-props & children] (cond->> args
                                                (not (map? (first args))) (cons {}))]
           (apply base (merge props user-props) children)))
       (doto (aset "re$view$base" (aget base "re$view$base")))))
  ([base base-overrides props]
   (partial (view* (merge-with merge (aget base "re$view$base") base-overrides)) props)))



(comment

  ;; example of component with controlled input

  (ns my-app.core
    (:require [re-view.core :refer [defview]]))

  (defview greeting
           {:life/initial-state {:first-name "Herbert"}}
           [{:keys [first-name view/state] :as this}]
           [:div
            [:p (str "Hello, " first-name "!")]
            [:input {:value     first-name
                     :on-change #(swap! state assoc :first-name (-> % .-target .-value))}]]))

(defn pass-props
  "Remove prop keys handled by component, useful for passing down unhandled props to a child component.
  By default, removes all keys listed in the component's :spec/props map. Set `:consume false` for props
  that should be passed through."
  [this]
  (apply dissoc (get this :view/props) (get-in this [:spec/props :props/consumed])))

(def is-react-element? v-util/is-react-element?)
