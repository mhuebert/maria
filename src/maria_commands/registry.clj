(ns maria-commands.registry
  (:require [clojure.string :as string]
            [re-view.util :refer [parse-opt-args]]))

(defn spaced-name [the-name]
  (str (string/upper-case (first the-name)) (string/replace (subs the-name 1) "-" " ")))

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
        commands 'maria-commands.registry/commands
        mappings 'maria-commands.registry/mappings
        normalize-keyset-string 'maria-commands.registry/normalize-keyset-string
        name-as-symbol (symbol (str (namespace command-name) "_" (name command-name)))
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
                                                                           `(fn ~name-as-symbol ~arglist
                                                                              ~@body)
                                                                           `(fn ~name-as-symbol [] ~'(.-Pass js/CodeMirror)))})
                (doseq [pattern# parsed-key-patterns#]
                  (swap! ~mappings update-in (conj pattern# :exec) conj ~command-name)))]
    form))