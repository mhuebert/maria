(ns cells.cell-tests
  (:require [cells.cell :as cell
             :refer [dep-graph cell]
             :refer-macros [defcell cell]]
            [cljs.test :refer-macros [deftest testing is are run-tests]]
            [cells.lib]
            [clojure.string :as string]
            [cells.eval-context :as eval-context]))

(defn dep-set [coll]
  (set (map cell/cell-name coll)))

(deftest dependencies-test
  (cell/reset-namespace 'cells.cell-tests)

  (defcell a 1)
  (defcell b @a)
  (defcell c @b)
  (defcell d c)

  (are [cell immediate-dependencies]
    (= (cell/dependencies cell) (dep-set immediate-dependencies))
    b #{a}
    c #{b}
    d #{})

  (are [cell immediate-dependents]
    (= (cell/dependents cell) (dep-set immediate-dependents))
    a #{b}
    b #{c}
    c #{}
    d #{})

  (are [cell transitive-dependents]
    (= (cell/transitive-dependents cell) (dep-set transitive-dependents))
    a #{b c}
    b #{c}
    c #{}
    d #{}))



(deftest value-propagation
  (cell/reset-namespace 'cells.cell-tests)

  (defcell e 1)
  (is (= 1 @e))

  (reset! e 2)
  (is (= 2 @e))

  (defcell f @e)
  (defcell g @f)
  (is (= @e @f @g 2))

  (reset! e 3)
  (is (= @e @f @g 3)))



(deftest anonymous-cells
  (cell/reset-namespace 'cells.cell-tests)

  (def h (cell :h 1))
  (def i (cell :i @h))

  (= (cell/dependents h) (dep-set #{i}))
  (is (= 1 @i))

  (def j (cell (str @h @i)))
  (is (= "11" @j))

  (reset! h 2)
  (is (= "22" @j))


  (doseq [item ["a" :b 1 {} []]]
    (let [c (cell item nil)]
      (is (string/ends-with? (str (name c))
                             (str (hash item)))
          "cell id ends in hash of name argument")))

  (testing "Anonymous cells in a loop"
    (let [multi (for [n [1 2 3 4 5]]
                  (cell n n))]
      (prn (map name multi))
      (is (= (list 1 2 3 4 5)
             (map deref multi))))))

(deftest nested-cells
  (cell/reset-namespace 'cells.cell-tests)

  (defcell k
    (defcell l 1)
    (defcell m @l)
    (defcell m-
      (defcell m--
        @m))
    (str @l @m))

  (is (= (cell/dependencies k) (dep-set #{l m})))
  (is (= (cell/dependencies m) (dep-set #{l})))

  (is (= (cell/transitive-dependents l)
         (dep-set #{m k m--})))
  (is (= @k "11"))
  (is (= @m-- 1))


  (reset! l 2)

  (is (= 2 @l @m @@m- @m--))

  (is (= "22" @k))

  (are [cell trans-dependents]
    (= (cell/topo-sort (cell/transitive-dependents cell))
       (map cell/cell-name trans-dependents))
    l (list m k m--)
    m (list k m--)
    m- (list)
    m-- (list))

  (is (= (cell/topo-sort (cell/transitive-dependents l))
         (map cell/cell-name (list m k m--)))
      "Cells are sorted topographically"))

(deftest cell-function
  (cell/reset-namespace 'cells.cell-tests)

  (defcell o
    (mapv cell (range) [:a :b]))

  (is (= (mapv deref @o) [:a :b])
      "When given unique keys, cell function returns unique cells")


  (defcell p
    (mapv cell (repeat 1) [:c :d]))

  (is (= (mapv deref @p) [:c :c])
      "When given same key, cell function returns existing cell"))

(deftest contexts
  ;; We keep track of the "evaluation context" of a cell, such as the
  ;; code editor window where the cell is defined, to facilitate
  ;; interactive development.

  ;; Then we can dispose of all the cells 'owned' by that context when
  ;; desired, eg. immediately before the user has made a change to the
  ;; source code in a particular block of code, and wants to re-evaluate it.

  (cell/reset-namespace 'cells.cell-tests)


  (def ctx-1 (eval-context/new-context))
  (def ctx-2 (eval-context/new-context))

  (binding [cell/*eval-context* ctx-1]
    (defcell r 1)
    (defcell r- @r))

  (binding [cell/*eval-context* ctx-2]
    (defcell s @r)
    (defcell s- @s))

  (is (= (cell/transitive-dependents r) (dep-set [r- s s-])))

  (eval-context/dispose! ctx-1)

  (is (= nil @r @r-))
  (is (= 1 @s @s-))
  (is (= (cell/transitive-dependents r) (dep-set [r- s s-])))



  (comment (binding [cell/*eval-context* ctx-1
                     cell/*debug* true]
             (cell/-compute-with-dependents! r)
             (cell/-compute-with-dependents! r-))

           (binding [cell/*eval-context* ctx-2
                     cell/*debug* true]
             (cell/-compute-with-dependents! s)
             (cell/-compute-with-dependents! s-)))



  (def ctx-3 (eval-context/new-context))
  (def ctx-4 (eval-context/new-context))

  (binding [cell/*eval-context* ctx-3]
    (defn f1 [x]
      (cell @(cell x (str x 1)))))

  (defn f2 []
    (def f3 (cell @(f1 "X"))))
  (binding [cell/*eval-context* ctx-4]
    (f2))

  (is (= "X1" @f3))
  (eval-context/dispose! ctx-4)
  (is (= nil @f3))
  (f2)
  (is (= "X1" @f3))


  )

#_(comment
    ;; REMOVED cell-seqs.. unclear use case, unclear 'clone' behaviour

    (deftest cell-seqs

      (defcell n (inc @self))

      (is (= (take 5 n)
             '(1 2 3 4 5))))

    ;; REMOVED restrictions on reset! and swap!.
    ;; it is tricky to implement restrictions well, and
    ;; treating cells more like atoms has interesting use cases.

    (deftest restricted-mutate
      (defcell o (inc @self))
      (defcell p (swap! self inc))
      @p
      @o
      (is (thrown? js/Error (reset! o 10)))

      (is (thrown? js/Error (swap! o inc)))

      (defcell q
        (is (thrown? js/Error (reset! o 11))))))

#_(comment
    (deftest timers
      ;; need a better approach for automated testing of interval
      (cell/reset-namespace 'cells.cell-tests)

      (let [n (cell (interval 1000 inc))
            o (cell (interval 1500 inc))
            p (cell (str @n ", " @o))]

        (cell (prn @p)))))