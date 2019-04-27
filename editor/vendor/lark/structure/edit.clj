(ns lark.structure.edit)

(defmacro operation
  "Wraps `body` in a CodeMirror operation, and returns the array of changes made by the operation."
  [editor & body]
  `(binding [~'lark.structure.edit/*changes* (~'array)]
     (~'.on ~editor "changes" ~'lark.structure.edit/log-editor-changes)
     (~'.operation ~editor (fn [] ~@body))
     (~'.off ~editor "changes" ~'lark.structure.edit/log-editor-changes)
     (or ~'lark.structure.edit/*changes* true)))

(defmacro with-formatting
  [editor & body]
  ;; TODO
  ;; - a `format` command which returns a "formatted ast"
  ;; - set contents of editor via AST instead of string
  ;; first, eval `body`. assume that cursor is left in correct position.
  (let [[opts body] (if (or (map? (first body))
                            (symbol? (first body)))
                      [(first body) (rest body)]
                      [nil body])]
    `(~'.operation ~editor
      (fn []
        (let [res# (do ~@body)
              editor# ~editor]
          (when-not (false? res#)
            (~'lark.structure.edit/format! editor# ~opts))
          res#)))))