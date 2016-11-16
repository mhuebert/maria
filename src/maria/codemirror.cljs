(ns maria.codemirror
  (:require [cljsjs.codemirror]
            [cljsjs.codemirror.mode.clojure]
            [cljsjs.codemirror.addon.edit.closebrackets]
            [re-view.core :as v :refer-macros [defcomponent]]
            [clojure.string :as string]
            [maria.tree.core :as tree]
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

(defn get-cursor [editor]
  (let [cursor (.getCursor editor)]
    {:row (inc (.-line cursor))
     :col (.-ch cursor)}))

(def options
  {:theme                     "solarized light"
   :styleSelectedText         "cm-selected"
   :match-brackets            true
   :autoCloseBrackets         "()[]{}\"\""
   :highlightSelectionMatches true
   :lineNumbers               false
   :lineWrapping              true
   :styleActiveLine           true
   :mode                      "clojure"
   :keyMap                    "macDefault"})

(defn parse-range [{:keys [row col end-row end-col]}]
  [#js {:line (dec row) :ch col}
   #js {:line (dec end-row) :ch end-col}])

(defn match-brackets [cm node {:keys [edges handles]}]
  (let [next-edges (tree/edge-ranges node)]
    (when-not (= edges next-edges)
      (doseq [handle handles] (.clear handle))
      (when (tree/can-have-children? node)
        {:edges   next-edges
         :handles (doall (for [[from to] (map parse-range next-edges)]
                           (.markText cm from to #js {:className "CodeMirror-matchingbracket"})))}))))

(defn track-cursor
  [this cm]
  (let [{:keys [ast cursor-state]} (v/state this)
        node (tree/node-at ast (get-cursor cm))]
    (v/update-state! this assoc :cursor-state
                     (merge {:node node}
                            (match-brackets cm node cursor-state)))))

(defn highlight-cursor-form [cm e]
  (let [this (.-view cm)
        {{node :node edges :edges}          :cursor-state
         {prev-node :node handles :handles} :highlight-state} (v/state this)]
    (if (.-metaKey e)
      (when (and (not= node prev-node))
        (let [next-handles (doall (for [[from to] (map parse-range (if (not (tree/can-have-children? node))
                                                                        (list (update node :col dec))
                                                                        edges))]
                                    (.markText cm from to #js {:className "CodeMirror-cursor-form"})))]
          (v/update-state! this assoc :highlight-state
                           {:node    node
                            :handles next-handles})))
      (do (doseq [handle handles] (some-> handle (.clear)))
          (v/update-state! this dissoc :highlight-state)))))

(defcomponent editor
              :component-did-mount
              (fn [this {:keys [value read-only? on-mount] :as props}]
                (let [editor (js/CodeMirror (js/ReactDOM.findDOMNode (v/react-ref this "editor-container"))
                                            (clj->js (cond-> options
                                                             read-only? (-> (select-keys [:theme :mode :lineWrapping])
                                                                            (assoc :readOnly "nocursor")))))]
                  (set! (.-view editor) this)
                  (when-not read-only?

                    ;; event handlers are passed in as props with keys like :event/mousedown
                    (doseq [[event-key f] (filter (fn [[k v]] (= (namespace k) "event")) props)]
                      (.on editor (name event-key) f))

                    (.on editor "beforeChange" ignore-self-op)
                    (.on editor "cursorActivity" (partial track-cursor this))
                    (.on editor "change" #(v/update-state! this assoc :ast (tree/ast (.getValue %1))))

                    (v/update-state! this assoc :editor editor)

                    (when on-mount (on-mount editor this)))

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
