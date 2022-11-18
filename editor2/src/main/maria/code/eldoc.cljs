(ns maria.code.eldoc
  (:require ["@codemirror/view" :refer [ViewPlugin]]
            ["@codemirror/state" :refer [StateField]]
            ["@codemirror/autocomplete" :as autocomplete]
            [applied-science.js-interop :as j]
            [nextjournal.clojure-mode.node :as n]
            [yawn.view :as v]
            [maria.repl.api :refer [resolve-symbol doc-map]]
            [maria.util :refer [use-watch]]
            [maria.code.commands :as commands]
            [maria.ui :as ui]))

(defn closest-operator [state node]
  (when-let [expr (n/closest node (fn [node]
                                    (and (= "List" (n/name node))
                                         (#{"Operator"
                                            "DefLike"
                                            "NS"} (some-> node n/down n/right n/name)))))]
    (->> expr
         n/down
         n/right
         (n/string state)
         symbol)))

(defn operator [^js state]
  (closest-operator state (n/tree state (.. state -selection -main -head))))

(defonce operator-field
         (.define StateField
                  #js{:create operator
                      :update (j/fn [_prev ^js {:keys [state]}]
                                (operator state))
                      :compare =}))

(defonce !current-operator (atom nil))

(defn extension [node-view]
  #js[operator-field
      (.define ViewPlugin
               (fn [_]
                 #js{:update (fn [^js view-update]
                               (when (.. view-update -view -hasFocus)
                                 (let [ns (commands/code:ns node-view)
                                       sym (or (j/get (autocomplete/selectedCompletion (.-state view-update)) :sym)
                                               (.. view-update -state (field operator-field)))]
                                   (reset! !current-operator (when sym
                                                               (try (doc-map ns sym)
                                                                    (catch js/Error e nil)))))))}))])

(v/defview view []
  (when-let [{:as m :keys [ns name doc arglists]} (use-watch !current-operator)]
    (ui/doc-tooltip m
      [:div.fixed.bottom-0.left-0.right-0.bg-stone-200.flex.items-center.px-4.font-mono.text-sm.gap-list.whitespace-nowrap.w-full
       {:class ["h-[35px]"
                "border-t border-stone-300"]}
       [:div (ui/show-sym ns name)]
       [:div (ui/show-arglists arglists)]
       [:div.truncate doc]])))