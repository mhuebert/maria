(ns maria.code.eldoc
  (:require ["@codemirror/view" :refer [ViewPlugin]]
            ["@codemirror/state" :refer [StateField]]
            [applied-science.js-interop :as j]
            [nextjournal.clojure-mode.node :as n]
            [yawn.view :as v]
            [maria.repl.api :refer [resolve-symbol]]
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
                                       sym (.. view-update -state (field operator-field))]
                                   (reset! !current-operator (when sym
                                                               (try (resolve-symbol ns sym)
                                                                    (catch js/Error e nil)))))))}))])

(v/defview view []
  (when-let [{:keys [ns name doc arglists]} (meta (use-watch !current-operator))]
    [:div.fixed.bottom-0.left-0.right-0.bg-stone-200.flex.items-center.px-4.font-mono.text-sm
     {:class ["h-[35px]"
              "border-t border-stone-300"]}
     (ui/show-sym ns name)
     [:div.ml-2]
     (ui/show-arglists arglists)
     [:div.ml-2]
     [:div.truncate doc]]))