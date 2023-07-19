(ns maria.editor.prosemirror.schema
  (:require ["prosemirror-markdown" :as md]))

(def schema md/schema)
(def parser md/defaultMarkdownParser)
(defn markdown->doc [source] (.parse parser source))
(defn doc->markdown [doc] (.serialize md/defaultMarkdownSerializer doc))