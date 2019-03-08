(ns prosemirror.markdown
  (:require [chia.view :as v]
            [prosemirror.base :as base]
            [prosemirror.tables :as tables]
            [prosemirror.core :as pm]
            [goog.object :as gobj]
            ["prosemirror-markdown" :as pm-md]
            [chia.view.props :as props]))

(def ^:dynamic *tables?* false)
(def ^:dynamic *fenced-code-blocks?* true)

(def markdown-schema pm-md/schema)
(def defaultMarkdownSerializer pm-md/defaultMarkdownSerializer)
(def defaultMarkdownParser pm-md/defaultMarkdownParser)
(def MarkdownSerializerState pm-md/MarkdownSerializerState)

(def default-serializer-nodes (gobj/get defaultMarkdownSerializer "nodes"))
(def default-serializer-marks (gobj/get defaultMarkdownSerializer "marks"))

(defn patch-state
  "Patch markdown serializer state to emit tight lists."
  [st]
  st
  #_(let [render-list (.-renderList st)]
      (aset st "renderList" (fn [node delim first-delim]
                              (aset node "attrs" #js {:tight true})
                              (this-as this
                                (.apply render-list this (js-arguments)))))
      st))

(defn MarkdownSerializer [nodes marks]
  #js {:serialize (fn [content]
                    (let [state (patch-state (MarkdownSerializerState.
                                               (doto default-serializer-nodes (gobj/extend (clj->js (or nodes #js {}))))
                                               (doto default-serializer-marks (gobj/extend (clj->js (or marks #js {})))) nil))]
                      (.renderContent state content)
                      (.-out state)))})

(def fenced-code-nodes {:code_block  (fn [state node]
                                       (.write state (str "```" (.-params (.-attrs node)) "\n"))
                                       (.text state (.-textContent node) false)
                                       (.ensureNewLine state)
                                       (.write state "```")
                                       (.closeBlock state node))
                        :bullet_list (fn [state node]
                                       (.renderList state node "    " (fn []
                                                                        (str (or (.. node -attrs -bullet) "*") " "))))})


(def schema (-> markdown-schema
                (cond-> *tables?* (tables/add-schema-nodes))
                ;; WARNING: this is a brittle patch that will become redundant after the upstream bug is fixed.
                (doto (aset "marks" "code" "isCode" true))))

(def serializer (MarkdownSerializer (merge {}
                                           (when *tables?*
                                             tables/table-nodes)
                                           (when *fenced-code-blocks?*
                                             fenced-code-nodes)) nil))

(defn serialize-selection [selection]
  (let [fragment (.-content (.content selection))]
    (.serialize serializer fragment)))

(def parser (cond-> defaultMarkdownParser
                    *tables?* (tables/add-parser-nodes schema pm-md/MarkdownParser)))

(def Editor (props/partial-props base/Editor
                                 {:serialize #(.serialize serializer %)
                                  :parse     #(.parse parser %)
                                  :schema    schema}))