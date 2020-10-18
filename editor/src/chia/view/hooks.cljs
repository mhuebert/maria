(ns chia.view.hooks
  "React hooks in ClojureScript"
  (:require ["react" :as react]
            [chia.view.impl :as impl]
            [chia.view.render-loop :as render-loop]
            [chia.reactive :as r]
            [applied-science.js-interop :as j]
            [chia.view.hiccup :as hiccup]
            [chia.view.props :as props]))

(defonce ^:private sentinel #js{})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Why does this namespace exist? why not just use React hooks directly?
;;
;; * some hooks that rely on javascript-specific semantics like `undefined` and js equality,
;;   these need to be adapted for cljs to work properly at all
;; * figwheel/shadow-style reloading needs to be explicitly supported
;; * built-in state handling is not consistent with how Clojure handles state (ie. use atoms)
;;
;; lastly, Chia has its own reactivity system which we want to support.




;; React hooks that have alternative implementations/wrappers in this ns
(def -use-effect react/useEffect)
(def -use-context react/useContext)
(def -use-memo react/useMemo)
(def -use-layout-effect react/useLayoutEffect)

;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Built-in hooks to be used directly

(def use-state react/useState)
(def use-reducer react/useReducer)
(def use-callback react/useCallback)
(def use-ref react/useRef)
(def use-imperative-handle react/useImperativeHandle)
(def use-debug-value react/use-debug-value)

;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Modified versions of built-in hooks

(defn use-context
  "Returns binding for context `context-k` (context can be a React context or a globally distinct keyword)"
  [context-k]
  (-use-context
   (impl/lookup-context context-k)))

(defn- effect*
  "`f` is called on every render, or each time `key` is not= to the previous `key`.

   If a function is returned from `f`, it will be called when the view unmounts."
  [native-use-effect]

  (fn use-effect*
    ([f] (native-use-effect (impl/wrap-effect f)))
    ([f key] (use-effect* f = key))
    ([f equal? key]
     (let [key-ref (j/get (use-ref #js[key 0]) :current)
           key-count (let [kcount (aget key-ref 1)]
                       (if (not (equal? key (aget key-ref 0)))
                         (aset key-ref 1 (inc kcount))
                         kcount))]
       (aset key-ref 0 key)
       (native-use-effect (impl/wrap-effect f) #js [key-count])))))

(def use-effect
  "`f` is called on every render, or each time `key` is not= to the previous `key`.

   If a function is returned from `f`, it will be called when the view unmounts."
  ^{:arglists '([f] [f key] [f equal? key])}
  (effect* -use-effect))

(def use-layout-effect
  "Like `use-effect` but called synchronously, after DOM operations are complete."
  ^{:arglists '([f] [f key] [f equal? key])}
  (effect* -use-layout-effect))

;;;;;;;;;;;;;;;;;;;;;
;;
;; Custom hooks
;;

(defn use-will-unmount
  "Evaluates `f` when component unmounts."
  [f]
  (use-effect (constantly f) nil))

(defn use-did-mount
  [f]
  (use-effect f nil))

(defn use-memo
  "Evaluates `f` once, caches and returns result. Re-evaluates when `key` changes.

   Guaranteed to only evaluate once per lifecycle."
  ([f] (use-memo f identical? sentinel))
  ([f key] (use-memo f = key))
  ([f equal? key]
   (let [mem (-> (use-ref #js[nil nil])
                 (j/get :current))]
     (when-not (equal? (aget mem 0) key)
       (j/assoc! mem 0 key 1 (f)))
     (aget mem 1))))

(deftype HookAtom [^:mutable state]
  IDeref
  (-deref [_] (aget state 0))
  IReset
  (-reset! [_ value] ((aget state 1) (constantly value)))
  ISwap
  (-swap! [_ f] ((aget state 1) f))
  (-swap! [_ f a] ((aget state 1) #(f % a)))
  (-swap! [_ f a b] ((aget state 1) #(f % a b)))
  (-swap! [_ f a b xs] ((aget state 1) #(apply f % a b xs))))

(defn use-atom
  "Returns an atom with `initial-state`. Current view will re-render when value of atom changes."
  ([] (use-atom nil))
  ([initial-state]
   (-> (use-ref (HookAtom. nil))
       (j/get :current)
       (j/assoc! .-state (use-state initial-state)))))

(defn use-atom-sync
  "like `use-atom` but not react-concurrent-safe"
  ([] (use-atom-sync nil))
  ([initial-state]
   (let [chia$view r/*reader*
         state-atom (use-memo (fn []
                                    (let [state-atom (atom initial-state)]
                                      (add-watch state-atom ::state-atom
                                                 (fn [_ _ old-state new-state]
                                                   (when (not= old-state new-state)
                                                     (render-loop/schedule-update! chia$view))))
                                      state-atom)))]
     (use-will-unmount #(remove-watch state-atom ::state-atom))
     state-atom)))

(defn use-schedule-update
  "Returns a `forceUpdate`-like function for the current view (not synchronous)."
  []
  (-> (use-reducer inc 0)
      (aget 1)))

(defn use-interval
  [{:keys [interval
           now?
           key]} f]
  (use-effect (fn []
                (when now? (f))
                (let [i (js/setInterval f interval)]
                  #(js/clearInterval i)))
              [key interval]))

(defn use-dom-ref
  "Returns a ref to be passed as the `:ref` to a react view.
  When mounted, `f` is called once with the referenced DOM element."
  [f]
  (let [[val setval] (use-state)]
    (use-layout-effect
     (fn [] (f val))
     val)
    setval))

(defn use-listener [target event f capture-phase]
  (let [f-ref (use-ref f)]
    (use-effect (fn []
                  (.addEventListener target event (j/get f-ref :current) capture-phase)
                  #(.removeEventListener target event (j/get f-ref :current) capture-phase)))))

;;;;;;;;;;;;;;;;;;;;;
;;
;; Chia-specific
;;

(defn use-forwarded-ref
  "Returns a `ref` which will be forwarded to parent.
  Requires `:view/forward-ref?` option on this view to be true."
  []
  (let [forwarded-ref (j/get r/*reader* .-chia$forwardRef)
        ref (use-ref)]
    (assert (j/contains? r/*reader* .-chia$forwardRef) "use-forwarded-ref requires :view/forward-ref? to be true")
    (use-imperative-handle forwarded-ref
                           (fn [] (j/get ref :current)))
    ref))