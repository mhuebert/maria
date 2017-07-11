(ns maria.commands.core
  (:require [clojure.string :as string]))

(defn spaced-name [k]
  (let [s (name k)]
    (str (string/upper-case (first s)) (string/replace (subs s 1) "-" " "))))

(defmacro defcommand [name-as-keyword
                      key-patterns
                      docstring & body]
  (let [normalized-key-patterns (if (string? key-patterns) [key-patterns] key-patterns)
        commands 'maria.commands.core/commands
        mappings 'maria.commands.core/mappings
        normalize-keyset-string 'maria.commands.core/normalize-keyset-string
        form `(let [parsed-key-patterns# (mapv ~normalize-keyset-string ~normalized-key-patterns)]
                (swap! ~commands assoc ~name-as-keyword {:name         ~name-as-keyword
                                                         :display-name ~(spaced-name name-as-keyword)
                                                         :doc          ~docstring
                                                         :key-pattern  ~normalized-key-patterns
                                                         :command      ~(if
                                                                          (vector? (first body))
                                                                          `(fn ~(name name-as-keyword) ~@body)
                                                                          (first body))})
                (doseq [pattern# parsed-key-patterns#]
                  (swap! ~mappings assoc-in pattern# ~name-as-keyword)))]
    form))