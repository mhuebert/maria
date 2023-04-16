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
  #?(:cljs (:require-macros [maria.ui :as ui])))

(r/redef !state (r/atom {:sidebar/visible? false
                         :sidebar/width 250
                         :sidebar/transition "all 0.2s ease 0s"}))

(defmacro x [& args] `(v/x (do ~@args)))

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

(def c:divider "border-b-2 border-zinc-100")
(def c:button-med (str "bg-zinc-300 hover:bg-zinc-400 active:bg-zinc-500 "
                       "font-sans font-bolxd no-underline "
                       "text-zinc-800 visited:text-zinc-800 hover:text-zinc-800 "
                       #_"text-white visited:text-white hover:text-white "
                       "cursor-pointer"))

(def c:button-dark (str "bg-zinc-600 hover:bg-zinc-700 active:bg-zinc-900 "
                        "font-sans font-bold no-underline "
                        "text-white visited:text-white hover:text-white "
                        "cursor-pointer"))
(def c:button-light (str "bg-white active:bg-zinc-50 "
                         "shadow-md hover:shadow-lg "
                         "font-sans font-bold no-underline "
                         "text-black visited:text-black hover:text-black "
                         "cursor-pointer"))