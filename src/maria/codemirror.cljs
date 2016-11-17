(ns maria.codemirror
  (:require [cljsjs.codemirror]
            [cljsjs.codemirror.mode.clojure]
            [cljsjs.codemirror.addon.edit.closebrackets]
            [re-view.core :as v :refer-macros [defcomponent]]
            [clojure.string :as string]
            [maria.tree.core :as tree]
            [goog.events :as events]
            [cljs.pprint :refer [pprint]]))

(def ^:dynamic *self-op*)

(defn ignore-self-op
  "Editor should not fire 'change' events for self-inflicted operations."
  [cm change]
  (when *self-op*
    (.cancel change)))

(defn set-preserve-cursor [editor value]
  (let [cursor-pos (.getCursor editor)]
    (.setValue editor (str value))
    (if (-> editor (aget "state" "focused"))
      (.setCursor editor cursor-pos))))

(defn get-cursor-pos [editor]
  (let [cm-pos (.getCursor editor)]
    {:row (inc (.-line cm-pos))
     :col (.-ch cm-pos)}))

(defn get-mouse-pos [editor e]
  (let [cm-pos (.coordsChar editor #js {:left (.-clientX e)
                                        :top  (.-clientY e)})]
    {:row (inc (.-line cm-pos))
     :col (.-ch cm-pos)}))

(def options
  {:theme                     "solarized light"
   :autoCloseBrackets         "()[]{}\"\""
   :lineNumbers               false
   :lineWrapping              true
   :mode                      "clojure"
   :keyMap                    "macDefault"})

(defn parse-range [{:keys [row col end-row end-col]}]
  [#js {:line (dec row) :ch col}
   #js {:line (dec end-row) :ch end-col}])

(defn highlights [node]
  (if (tree/can-have-children? node)
    (tree/edge-ranges node)
    [(update node :col dec)]))

(defn mark-ranges! [cm ranges payload]
  (doall (for [[from to] (map parse-range ranges)]
           (.markText cm from to payload))))

(defn clear-brackets! [this]
  (doseq [handle (get-in (v/state this) [:cursor-state :handles])]
    (.clear handle))
  (v/update-state! this update :cursor-state dissoc :handles))

(defn match-brackets! [this cm node]
  (let [prev-node (get-in (v/state this) [:cursor-state :node])]
    (when (not= prev-node node)
      (clear-brackets! this)
      (when (tree/can-have-children? node)
        (v/update-state! this assoc-in [:cursor-state :handles]
                         (mark-ranges! cm (highlights node) #js {:className "CodeMirror-matchingbracket"}))))))

(defn clear-highlight! [this]
  (doseq [handle (get-in (v/state this) [:highlight-state :handles])]
    (.clear handle))
  (v/update-state! this dissoc :highlight-state))

(defn highlight-node! [this cm node]
  (when (and (not= node (get-in (v/state this) [:highlight-state :node]))
             (not (.somethingSelected cm))
             (tree/sexp? node))
    (clear-highlight! this)
    (v/update-state! this assoc :highlight-state
                     {:node    node
                      :handles (mark-ranges! cm (highlights node) #js {:className "CodeMirror-eval-highlight"})})))

(defn update-highlights [cm e]
  (let [this (.-view cm)
        {{cursor-node :node} :cursor-state
         ast                 :ast
         :as                 state} (v/state this)]
    (case [(.-type e) (= 91 (.-which e)) (.-metaKey e)]
      ["mousemove" false true] (highlight-node! this cm (->> (get-mouse-pos cm e)
                                                             (tree/node-at ast)))
      ["keyup" true false] (clear-highlight! this)
      ["keydown" true true] (highlight-node! this cm cursor-node)
      nil)))

(defn update-cursor
  [this cm]
  (let [{:keys [ast]} (v/state this)
        node (tree/node-at ast (get-cursor-pos cm))]
    (match-brackets! this cm node)
    (v/update-state! this assoc-in [:cursor-state :node] node)))

(defn update-ast
  [cm]
  (when-let [ast (try (tree/ast (.getValue cm))
                      (catch js/Error e (.debug js/console e)))]
    (v/update-state! (.-view cm) assoc :ast ast)))

(defcomponent editor
  :component-did-mount
  (fn [this {:keys [value read-only? on-mount] :as props}]
    (let [dom-node (js/ReactDOM.findDOMNode (v/react-ref this "editor-container"))
          editor (js/CodeMirror dom-node
                                (clj->js (cond-> options
                                                 read-only? (-> (select-keys [:theme :mode :lineWrapping])
                                                                (assoc :readOnly "nocursor")))))]
      (set! (.-view editor) this)
      (when-not read-only?

        ;; event handlers are passed in as props with keys like :event/mousedown
        (doseq [[event-key f] (filter (fn [[k v]] (= (namespace k) "event")) props)]
          (let [event-key (name event-key)]
            (if (#{"mousedown" "click" "mouseup"} event-key)
              ;; use goog.events to attach mouse handlers to dom node at capture phase
              ;; (which lets us stopPropagation and prevent CodeMirror selections)
              (events/listen dom-node event-key f true)
              (.on editor event-key f))))

        (.on editor "beforeChange" ignore-self-op)
        (.on editor "cursorActivity" (partial update-cursor this))
        (.on editor "change" update-ast)

        (v/update-state! this assoc :editor editor)


        (when on-mount (on-mount editor this)))
      (aset js/window "cm" editor)
      (when value (.setValue editor (str value)))))
  :component-will-receive-props
  (fn [this {:keys [value]} {next-value :value}]
    (when (and next-value (not= next-value value))
      (when-let [editor (:editor (v/state this))]
        (binding [*self-op* true]
          (set-preserve-cursor editor next-value)))))
  :should-component-update
  (fn [_ _ state _ prev-state]
    (not= (dissoc state :editor) (dissoc prev-state :editor)))
  :render
  (fn [this props state]
    [:.h-100 {:ref "editor-container"}]))

(defn viewer [source]
  (editor {:read-only? true
           :value      source}))

(defn selection-text [cm]
  (when (.somethingSelected cm)
    (.getSelection cm)))
