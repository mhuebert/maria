(ns chia.view.legacy
  (:require ["react" :as react]
            ["react-dom" :as react-dom]
            [applied-science.js-interop :as j]
            [chia.view.render-loop :as render-loop]
            [chia.reactive :as r]
            [cljs.spec.alpha :as s]
            [chia.view.legacy.view-specs]
            [chia.view.legacy.util :as class-util]
            [chia.util :as u])
  (:require-macros [chia.view.legacy :as class]))

(def Component react/Component)
(def -create-element react/createElement)

(def dom-node
  "Return DOM node for component"
  react-dom/findDOMNode)

(defn get-derived-state-from-props [props $state]
  ;; when a component receives new props, update internal state.
  (j/assoc! $state
            :prev-props (j/unchecked-get $state :props)
            :props (j/unchecked-get props :props)
            :prev-children (j/unchecked-get $state :children)
            :children (j/unchecked-get props :children)))

(defn- bind [f]
  (fn []
    (this-as ^js this
      (class/apply-fn f this))))


(def default-methods
  {:view/should-update
   (fn []
     (this-as this
       (let [$state (j/unchecked-get this :state)]
         (or (not= (j/unchecked-get $state :props)
                   (j/unchecked-get $state :prev-props))
             (not= (j/unchecked-get $state :children)
                   (j/unchecked-get $state :prev-children))
             (when-let [state (j/unchecked-get $state :state-atom)]
               (not= @state (j/unchecked-get $state :prev-state)))))))
   :static/get-derived-state-from-props get-derived-state-from-props
   :view/will-unmount
   (fn []
     (this-as this
       ;; manually track unmount state, react doesn't do this anymore,
       ;; otherwise our async render loop can't tell if a component is still on the page.

       (some-> (:view/state this)
               (remove-watch this))

       (doseq [f (some-> (j/unchecked-get this :chia$onUnmount)
                         (vals))]
         (when f (f this)))

       (r/dispose-reader! this)
       (render-loop/dequeue! this)))
   :view/did-update
   (fn []
     (this-as ^js this
       (let [$state (j/unchecked-get this :state)
             state-atom (j/unchecked-get $state :state-atom)]
         (-> $state
             (j/assoc! :prev-props (j/unchecked-get $state :props)
                       :prev-children (j/unchecked-get $state :children))
             (cond-> state-atom (j/assoc! :prev-state @state-atom))))))})



(defn wrap-method [k f]
  (if-not f
    (default-methods k)
    (case k
      (:view/should-update
       :view/will-receive-state) (bind f)
      :view/will-unmount
      (fn []
        (this-as this
          (class/apply-fn f this)
          (.call (default-methods :view/will-unmount) this)))
      :view/render
      (fn []
        (this-as this
          (j/assoc! this .-chia$toUpdate false)             ;; avoids double-render in render loop
          (r/with-dependency-tracking! {:reader this}
            (class/apply-fn f this))))
      :view/did-update
      (fn []
        (this-as this
          (class/apply-fn f this)
          (.call (default-methods :view/did-update) this)))
      :view/did-catch
      (fn [error info]
        (this-as this
          (f this error info)))
      :static/get-derived-state-from-props
      (fn [props state]
        (let [default (default-methods :static/get-derived-state-from-props)]
          (f props (default props state))))

      (if (fn? f)
        (case (namespace k)
          "view" (bind f)
          "static" f
          (fn [& args]
            (this-as this
              (apply f this args))))
        f))))

