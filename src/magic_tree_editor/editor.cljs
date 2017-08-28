(ns magic-tree-editor.editor)

;; protocols for editor


;; protocols for AST/node
;; - from-string (deserialize)
;; - to-string (serialize)

;;

#_(defprotocol ICursor

  (get-selections [this])
  (put-selections! [this selections])

  (cursor-edge [this])
  (cursor-coords [this])
  (at-end? [this])
  (at-start? [this])
  (selection-expand [this])
  (selection-contract [this]))