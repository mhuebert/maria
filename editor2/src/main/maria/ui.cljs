(ns maria.ui
  (:require ["@radix-ui/react-tooltip" :as Tooltip]
            [clojure.string :as str]
            [yawn.view :as v]))

(def tag-el :span.text-stone-500.text-xs.bg-stone-100.p-1.ml-1)

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
  (into [:<>] (map #(vector :span.text-blue-500 (str %))) arglists))

(defn show-doc [{:as var-meta :keys [ns name arglists doc macro special-form spec]}]
  (when var-meta
    (v/x
     (into [:<>]
           (comp (keep identity)
                 (interpose [:div.mb-1]))
           [[:div.gap-list.items-center
             [show-sym ns name]
             (first
              (for [tag [:macro :special-form :spec]
                    :when (tag var-meta)]
                [tag-el (clojure.core/name tag)]))]
            (when arglists [show-arglists arglists])
            (some-> doc (str/replace #"\n ( \S)" (fn [[_ x]] x)))]))))

(v/defview doc-tooltip [m trigger]
  (when m
    [:> Tooltip/Provider
     [:> Tooltip/Root {:delayDuration 300}
      [:> Tooltip/Trigger {:asChild true} trigger]
      [:> Tooltip/Portal
       [:> Tooltip/Content
        [:> Tooltip/Arrow {:class "fill-white"}]
        [:div.bg-white.rounded.shadow-md.p-3 {:class "max-w-[400px]"} (show-doc m)]]]]]))