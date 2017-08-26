(ns magic-tree-codemirror.edit)

(defmacro operation [editor & body]
  `(do (~'.operation ~editor (fn [] ~@body))
       true))