(ns lark.tree.core
  (:require [lark.tree.parse :as parse]
            [lark.tree.emit :as emit]
            [lark.tree.node :as n]
            [lark.tree.format :as format]))

;; Parse

(def ast
  "Given ClojureScript source, returns AST"
  parse/ast)

(def ast-zip n/ast-zip)

(def string-zip
  "Given ClojureScript source, returns zipper"
  (comp ast-zip parse/ast))

;; Navigation
(defn format [x]
  (let [x (cond-> x
                  (string? x) (parse/ast))]
    (binding [format/*pretty* true]
      (emit/string x))))

(comment

 (let [sample-code-string ""]
   (let [_ (.profile js/console "parse-ast")
         ast (do (dotimes [n 4]
                   (parse/ast sample-code-string))
                 (time (parse/ast sample-code-string)))
         _ (.profileEnd js/console)

         _ (.profile js/console "emit-string")
         str (do (dotimes [n 4]
                   (emit/string ast))
                 (time (emit/string ast)))
         _ (.profileEnd js/console)

         _ (.profile js/console "emit-formatted-string")
         formatted-str (do (dotimes [n 4]
                             (format ast))
                           (time (format ast)))
         _ (.profileEnd js/console)]
     (println :cljs-core-string-verify (= str sample-code-string)))))

