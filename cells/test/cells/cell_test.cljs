(ns cells.cell-test
  (:require [cells.cell :as cell :refer [cell] :refer-macros [defcell cell]]
            [cells.util :as util]
            [cljs.test :refer-macros [deftest
                                      testing
                                      is
                                      are
                                      run-tests
                                      async]]
            [cells.lib]
            [clojure.string :as str]))

(defn dep-set [cells]
  (set cells))

(deftest cells
  (testing "dependencies-test"
    (let [a (cell 1)
          b (cell @a)
          c (cell @b)
          d (cell c)]

      (are [cell immediate-dependencies]
        (= (set (cell/immediate-dependencies cell))
           (dep-set immediate-dependencies))
        b #{a}
        c #{b}
        d #{})

      (are [cell immediate-dependents]
        (= (set (cell/immediate-dependents cell))
           (dep-set immediate-dependents))
        a #{b}
        b #{c}
        c #{}
        d #{})

      (are [cell transitive-dependents]
        (= (set (cell/dependents cell))
           (dep-set transitive-dependents))
        a #{b c}
        b #{c}
        c #{}
        d #{})))

  (testing "value-propagation"

    (let [e (cell 1)]

      (is (= 1 @e))

      (reset! e 2)
      (is (= 2 @e))


      (let [f (cell @e)
            g (cell @f)]
        (is (= @e @f @g 2))

        (reset! e 3)
        (is (= @e @f @g 3)))))

  (testing "anonymous-cells"

    (let [h (cell {:key :h} 1)
          i (cell {:key :i} @h)
          j (cell (str @h @i))]

      (= (set (cell/immediate-dependents h)) (dep-set #{i}))
      (is (= 1 @i))

      (is (= "11" @j))

      (reset! h 2)
      (is (= "22" @j))

      #_(doseq [key ["a" :b 1 {} []]]
          (let [c (cell {:key key} nil)]
            (is (str/includes? (name c)
                               (str (hash key)))
                "cell contains hash of name argument")))

      (testing "Anonymous cells in a loop"
        (let [multi (for [n [1 2 3 4 5]]
                      (cell {:key n} n))]
          (is (= (list 1 2 3 4 5)
                 (map deref multi)))))))

  (testing "cell-function"

    (let [o (cell (mapv cell (map (partial hash-map :key) (range)) [:a :b]))]
      (is (= (mapv deref @o) [:a :b])
          "When given unique keys, cell function returns unique cells"))

    (let [p (cell
             (mapv cell
                   (map (partial hash-map :key) (repeat 1))
                   [:c :d]))]
      (is (= (mapv deref @p) [:c :c])
          "When given same key, cell function returns existing cell")))

  (testing "metadata"
    (let [a1 (cell nil)
          a2 (with-meta a1 {:hello true})]
      (reset! a1 ::hello)
      (is (= @a1 @a2))
      (is (not= (meta a1) (meta a2)))
      (is (= {:hello true} (meta a2)))
      (is (nil? (meta a1))))))



#_(comment
   (deftest timers
     ;; need a better approach for automated testing of interval
     (repl/reset-namespace 'cells.cell-test)

     (let [n (cell (interval 1000 inc))
           o (cell (interval 1500 inc))
           p (cell (str @n ", " @o))]

       (cell (prn @p))))

   (defcell birds
            (lib/fetch "https://ebird.org/ws1.1/data/obs/geo/recent"
                       {:query {:lat "52.4821146"
                                :lng "13.4121388"
                                :maxResults 10
                                :fmt "json"}}))

   (defcell first-bird
            (first @birds)))
#_(deftest contexts
    ;; We keep track of the "evaluation context" of a cell, such as the
    ;; code editor window where the cell is defined, to facilitate
    ;; interactive development.

    ;; Then we can dispose of all the cells 'owned' by that context when
    ;; desired, eg. immediately before the user has made a change to the
    ;; source code in a particular block of code, and wants to re-evaluate it.

    (repl/reset-namespace 'cells.cell-test)


    (let [ctx-1 (lifecycle/default-owner)
          ctx-2 (lifecycle/default-owner)]

      (binding [lifecycle/*owner* ctx-1]
        (defcell r 1)
        (defcell r- @r))

      (binding [lifecycle/*owner* ctx-2]
        (defcell s @r)
        (defcell s- @s))

      (is (= (dep/transitive-dependents @*graph* r)
             (dep-set [r- s s-])))

      (lifecycle/dispose! ctx-1)

      (is (= nil @r @r-))
      (is (= 1 @s @s-))

      (is (= (dep/transitive-dependents @*graph* r)
             (dep-set [r- s s-]))
          "Dependencies to named cells persist"))

    (comment (binding [lifecycle/*owner* ctx-1
                       cell/DEBUG true]
               (cell/eval-and-set! r)
               (cell/eval-and-set! r-))

             (binding [lifecycle/*owner* ctx-2
                       cell/DEBUG true]
               (cell/eval-and-set! s)
               (cell/eval-and-set! s-)))



    (def ctx-3 (lifecycle/default-owner))
    (def ctx-4 (lifecycle/default-owner))

    (binding [lifecycle/*owner* ctx-3]
      (defn f1 [x]
        (cell @(cell {:key x} (str x 1)))))

    (defn f2 []
      (def f3 (cell @(f1 "X"))))
    (binding [lifecycle/*owner* ctx-4]
      (f2))

    (is (= "X1" @f3))
    (lifecycle/dispose! ctx-4)
    (is (= nil @f3))
    (f2)
    (is (= "X1" @f3))

    )