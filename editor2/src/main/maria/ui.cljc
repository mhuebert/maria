(ns maria.ui
  (:require #?(:cljs ["prosemirror-keymap" :refer [keydownHandler]])
            [clojure.pprint]
            [yawn.view :as v]
            [re-db.fast :as fast]
            [re-db.hooks :as hooks]
            [re-db.memo :as memo]
            [re-db.react]
            [re-db.reactive :as r]
            [re-db.xform :as xf])
  #?(:cljs (:require-macros maria.ui)))

(defmacro defview [name & args]
  (v/defview:impl {:wrap-expr (fn [expr] `(re-db.react/use-derefs ~expr))} name args))

(defmacro use-> [ref & forms]
  `(let [ref# ~ref]
     (hooks/use-deref
      (hooks/use-memo
       (fn []
         (xf/map #(-> % ~@forms) ref#))))))

(memo/defn-memo $gets [ref & ks]
  (r/reaction (apply fast/gets (hooks/use-deref ref) ks)))

#?(:cljs
   (defn use-global-keymap [bindings]
     (hooks/use-effect
      (fn []
        (let [on-keydown (let [handler (keydownHandler (clj->js bindings))]
                           (fn [event]
                             (handler #js{} event)))]
          (.addEventListener js/window "keydown" on-keydown)
          #(.removeEventListener js/window "keydown" on-keydown))))))

(defmacro pprinted [x]
  `(with-out-str (~(if (:ns &env) 'cljs.pprint/pprint 'clojure.pprint/pprint) ~x)))


(r/redef !sidebar-state (r/atom {:visible? true
                                 :width 250
                                 :transition "all 0.2s ease 0s"}))

(maria.ui/defview with-sidebar [sidebar content]
  (let [{:keys [visible? width transition]} (hooks/use-deref !sidebar-state)]
    [:div
     {:style {:padding-left (if visible? width 0)
              :transition transition}}
     [:div.fixed.top-0.bottom-0.bg-white.rounded.z-10.drop-shadow-md.divide-y.overflow-hidden
      {:style {:width width
               :transition transition
               :left (if visible? 0 (- width))}}
      sidebar]
     content]))