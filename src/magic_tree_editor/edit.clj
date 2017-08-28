(ns magic-tree-editor.edit)

(defmacro operation [editor & body]
  `(binding [~'magic-tree-editor.edit/*changes* (~'array)]
     (~'.on ~editor "changes" ~'magic-tree-editor.edit/log-editor-changes)
     (~'.operation ~editor (fn [] ~@body))
     (~'.off ~editor "changes" ~'magic-tree-editor.edit/log-editor-changes)
     (or ~'magic-tree-editor.edit/*changes* true)))