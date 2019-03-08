(ns prosemirror.tables
  (:require [goog.object :as gobj]
            [prosemirror.core :as pm]
            [clojure.string :as string]
            [applied-science.js-interop :as j]))

(defn add-schema-nodes [schema]
  (let [table-nodes {:table        {:toDOM   #(to-array ["table" 0])
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
    (pm/Schema. #js {:nodes (-> schema
                                (j/get-in [:spec :nodes])
                                (.append (clj->js table-nodes)))
                     :marks (j/get-in schema [:spec :marks])})))

(def table-nodes
  {:table             (fn [^js state node]
                        (.renderContent state node)
                        (.write state "\n"))
   :table_body        (fn [^js state node]
                        (.renderContent state node))
   :table_header      (fn [^js state node]
                        (.renderContent state node)
                        (let [columns (aget node "firstChild" "content" "childCount")]
                          ;; only take as many columns as are in the first row
                          (.write state
                                  (str "|" (string/join
                                             (take columns (repeat "---|")))
                                       "\n"))))
   :table_header_cell (fn [^js state node]
                        (.write state "| ")
                        (.renderInline state node)
                        (.write state " "))
   :table_row         (fn [^js state node]
                        (.renderContent state node)
                        (.write state "|\n"))
   :table_cell        (fn [^js state node]
                        (.write state "| ")
                        (.renderInline state node)
                        (.write state " "))})

(defn add-parser-nodes [parser schema MarkdownParser]
  (let [token->node {:table {:block "table"}
                     :thead {:block "table_header"}
                     :tbody {:block "table_body"}
                     :tr    {:block "table_row"}
                     :th    {:block "table_cell"
                             :attrs {:cellType "th"}}
                     :td    {:block "table_cell"}}]
    (MarkdownParser.
      schema
      (js/markdownit "default" #js {"html" false})
      (-> (j/get parser :tokens)
          (doto (gobj/extend (clj->js token->node)))))))