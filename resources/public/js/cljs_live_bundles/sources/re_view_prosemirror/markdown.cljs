(ns re-view-prosemirror.markdown
  (:require [re-view.core :as v]
            [goog.object :as gobj]
            [pack.prosemirror-markdown]
            [re-view-prosemirror.base :as base]
            [clojure.string :as string]))

(def pmMarkdown (.-pmMarkdown js/window))
(def Schema (aget js/pm "Schema"))
(def base-schema (gobj/get pmMarkdown "schema"))
(def schema (let [table-nodes {:table        {:toDOM   #(to-array ["table" 0])
                                              :content "table_header table_body"
                                              :group   "block"}
                               :table_header {:toDOM   #(to-array ["thead" 0])
                                              :content "table_row"}
                               :table_body   {:toDOM   #(to-array ["tbody" 0])
                                              :content "table_row+"}
                               :table_row    {:content  "table_cell+"
                                              :toDOM    #(to-array ["tr" 0])
                                              :tableRow true}
                               :table_cell   {:attrs     {:cellType {:default "td"}}
                                              :toDOM     #(to-array [(aget % "attrs" "cellType") 0])
                                              :content   "inline<_>*"
                                              :isolating true}}]
              (Schema. #js {:nodes (-> base-schema
                                       (gobj/getValueByKeys "spec" "nodes")
                                       (.append (clj->js table-nodes)))
                            :marks (aget base-schema "spec" "marks")})))

(def ^js/pmMarkdown.MarkdownSerializer serializer
  (let [MarkdownSerializer (.-MarkdownSerializer pmMarkdown)
        nodes (.. pmMarkdown -defaultMarkdownSerializer -nodes)
        marks (.. pmMarkdown -defaultMarkdownSerializer -marks)]
    (MarkdownSerializer. (doto nodes
                           (gobj/extend
                             (clj->js {:table             (fn [^js/pmMarkdown.MarkdownSerializerState state node]
                                                            (.renderContent state node)
                                                            (.write state "\n"))
                                       :table_body        (fn [^js/pmMarkdown.MarkdownSerializerState state node]
                                                            (.renderContent state node))
                                       :table_header      (fn [^js/pmMarkdown.MarkdownSerializerState state node]
                                                            (.renderContent state node)
                                                            (let [columns (aget node "firstChild" "content" "childCount")]
                                                              ;; only take as many columns as are in the first row
                                                              (.write state
                                                                      (str "|" (string/join
                                                                                 (take columns (repeat "---|")))
                                                                           "\n"))))
                                       :table_header_cell (fn [^js/pmMarkdown.MarkdownSerializerState state node]
                                                            (.write state "| ")
                                                            (.renderInline state node)
                                                            (.write state " "))
                                       :table_row         (fn [^js/pmMarkdown.MarkdownSerializerState state node]
                                                            (.renderContent state node)
                                                            (.write state "|\n"))
                                       :table_cell        (fn [^js/pmMarkdown.MarkdownSerializerState state node]
                                                            (.write state "| ")
                                                            (.renderInline state node)
                                                            (.write state " "))}))) marks)))

(def ^js/pmMarkdown.MarkdownParser parser
  (let [MarkdownParser (.-MarkdownParser pmMarkdown)
        token->node {:table {:block "table"}
                     :thead {:block "table_header"}
                     :tbody {:block "table_body"}
                     :tr    {:block "table_row"}
                     :th    {:block "table_cell"
                             :attrs {:cellType "th"}}
                     :td    {:block "table_cell"}}]
    (MarkdownParser.
      schema (js/markdownit "default" #js {"html" false})
      (-> (.-defaultMarkdownParser pmMarkdown)
          (.-tokens)
          (doto (gobj/extend (clj->js token->node)))))))

(def Editor (v/partial base/Editor {:serialize #(.serialize serializer %)
                                    :parse     #(.parse parser %)
                                    :schema    schema}))