(ns maria.log
  (:refer-clojure :exclude [println])
  #?(:cljs (:require-macros [maria.log])))

(def js-loggers
  "Browser logging functions based on `key`, defaulting to `console.log`"
  {:error #?(:clj  'js/console.error
             :cljs js/console.error)
   :warn  #?(:clj  'js/console.warn
             :cljs js/console.warn)
   :debug #?(:clj  'js/console.debug
             :cljs js/console.debug)
   :log   #?(:clj  'js/console.log
             :cljs js/console.log)})

#?(:clj
   (defmacro println
     "Prints `args` to the console, using the given `level`.

      Using a macro to wrap println allows for display of the correct location/stack,
      which is normally obscured by the stack for `println` itself."
     [level & args]
     (let [[logger args] (if (contains? js-loggers level)
                           [(get js-loggers level) args]
                           [(get js-loggers :log) (cons level args)])]
       `(~logger (println-str ~@args)))))