(ns maria.editor.code.docbar
  (:require ["@codemirror/view" :refer [ViewPlugin]]
            ["@codemirror/state" :refer [StateField]]
            ["@codemirror/autocomplete" :as autocomplete]
            [applied-science.js-interop :as j]
            [applied-science.js-interop.alpha :refer [js]]
            [maria.editor.code.commands :as commands]
            [maria.editor.code.repl :as repl]
            [maria.editor.util :as u]
            [maria.editor.views :as views]
            [maria.ui :as ui]
            [nextjournal.clojure-mode.node :as n]
            [re-db.reactive :as r]
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
  (defn extension [NodeView]
    [operator-field
     (.define ViewPlugin
              (fn [_]
                {:update (fn [{:as view-update :keys [view state]}]
                           (when (.-hasFocus view)
                             (let [ns (commands/code:ns NodeView)
                                   sym (or (-> (autocomplete/selectedCompletion state)
                                               (j/get :sym))
                                           (.field state operator-field))
                                   {:keys [!sci-ctx]} (.-ProseView NodeView)]
                               (assert !sci-ctx "eldoc extension requires sci context")
                               (reset! !state
                                       (when sym
                                         (repl/doc-map @!sci-ctx ns sym))))))}))]))

(ui/defview view []
  (let [{:sidebar/keys [visible? width]} @ui/!state]
    [:<>
     [:div {:style {:height 35}}]
     [:div.fixed.bottom-0.right-0
      {:style {:left (if visible? width 0)
               :transition ui/sidebar-transition}}
      (when-let [{:as m :keys [ns name doc arglists]} @!state]
        (v/x
          [:div.bg-stone-200.flex.items-center.px-4.font-mono.text-sm.gap-list.whitespace-nowrap.w-full
           {:class ["h-[35px]"
                    "border-t border-stone-300"]}
           [:div (views/show-sym ns name)]
           [:div (views/show-arglists arglists)]
           (when doc
             (views/doc-tooltip
               m
               (v/x [:div.bg-white.m-1.inline-block.py-1.px-2.rounded-md.text-xs.opacity-70.hover:opacity-100.truncated (u/truncate-segmented doc 20 "...")])))]))]]))