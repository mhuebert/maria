(ns maria.commands.registry
  (:require [clojure.string :as string]))

(defn spaced-name [the-name]
  (str (string/upper-case (first the-name)) (string/replace (subs the-name 1) "-" " ")))

(defn parse-opt-args [preds args]
  (loop [preds preds
         args args
         out []]
    (if (empty? preds)
      (conj out args)
      (let [match? ((first preds) (first args))]
        (recur (rest preds)
               (cond-> args match? (rest))
               (conj out (if match? (first args) nil)))))))

(comment
  (doseq [[args opts] [[["a" {} 1 2] ["a" {} '(1 2)]]
                       [[{} 1 2] [nil {} '(1 2)]]
                       [["a" 1 2] ["a" nil '(1 2)]]]]
    (assert (= (parse-opt-args [string? map?] args) opts))))

(defmacro defcommand [command-name & args]
  (let [[docstring options body] (parse-opt-args [string? map?] args)
        {key-patterns :bindings
         pred         :when
         :or          {key-patterns []}} options
        [arglist & body] body
        _ (assert (vector? arglist))
        normalized-key-patterns (if (string? key-patterns) [key-patterns] key-patterns)
        commands 'maria.commands.registry/commands
        mappings 'maria.commands.registry/mappings
        normalize-keyset-string 'maria.commands.registry/normalize-keyset-string
        form `(let [parsed-key-patterns# (mapv ~normalize-keyset-string ~normalized-key-patterns)]
                (swap! ~commands assoc ~command-name {:name             ~command-name
                                                      :namespace        ~(some-> (namespace command-name) (spaced-name))
                                                      :display-name     ~(spaced-name (name command-name))
                                                      :doc              ~docstring
                                                      :pred             ~pred
                                                      :bindings-strings ~normalized-key-patterns
                                                      :bindings         parsed-key-patterns#
                                                      :command          (fn ~arglist
                                                                          ~@body)})
                (doseq [pattern# parsed-key-patterns#]
                  (swap! ~mappings update-in (conj pattern# :exec) conj ~command-name)))]
    form))