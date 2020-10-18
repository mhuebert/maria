(ns chia.view.hooks-test
  (:require ["react" :as react]
            ["react-dom" :as react-dom]
            ["react-dom/test-utils" :as dtest]
            ["react-test-renderer" :as rtest]
            [chia.view :as v]
            [chia.view.hooks :as hooks]
            [cljs.test :refer [deftest testing is are]]
            [chia.view.util :as u]
            [chia.db :as d]
            [applied-science.js-interop :as j]))

(def concurrent? false)
(def test-renderer? false)

(defn act [f] ((if test-renderer? rtest/act dtest/act) #(do (f) js/undefined)))

(defn act! [f & args] (act #(apply f args)))

(defonce test-element (u/find-or-append-element (str ::element)))

(def root (when test-renderer? (atom (rtest/create (v/to-element [:div])))))

(defn concurrent [x]
  (v/-create-element react/unstable_ConcurrentMode nil x))

(defn render! [x]
  (if test-renderer?
    (.update @root (cond-> x concurrent? (concurrent)))
    (v/render-to-dom (cond-> x concurrent? (concurrent)) test-element {:reload? false})))

(def render-count (atom 0))
(defn count-render! []
  (hooks/use-effect #(swap! render-count inc)))

(defn unmount! []
  (reset! render-count 0)
  (if test-renderer?
    (do (.unmount @root)
        (reset! root (rtest/create (v/to-element [:div]))))
    (v/unmount-from-dom test-element)))

(def internal-state-atom (atom nil))

(defn update-db! []
  (d/transact! [[:db/update-attr ::view-test :x inc]])
  (v/flush!))

(v/defview always-view
  {:view/should-update? (constantly true)}
  [x]
  (d/get ::view-test :x)
  (count-render!)
  (str x))

(v/defview view-with-atom []
  (prn :view-with-atom-start)
  (let [st (hooks/use-atom)]
    (count-render!)
    (reset! internal-state-atom st)
    (str "state: " @st)))

(deftest hooks

  (testing "vanilla state hook"
    (let [set-state! (atom nil)
          f (fn []
              (let [[v s!] (hooks/use-state [(rand-int 100)])]
                (count-render!)
                (reset! set-state! s!)
                (v/-create-element "div" nil (str "HELLO, " v))))]
      (act #(render! (v/-create-element f)))
      (prn :count @render-count)
      (act #(@set-state! (fn [x] (conj x (rand-int 100)))))
      (prn :after-act-count @render-count)
      (js/setTimeout #(prn :after-timeout @render-count) 100)


      ))

  #_(testing "memoization"

      (v/defview memoized-view [x]
        (prn :render-memoized-view)
        (d/get ::view-test :x)
        (count-render!)
        (str x))

      (unmount!)
      (render! (memoized-view 1))
      (is (= 1 @render-count) "First render")

      (render! (memoized-view 1))
      (is (= 1 @render-count)
          "View does not re-render with same args")
      (render! (memoized-view 2))
      (is (= 2 @render-count)
          "View re-renders with different args")

      (prn :will-update)
      (update-db!)
      (prn :did-update :measure-counts)

      ;; why isn't this updating?
      (is (= 3 @render-count)
          "View re-renders on chia.view/reactive invalidation"))

  #_#_#_(testing "should-update?"

          (unmount!)
          (render! (always-view 1))
          (render! (always-view 1))
          (is (= 2 @render-count)
              "view/should-update? works")

          (act! update-db!)
          (is (= 3 @render-count)
              "view/should-update? does not interfere with chia.view/reactive invalidations"))

      (testing "hooks/use-atom"
        (unmount!)
        (render! (view-with-atom))
        (swap! @internal-state-atom inc)
        (act v/flush!)
        (is (= 2 @render-count)
            "swapping internal state atom causes render"))

      (testing "ref forwarding"
        (unmount!)
        (def parent-ref (atom nil))
        (v/defview f-view
          {:view/forward-ref? true}
          []
          (let [ref (hooks/use-forwarded-ref)]
            [:div.black {:ref ref}]))

        (v/defview f-parent []
          (let [ref (hooks/use-ref 0)]
            (do (reset! parent-ref ref) nil)
            (d/get ::view-test :x)
            [f-view {:ref ref} "child"]))

        (render! (f-parent "parent"))
        (act! update-db!)

        (is (= (j/get-in @parent-ref [:current :tagName])
               "DIV")
            "Child ref is forwarded to parent"))

  )



