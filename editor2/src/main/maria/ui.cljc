(ns maria.ui
  (:require #?(:cljs ["prosemirror-keymap" :refer [keydownHandler]])
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