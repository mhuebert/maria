(ns maria.tree.to-string)

(declare to-string)

(defn wrap-children [left right children]
  (str left (apply str (map to-string children)) right))

(defn to-string [{:keys [tag children string value] :as node}]
  (case tag
    (:token :space :linebreak :comma) string
    :symbol value
    :vector (wrap-children \[ \] children)
    :list (wrap-children \( \) children)
    :map (wrap-children \{ \} children)
    :set (wrap-children "#{" \} children)
    :string (str \" value \")
    :meta (#(apply str (cons "^" (map to-string value))))
    :keyword (str (cond->> value
                           (:namespaced? node) (str ":")))
    nil ""
    ))



;meta
#_(let [[mta data] (node/sexprs children)]
    (assert (instance? clojure.lang.IMeta data)
            (str "cannot attach metadata to: " (pr-str data)))
    (with-meta data (if (map? mta) mta {mta true})))
