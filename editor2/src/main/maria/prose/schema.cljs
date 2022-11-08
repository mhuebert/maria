(ns maria.prose.schema
  (:require ["prosemirror-markdown" :as md]))

(def schema md/schema)
(def parser md/defaultMarkdownParser)
(defn md->doc [source] (.parse parser source))
(defn doc->md [doc] (.serialize md/defaultMarkdownSerializer doc))