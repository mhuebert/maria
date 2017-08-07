(ns maria.cells.code-eval
  (:require [maria.eval :as eval]
            [re-db.d :as d]))

(defn log-eval-result! [id result]
  (let [result (assoc result :id (d/unique-id))]
    (d/transact! [[:db/update-attr :repl/state :eval-log (fnil conj []) result]
                  (when id [:db/update-attr id :eval-log (fnil conj []) result])])))

(defn eval-str [id source]
  (log-eval-result! id (eval/eval-str source)))

(defn eval-form [id form]
  (log-eval-result! id (eval/eval form)))