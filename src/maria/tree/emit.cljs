(ns maria.tree.emit
  (:refer-clojure :exclude [*ns*])
  (:require [cljs.tools.reader.edn :as edn]
            [cljs.tools.reader :as r]
            [fast-zip.core :as z]
            [maria.tree.fn :refer [fn-walk]])
  (:require-macros [maria.tree.backtick :refer [template]]))

(def ^:dynamic *ns* (symbol "maria.user"))

(declare string)

(def edges
  {:deref            ["@"]
   :list             [\( \)]
   :fn               ["#(" \)]
   :map              [\{ \}]
   :meta             ["^"]
   :quote            ["'"]
   :reader-meta      ["#^"]
   :raw-meta         ["^"]
   :reader-macro     ["#"]
   :regex            ["#\"" \"]
   :set              ["#{" \}]
   :string           [\" \"]
   :syntax-quote     ["`"]
   :unquote          ["~"]
   :unquote-splicing ["~@"]
   :uneval           ["#_"]
   :var              ["#'"]
   :vector           [\[ \]]
   :comment          [";"]})

(def printable-only? #{:comment :uneval :space :newline :comma})

(defn wrap-children [left right children]
  (str left (apply str (map string children)) right))

#_(defn children? [{:keys [tag]}]
    (#{:list :fn :map :meta :set :vector :uneval} tag))

(defn string [node]
  (when-not (nil? node)
    (if (map? node)
      (let [{:keys [tag value options]} node
            [lbracket rbracket] (get edges tag [])]
        (case tag
          :base (apply str (map string value))
          (:token :space :newline :comma) value
          (:deref
            :fn
            :list
            :map
            :quote
            :reader-macro
            :set
            :syntax-quote
            :uneval
            :unquote
            :unquote-splicing
            :var
            :vector) (wrap-children lbracket rbracket value)
          (:meta :reader-meta) (str (:prefix options) (wrap-children lbracket rbracket value))
          (:string
            :regex
            :comment) (str lbracket value rbracket)
          :keyword value
          :namespaced-keyword (str ":" value)
          nil ""))
      (string (z/node node)))))

(declare sexp)

(defn as-code [forms]
  (map sexp (filter #(not (printable-only? (:tag %))) forms)))

(defn sexp [{:keys [tag value options] :as node}]
  (when node
    (case tag
      :base (first (as-code value))

      (:space
        :newline
        :comma) nil

      :string value
      :deref (template (deref ~(first (as-code value))))

      :token (edn/read-string value)
      :vector (vec (as-code value))
      :list (list* (as-code value))
      :fn (fn-walk (as-code value))
      :map (apply hash-map (as-code value))
      :set (template #{~@(as-code value)})
      :var (template #'~(first (as-code value)))
      (:quote :syntax-quote) (template (quote ~(first (as-code value))))
      :unquote (template (~'clojure.core/unquote ~(first (as-code value))))
      :unquote-splicing (template (~'clojure.core/unquote-splicing ~(first (as-code value))))
      :reader-macro (r/read-string (string node))
      (:meta
        :reader-meta) (let [[m data] (as-code value)]
                        (with-meta data (if (map? m) m {m true})))
      :regex (re-pattern value)
      :namespaced-keyword (keyword *ns* (name value))
      :keyword value

      (:comment
        :uneval) nil)))
