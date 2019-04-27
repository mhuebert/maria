(ns lark.tree.fn
  (:require [clojure.walk :as w]))


(defn construct-fn
  "Construct function form."
  [syms vararg body]
  (list
    'fn*
    (vec
      (concat
        syms
        (if vararg
          (list '& vararg))))
    body))

(defn sym-index
  "Get index based on the substring following the parameter's `%`.
   Zero means vararg."
  [n]
  (cond (= n "&") 0
        (= n "") 1
        (re-matches #"\d+" n) (#?(:cljs js/parseInt
                                  :clj Integer.) n)
        :else (throw (#?(:cljs js/Error.
                         :clj Exception.) "arg literal must be %, %& or %integer."))))

;; TODO: No promises available
(defn symbol->gensym
  "If symbol starting with `%`, convert to respective gensym."
  [sym-seq vararg? max-n sym]
  (if (symbol? sym)
    (let [nm (name sym)]
      (if (= (.indexOf nm "%") 0)
        (let [i (sym-index (subs nm 1))]
          ;;           (if (and (= i 0) (not (realized? vararg?)))
          ;;             (deliver vararg? true))
          (swap! max-n max i)
          (nth sym-seq i))))))

;; TODO: No promises available
(defn fn-walk
  "Walk the form and create an expand function form."
  [form]
  (let [syms (for [i (range)
                   :let [base (if (= i 0)
                                "rest__"
                                (str "p" i "__"))
                         s (name (gensym base))]]
               (symbol (str s "#")))
        vararg? false ;(promise)
        max-n (atom 0)
        body (w/prewalk
               #(or (symbol->gensym syms vararg? max-n %) %)
               form)]
    (construct-fn
      (take @max-n (rest syms))
      nil
      ;;       (if (deref vararg? 0 nil)
      ;;         (first syms))
      body)))