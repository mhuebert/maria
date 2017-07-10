(ns maria.commands.core
  (:require [clojure.string :as string]))

(defn parse-key-pattern [patterns]
  (mapv (fn [s] (set (string/split s #"[-+]"))) (string/split patterns " ")))

(defmacro defcommand [name-as-keyword
                      key-patterns
                      docstring & body]
  `(do (swap! ~'maria.commands.core/commands assoc ~name-as-keyword {:doc         ~docstring
                                                                     :key-pattern ~key-patterns
                                                                     :command     (fn ~(name name-as-keyword) ~@body)})
       (doseq [pattern ~(if (string? key-patterns) [key-patterns] key-patterns)]
         (swap! ~'maria.commands.core/mappings update-in [~(parse-key-pattern pattern) :commands] (fnil conj #{}) ~name-as-keyword))))