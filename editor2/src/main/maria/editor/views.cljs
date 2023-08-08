(ns maria.editor.views
  (:require ["@radix-ui/react-tooltip" :as Tooltip]
            ["@radix-ui/react-hover-card" :as HoverCard]
            [clojure.string :as str]
            [maria.cloud.markdown :as markdown]
            [maria.ui :as ui]
            [re-db.react]
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
            (some-> doc
                    (str/replace #"\n ( \S)" (fn [[_ x]] x))
                    markdown/show-markdown)]))))

(ui/defview doc-tooltip [m trigger]
  (when m
    [:> HoverCard/Root {:openDelay 0
                        :closeDelay 100}
     [:> HoverCard/Trigger {:asChild true} trigger]
     [:el HoverCard/Portal
      [:el.z-50.relative.overflow-y-auto.bg-white.rounded.shadow-lg.p-4 HoverCard/Content
       {:avoid-collisions true
        :collision-padding 10
        :style {:max-height "var(--radix-hover-card-content-available-height)"
                :max-width "var(--radix-hover-card-content-available-width)"}}
       [:> HoverCard/Arrow {:class "fill-white"}]
       [:div #_{:class "max-w-[600px]"}
        (show-doc m)]]]]))