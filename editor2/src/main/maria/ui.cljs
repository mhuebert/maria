(ns maria.ui
  (:require [yawn.view :as v]))

(defn show-sym
  ([sym] (show-sym (namespace sym) (name sym)))
  ([ns name]
   (v/x (if ns
          [:<>
           [:span.text-slate-500 (str ns)]
           [:span.text-slate-500 "/"]
           [:span.text-slate-800 (str name)]]
          [:span.text-slate-800 (str name)]))))

(defn show-arglists [arglists]
  (v/x (into [:div.text-blue-500]
             (mapv str arglists))))

(defn show-doc [{:as var-meta :keys [ns name arglists doc]}]
  (v/x (into [:<>]
             (comp (keep identity)
                   (interpose [:div.mb-1]))
             [[show-sym ns name]
              (when arglists [show-arglists arglists])
              doc])))