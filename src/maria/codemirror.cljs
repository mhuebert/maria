(ns maria.codemirror
  (:require [cljsjs.codemirror]
            [cljsjs.codemirror.mode.clojure]
            [maria.codemirror.matchbrackets]
            [cljsjs.codemirror.addon.edit.closebrackets]
            [re-view.core :as v :refer-macros [defcomponent]]
            [clojure.string :as string]))

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

(def options
  {:theme                     "solarized light"
   :styleSelectedText         "cm-selected"
   :matchBrackets             true
   :autoCloseBrackets         "()[]{}\"\""
   :highlightSelectionMatches true
   :lineNumbers               false
   :lineWrapping              true
   :styleActiveLine           true
   :mode                      "clojure"
   :keyMap                    "macDefault"})

(defcomponent editor
              :component-did-mount
              (fn [this {:keys [value read-only? on-mount] :as props}]
                (let [editor (js/CodeMirror (js/ReactDOM.findDOMNode (v/react-ref this "editor-container"))
                                            (clj->js (cond-> options
                                                             read-only? (-> (select-keys [:theme :mode :lineWrapping])
                                                                            (assoc :readOnly "nocursor")))))]
                  (when value (.setValue editor (str value)))

                  (when-not read-only?

                    ;; event handlers are passed in as props with keys like :event/mousedown
                    (doseq [[event-key f] (filter (fn [[k v]] (= (namespace k) "event")) props)]
                      (.on editor (name event-key) f))

                    (.on editor "beforeChange" ignore-self-op)

                    (v/update-state! this assoc :editor editor)

                    (when on-mount (on-mount editor this)))))
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

(defn ch+ [pos n]
  #js {:line (.-line pos)
       :ch   (+ (.-ch pos) n)})

(defn bracket-text
  "Text within nearest bracket range"
  [cm]
  (apply str (for [selection (.listSelections cm)
                   :let [cursor (.-head selection)
                         bracket (.findMatchingBracket cm cursor)]
                   :when bracket]
               (let [[from to] (sort-by (fn [pos] [(.-line pos) (.-ch pos)])
                                        [(.-from bracket) (.-to bracket)])
                     token (.getTokenAt cm from)
                     from (cond-> from
                                  (or (#{\' \`} (.-string token))
                                      (string/starts-with? (.-string token) "#"))
                                  (ch+ (- (count (.-string token)))))]
                 (.getRange cm from (ch+ to 1))))))

(defn selection-text [cm]
  (when (.somethingSelected cm)
    (.getSelection cm)))