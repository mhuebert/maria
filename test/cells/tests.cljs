(ns cells.tests
  (:require [cells.cell :as cell
             :refer [dep-graph]
             :refer-macros [defcell cell]]
            [cells.lib :as lib]
            [com.stuartsierra.dependency :as dep]
            [cljs.test :refer-macros [deftest testing is are run-tests]]))


(defn dep-set [coll]
  (set (map cell/cell-name coll)))

(deftest dependencies-test
  (cell/reset-namespace 'cells.tests)

  (defcell a 1)
  (defcell b @a)
  (defcell c @b)
  (defcell d c)

  (are [cell its-dependencies]
    (= (cell/dependencies cell) (dep-set its-dependencies))
    b #{a}
    c #{b}
    d #{})

  (are [cell its-dependents]
    (= (cell/dependents cell) (dep-set its-dependents))
    a #{b})

  (is (= (cell/dependencies d) #{})
      "Cells that are not dereferenced are not dependencies")

  (is (= (cell/transitive-dependents a) (dep-set #{b c}))))

(deftest value-propagation
  (cell/reset-namespace 'cells.tests)

  (defcell e 1)
  (is (= 1 @e))

  (cell/reset-cell! e 2)
  (is (= 2 @e))

  (defcell f @e)
  (defcell g @f)
  (is (= @e @f @g 2))

  (cell/reset-cell! e 3)
  (is (= @e @f @g 3)))

(deftest contexts
  (cell/reset-namespace 'cells.tests)

  (defcell ctx-1 1)
  (defcell ctx-2 @ctx-1)

  (cell/dispose! cell/*eval-context*)

  (is (nil? @ctx-1))
  (is (nil? @ctx-2))
  (cell/reset-cell! ctx-1 1)
  (is (= 1 @ctx-1))
  (is (nil? @ctx-2))
  )

(deftest anonymous-cells
  (cell/reset-namespace 'cells.tests)

  (def h (cell :h 1))
  (def i (cell :i @h))

  (is (= (name (name h)) (munge :h)))

  (= (cell/dependents h) (dep-set #{i}))
  (is (= 1 @i))

  (def j (cell (str @h @i)))
  (is (= "11" @j))

  (cell/reset-cell! h 2)
  (is (= "22" @j))

  (testing "Anonymous cells in a loop"
    (let [multi (for [n [1 2 3 4 5]]
                  (cell n n))]
      (is (= (list 1 2 3 4 5)
             (map deref multi))))))

(deftest nested-cells
  (cell/reset-namespace 'cells.tests)

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


  (cell/reset-cell! l 2)

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



(deftest cell-seqs

    (defcell n (inc @self))

    (is (= (take 5 n)
           '(1 2 3 4 5))))

(deftest restricted-mutate
  (defcell o (inc @self))
  (defcell p (swap! self inc))
  @p
  @o
  (is (thrown? js/Error (reset! o 10)))

  (is (thrown? js/Error (swap! o inc)))

  (defcell q
    (is (thrown? js/Error (reset! o 11)))))

(comment
  (deftest timers
    ;; doesn't really work for automated testing but we can eyeball it
    (cell/reset-namespace 'cells.tests)

    (let [n (cell (interval 1000 inc))
          o (cell (interval 1500 inc))
          p (cell (str @n ", " @o))]

      (cell (prn @p)))))


(defn start []
  (cell/reset-namespace 'cells.tests)
  (run-tests))

(defonce _ (start))