(defn- wrap-methods
  "Augment lifecycle methods with default behaviour."
  [methods required-keys]
  (->> (into required-keys (keys methods))
       (reduce (fn [obj k]
                 (j/assoc! obj
                           (or (class-util/lifecycle-keys k)
                               (u/camel-case (name k)))
                           (wrap-method k (get methods k)))) #js {})))

(defn- init-state!
  "Bind a component to an IWatchable/IDeref thing."
  [^js this watchable]
  (-> (j/unchecked-get this :state)
      (j/assoc! :state-atom watchable
                :prev-state @watchable))

  (add-watch watchable this
             (fn [_ _ old-state new-state]
               (when (not= old-state new-state)
                 (j/assoc-in! this [:state :prev-state] old-state)
                 (when-let [^js will-receive (j/unchecked-get this :componentWillReceiveState)]
                   (.call will-receive this))
                 (when (and (not r/*non-reactive*)
                            (if-let [^js should-update (j/unchecked-get this :shouldComponentUpdate)]
                              (.call should-update this)
                              true))
                   (render-loop/schedule-update! this)))))
  watchable)


(defn- populate-initial-state!
  "Populate initial state for `component`."
  [^js this ^js $props initial-state]
  (let [state-data (if (fn? initial-state)
                     (let [$state (j/unchecked-get this :state)]
                       (j/unchecked-set this :state (get-derived-state-from-props $props $state))
                       (apply initial-state this (:view/children this)))
                     initial-state)]
    (init-state! this (atom state-data)))
  this)


(defn- get-state!
  "Lazily create and bind a state atom for `component`"
  [this]
  (let [$state (j/get this :state)]
    (when-not (j/contains? $state :state-atom)
      (init-state! this (atom nil)))
    (j/unchecked-get $state :state-atom)))

(defn- get-special [this k not-found]
  (case k
    :view/state (get-state! this)
    :view/js-state (j/get this :state)
    (or (-> this
            (j/unchecked-get :state)
            (j/unchecked-get (name k)))
        not-found)))

(defn- get-prop [this k not-found]
  (get (get-special this :view/props nil) k not-found))

(def ChiaLegacyClass
  (specify! #js{}
    ILookup
    (-lookup
      ([this k]
       (-lookup this k nil))
      ([^js this k not-found]
       (case (namespace k)
         "view" (get-special this k not-found)
         (get-prop this k not-found))))
    r/IRecompute
    (-recompute! [this]
      (render-loop/schedule-update! this))
    INamed
    (-name [this] (j/unchecked-get this :displayName))
    (-namespace [this] nil)
    IPrintWithWriter
    (-pr-writer [this writer opts]
      (-write writer (str "👁[" (name this) "]")))))

(defn- ^:export extend-constructor
  [{:keys [lifecycle-keys
           static-keys

           react-keys
           unqualified-keys
           qualified-keys]} constructor]

  (j/extend! (.-prototype constructor)
             (.-prototype Component)
             ChiaLegacyClass
             (wrap-methods lifecycle-keys [:view/should-update
                                           :view/will-unmount
                                           :view/did-update]))

  (doto (.-prototype constructor)
    (j/assoc! "displayName" (.-displayName react-keys))
    (j/extend! unqualified-keys)
    (cond-> qualified-keys
            (j/assoc! "chia$class" qualified-keys)))

  (j/extend! constructor
             (wrap-methods static-keys [:static/get-derived-state-from-props])
             react-keys)

  constructor)



(defn validate-specs [{prop-spec     :spec/props
                       children-spec :spec/children} props children]
  (when js/goog.DEBUG
    (some-> prop-spec
            (s/explain-data props)
            (js/console.warn))
    (some-> children-spec
            (s/explain-data children)
            (js/console.warn))))

(defn- element-key [props children constructor]
  (str (or (get props :key)
           (when-let [class-react-key (j/get constructor :key)]
             (cond (string? class-react-key) class-react-key
                   (keyword? class-react-key) (get props class-react-key)
                   (fn? class-react-key) (.apply class-react-key (assoc props :view/children children) (to-array children))
                   :else (throw (js/Error "Invalid key supplied to component"))))
           (j/unchecked-get constructor :displayName))))

(defn- view*
  "Return a React element factory."
  [view-base constructor]
  (let [constructor (extend-constructor view-base constructor)]
    (doto (fn [props & children]
            (let [[{:as   props
                    :keys [ref]} children] (if (or (map? props)
                                                   (nil? props))
                                             [props children]
                                             [nil (cons props children)])]
              #_(validate-specs (:spec-keys view-base) props children)

              (-create-element constructor #js {"key"      (str (element-key props children constructor))
                                                "ref"      ref
                                                "props"    (some-> props
                                                                   (dissoc :ref))
                                                "children" children})))
      (j/assoc! :chia$constructor constructor))))

(defn component? [x]
  (identical? Component x))

(defn class-get
  "Get (qualified) keys from the view's methods map.

   Does not return lifecycle methods"
  ([this k] (class-get this k nil))
  ([^js this k not-found]
   (or (when this
         (when-let [class (or (j/get this :chia$class)
                              (j/get-in this [:chia$constructor
                                              :prototype
                                              :chia$class]))]
           (get class k)))
       not-found)))

;;;;;;;;;;;;;;;;;;;;;
;;
;; Contexts

(class/defclass context-observer
                {:view/should-update (constantly true)}
                [{:keys [view-fn
                         context-value]}]
                (view-fn context-value))

(defn consume*
  "Reads a React context value within component tree.

   `context` should be a keyword or React Context instance."
  [^js ctx f]
  (-> ctx
      (j/get :Consumer)
      (-create-element #js {} #(context-observer {:view-fn       f
                                                  :context-value %}))))