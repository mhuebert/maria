(ns maria.editor.code-blocks.docbar
  (:require ["@codemirror/view" :refer [ViewPlugin]]
            ["@codemirror/state" :refer [StateField]]
            ["@codemirror/autocomplete" :as autocomplete]
            [applied-science.js-interop.alpha :refer [js]]
            [applied-science.js-interop :as j]
            [maria.editor.code-blocks.commands :as commands]
            [maria.editor.code-blocks.repl :as repl]
            [maria.editor.views :as views]
            [maria.ui :as ui]
            [maria.ui :refer [defview]]
            [nextjournal.clojure-mode.node :as n]
            [re-db.reactive :as r]
            [yawn.hooks :as h]
            [yawn.view :as v]))

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

(defonce !state (r/atom nil))

(js
  (defn extension [node-view]
    [operator-field
     (.define ViewPlugin
              (fn [_]
                {:update (fn [{:as view-update :keys [view state]}]
                           (when (.-hasFocus view)
                             (let [ns (commands/code:ns node-view)
                                   sym (or (-> (autocomplete/selectedCompletion state)
                                               (j/get :sym))
                                           (.field state operator-field))
                                   {:keys [!sci-ctx]} (.-proseView node-view)]
                               (assert !sci-ctx "eldoc extension requires sci context")
                               (reset! !state
                                       (when sym
                                         (try (repl/doc-map @!sci-ctx ns sym)
                                              (catch js/Error e nil)))))))}))]))

(defview view []
  (let [sidebar (h/use-deref ui/!sidebar-state)]
    [:div.fixed.bottom-0.right-0
     {:style {:left (if (:visible? sidebar) (:width sidebar) 0)
              :transition (:transition sidebar)}}
     (when-let [{:as m :keys [ns name doc arglists]} @!state]
       (views/doc-tooltip m
                          (v/x
                           [:div.bg-stone-200.flex.items-center.px-4.font-mono.text-sm.gap-list.whitespace-nowrap.w-full
                            {:class ["h-[35px]"
                                     "border-t border-stone-300"]}
                            [:div (views/show-sym ns name)]
                            [:div (views/show-arglists arglists)]
                            [:div.truncate doc]])))]))