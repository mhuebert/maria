(ns maria.views.repl-specials
  (:require [re-view.core :as v :refer [defview]]
            [maria.live.ns-utils :as ns-utils]
            [maria.views.icons :as icons]
            [clojure.string :as string]
            [maria.live.source-lookups :as reader]
            [maria.views.cards :as repl-ui]
            [maria.editors.code :as code]
            [maria.util :as util]))

(defn docs-link [namespace name]
  (when (re-find #"^(cljs|clojure)\.core(\$macros)?$" namespace)
    [:.mv2
     [:a.f7.black {:href   (str "https://clojuredocs.org/clojure.core/" (util/cd-encode name))
                   :target "_blank"
                   :rel    "noopener noreferrer"} "clojuredocs ↗"]]))

(defview doc
  {:view/state #(atom (:expanded? %))
   :key        :name}
  [{:keys [doc
           meta
           arglists
           forms
           view/state
           view/props
           standalone?] :as this}]
  (let [[namespace name] [(namespace (:name this)) (name (:name this))]
        arglists (ns-utils/elide-quote (or forms
                                           (:arglists meta)
                                           arglists))]
    [:.ws-normal
     {:class (when standalone? repl-ui/card-classes)}
     [:.code.flex.items-center.pointer.mv1.hover-opacity-parent.pl3
      {:on-click #(swap! state not)}
      (when standalone?
        [:span.o-60 namespace "/"])
      name
      [:.flex-auto]
      [:span.o-50.hover-opacity-child
       (when (or doc (seq arglists))
         (-> icons/ArrowPointingDown
             (icons/style {:transition "all ease 0.2s"
                           :transform (when-not @state "rotate(90deg)")})))]]
     (when @state
       [:.ph3
        [:.mv1.blue.f6 (string/join ", " (map str arglists))]
        [:.gray.mv2.f6 doc]
        (docs-link namespace name)])]))

(defview var-source
  {:view/will-mount (fn [{:keys [view/props view/state]}]
                      (reader/var-source props (partial reset! state)))}
  [{:keys [view/state special-form name]}]
  (let [{:keys [value error] :as result} @state]
    (cond (nil? result) [:.pa2 "Loading..."]
          error [:.ma3 (if special-form
                         (str "Source code is not available. (`" name "` is a special form, not written in Clojure.)")
                         error)]
          value (code/viewer value))))

(defview dir
  {:view/initial-state {:expanded? false}}
  [{:keys [view/state]} c-state ns]
  (let [defs (->> (:defs (ns-utils/analyzer-ns c-state ns))
                  (seq)
                  (sort)
                  (map second)
                  (filter #(not (:private (:meta %)))))
        c (count defs)
        limit 10
        {:keys [expanded?]} @state]
    [:.sans-serif
     {:class repl-ui/card-classes}
     [:.b.pv2.ph3.f5 (str ns)]
     (map doc (cond->> defs
                       (not expanded?) (take limit)))
     (when (and (not expanded?) (> c limit))
       [:.o-50.pv2.flex.items-center.ph3.pointer.bt.b--near-white
        {:on-click #(swap! state assoc :expanded? true)}
        [:span "Show All (" (- c limit) " more)"]
        [:.flex-auto] icons/ExpandMore])]))

