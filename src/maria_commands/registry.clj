(ns maria-commands.registry
  (:require [re-view.util :refer [parse-opt-args]]))

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
        {bindings        :bindings
         when*           :when
         intercept-when* :intercept-when
         priority        :priority
         private         :private
         :or             {bindings []}} options
        _ (when (nil? arglist)
            (assert (empty? body)))
        name-as-symbol (symbol (str (namespace command-name) "_" (name command-name)))]
    `(~'maria-commands.registry/register! {:name           ~command-name
                                           :doc            ~docstring
                                           :exec-pred      ~when*
                                           :intercept-pred ~(if (boolean? intercept-when*)
                                                              `(fn [] ~intercept-when*)
                                                              intercept-when*)
                                           :priority       ~priority
                                           :private        ~private
                                           :command        ~(if arglist
                                                              `(fn ~name-as-symbol ~arglist
                                                                 ~@body)
                                                              `(fn ~name-as-symbol [] ~'(.-Pass js/CodeMirror)))} ~bindings)))