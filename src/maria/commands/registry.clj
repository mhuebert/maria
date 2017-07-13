(ns maria.commands.registry
  (:require [clojure.string :as string]))

(defn spaced-name [the-name]
  (str (string/upper-case (first the-name)) (string/replace (subs the-name 1) "-" " ")))

(defmacro defcommand [name-as-keyword
                      key-patterns
                      docstring & body]
  (let [normalized-key-patterns (if (string? key-patterns) [key-patterns] key-patterns)
        commands 'maria.commands.registry/commands
        mappings 'maria.commands.registry/mappings
        normalize-keyset-string 'maria.commands.registry/normalize-keyset-string
        form `(let [parsed-key-patterns# (mapv ~normalize-keyset-string ~normalized-key-patterns)]
                (swap! ~commands assoc ~name-as-keyword {:name                ~name-as-keyword
                                                         :namespace           ~(some-> (namespace name-as-keyword) (spaced-name))
                                                         :display-name        ~(spaced-name (name name-as-keyword))
                                                         :doc                 ~docstring
                                                         :key-pattern-strings ~normalized-key-patterns
                                                         :key-patterns        parsed-key-patterns#
                                                         :command             ~(if
                                                                                 (vector? (first body))
                                                                                 `(fn ~(name name-as-keyword) ~@body)
                                                                                 (first body))})
                (doseq [pattern# parsed-key-patterns#]
                  (swap! ~mappings update-in pattern# assoc :exec ~name-as-keyword)))]
    form))