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

(defmacro defcommand
  "Defines a command. command-name should be a namespaced keyword, followed by the optional positional args:
  - docstring
  - options map, which may contain:
      :bindings, a vector of keymaps to bind, each containing a keyset of the form 'Cmd-X'
      :intercept-when, a predicate indicating whether the key binding should stopPropagation and preventDefault
           (if not supplied, will stop the event when :exec-when is true)
      :exec-when, a predicate indicating whether the command is enabled for a given context
  - a vector of arguments followed by body forms, for the command function.

  If no arglist/body is provided, a passthrough function will be supplied, so that `defcommand`
  can be used for documenting existing/built-in behaviour."
  [command-name & args]
  (let [[docstring options arglist body] (parse-opt-args [string? map? vector?] args)
        {key-patterns   :bindings
         exec-pred      :when
         intercept-pred :intercept-when
         :or            {key-patterns []}} options
        normalized-key-patterns (if (string? key-patterns) [key-patterns] key-patterns)
        _ (when (nil? arglist)
            (assert (empty? body)))
        commands 'maria.commands.registry/commands
        mappings 'maria.commands.registry/mappings
        normalize-keyset-string 'maria.commands.registry/normalize-keyset-string
        form `(let [parsed-key-patterns# (mapv ~normalize-keyset-string ~normalized-key-patterns)]
                (swap! ~commands assoc ~command-name {:name             ~command-name
                                                      :namespace        ~(some-> (namespace command-name) (spaced-name))
                                                      :display-name     ~(spaced-name (name command-name))
                                                      :doc              ~docstring
                                                      :exec-pred        ~exec-pred
                                                      :intercept-pred   ~(if (boolean? intercept-pred)
                                                                           `(fn [] ~intercept-pred)
                                                                           intercept-pred)
                                                      :bindings-strings ~normalized-key-patterns
                                                      :bindings         parsed-key-patterns#
                                                      :command          ~(if arglist
                                                                           `(fn ~arglist
                                                                              ~@body)
                                                                           '(fn [] (.-Pass js/CodeMirror)))})
                (doseq [pattern# parsed-key-patterns#]
                  (swap! ~mappings update-in (conj pattern# :exec) conj ~command-name)))]
    form))