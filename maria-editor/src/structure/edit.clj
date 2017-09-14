(ns structure.edit)

(defmacro operation
  "Wraps `body` in a CodeMirror operation, and returns the array of changes made by the operation."
  [editor & body]
  `(binding [~'structure.edit/*changes* (~'array)]
     (~'.on ~editor "changes" ~'structure.edit/log-editor-changes)
     (~'.operation ~editor (fn [] ~@body))
     (~'.off ~editor "changes" ~'structure.edit/log-editor-changes)
     (or ~'structure.edit/*changes* true)))