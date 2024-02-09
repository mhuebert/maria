(ns maria.editor.prosemirror.schema
  (:require [applied-science.js-interop :as j]
            [applied-science.js-interop.alpha :refer [js]]
            ["prosemirror-markdown" :as md]
            [clojure.string :as str]))

(def schema md/schema)

(def parser md/defaultMarkdownParser)

(defn markdown->doc [source] (.parse parser source))

(defn clojure-block? [lang]
  (re-matches #"(?i)clj\w?|clojure|clojurescript" lang))

(def code-prefix "¡Ⓜ")

(defn prefix-lines [s]
  (str code-prefix
       (str/replace-all s #"[\n\r]" (str "\n" code-prefix))))

(def serializer (js
                  (let [{:keys [nodes marks]
                         {original :code_block} :nodes} md/defaultMarkdownSerializer]
                    (new md/MarkdownSerializer
                         (j/extend! {}
                                    nodes
                                    {:code_block
                                     (fn [{:as state :keys [delim]}
                                          {:as node :keys [textContent] {lang :params} :attrs}]
                                       (if (and (str/blank? delim)
                                                (or (clojure-block? lang)
                                                    (str/blank? lang)))
                                         (do
                                           (when-not (str/blank? textContent)
                                             (.text state (-> textContent str/trim prefix-lines) false))
                                           (.closeBlock state node))
                                         (original state node)))})
                         marks))))

(defn serialize [x] (.serialize ^js serializer x #js{:tightLists true}))

(defn doc->markdown [doc] (.serialize md/defaultMarkdownSerializer doc #js{:tightLists true}))