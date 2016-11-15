(ns maria.tree.to-string)

(declare to-string)

(defn wrap-children [left right children]
  (str left (apply str (map to-string children)) right))

(defn to-string [{:keys [tag children string value prefix] :as node}]
  (case tag
    (:token :space :newline :comma) string
    :symbol (or string value)
    :vector (wrap-children \[ \] children)
    :list (wrap-children \( \) children)
    :fn (wrap-children "#(" \) children)
    :map (wrap-children \{ \} children)
    :set (wrap-children "#{" \} children)
    :string (str \" value \")
    :regex (str \# \" value \")
    :meta (apply str (cons prefix (map to-string children)))
    :keyword (str (cond->> value
                           (:namespaced? node) (str ":")))
    :base (apply str (map to-string children))
    :reader-macro (str \# (apply str (map to-string children)))
    :var (str "#'" (to-string (first children)))
    :uneval (str "#_" (to-string (first children)))
    nil ""
    ))

(defn to-sexp [{:keys [tag children string value prefix] :as node}]
  (case tag
    :var (list 'var value)))



;meta
#_(let [[mta data] (node/sexprs children)]
    (assert (instance? clojure.lang.IMeta data)
            (str "cannot attach metadata to: " (pr-str data)))
    (with-meta data (if (map? mta) mta {mta true})))
