(ns maria.ui
  (:require #?(:cljs ["react" :as react])
            #?(:cljs ["prosemirror-keymap" :refer [keydownHandler]])
            #?(:cljs ["@radix-ui/react-tooltip" :as Tooltip])
            [applied-science.js-interop :as j]
            [clojure.pprint]
            [yawn.view :as v]
            [re-db.fast :as fast]
            [re-db.hooks :as hooks]
            [re-db.memo :as memo]
            [re-db.react]
            [re-db.reactive :as r]
            [re-db.xform :as xf]
            #?(:cljs [maria.cloud.local :as local]))
  #?(:cljs (:require-macros [maria.ui :as ui])))

(def sidebar-transition "all 0.2s ease 0s")

(def default-state {:sidebar/visible? false
                    :sidebar/width 250
                    :sidebar/transition "all 0.2s ease 0s"
                    :sidebar/open #{"curriculum"}})

#?(:cljs
   (defonce !state (local/ratom ::state default-state)))

(comment
  @!state
  (swap! !state merge default-state))

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

(defmacro pprinted [x]
  `(with-out-str (~(if (:ns &env) 'cljs.pprint/pprint 'clojure.pprint/pprint) ~x)))

(def c:divider "border-b-2 border-zinc-100")

(def c:button-dark (str "bg-zinc-600 hover:bg-zinc-700 active:bg-zinc-900 "
                        "font-sans font-bold no-underline "
                        "text-white visited:text-white hover:text-white "
                        "cursor-pointer"))
(def c:button-light (str "bg-white active:bg-zinc-50 "
                         "shadow-md hover:shadow-lg "
                         "font-sans font-bold no-underline "
                         "text-black visited:text-black hover:text-black "
                         "cursor-pointer"))

#?(:cljs
   (defonce get-context (memoize (fn [_k] (react/createContext nil)))))

#?(:cljs
   (defn use-context
     ([k not-found]
      (or (get-context k) not-found))
     ([k]
      (react/useContext (get-context k)))))

#?(:clj
   (defmacro provide-context [values child]
     (loop [values values
            out child]
       (if (empty? values)
         `(v/x ~out)
         (let [[k v] (first values)]
           (recur (rest values)
                  `[:el (j/get (~'maria.ui/get-context ~k) :Provider)
                    {:value ~v}
                    ~out]))))))

#?(:cljs
   (defn keydown-handler [bindings]
     (let [handler (keydownHandler (clj->js bindings))]
       (fn [e] (handler #js{} e)))))

#?(:cljs
   (ui/defview tooltip [trigger content]
     [:el Tooltip/Provider {:delay-duration 200}
      [:el Tooltip/Root
       [:el Tooltip/Trigger {:asChild true} trigger]

       [:el.bg-zinc-600.text-white.rounded.shadow.text-sm.px-2.py-1.z-60 Tooltip/Content
        [:el.fill-zinc-600 Tooltip/Arrow]
        content]]]))