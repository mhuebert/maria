(ns lark.eval)

(defmacro defspecial
  "Define a repl-special function. It will receive current compiler-state and compiler-env as first two args."
  [name & body]
  (let [docstring (when (string? (first body)) (first body))
        body (cond->> body docstring (drop 1))]
    `(do (def ~name (with-meta (fn [c-state# c-env# source# [~'_ & args#]]
                                 (let [~'&source source#]
                                    (apply ~(cons 'fn body) c-state# c-env# args#)))
                               {:doc      ~docstring
                                :name     '~(symbol (str *ns*) (str name))
                                :arglists '~[(->> (filter vector? body)
                                                  (first)
                                                  (drop 2)
                                                  (vec))]}))
         (~'lark.eval/swap-repl-specials! ~'assoc '~name ~name))))