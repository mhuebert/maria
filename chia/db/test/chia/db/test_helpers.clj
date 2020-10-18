(ns chia.db.test-helpers)

(defmacro throws [& body]
  (let [message (when (string? (last body)) (last body))
        body (cond-> body
                     message (drop-last))]
    (cond-> `(~'cljs.test/is (~'thrown? ~'js/Error ~@body))
            message (concat (list message)))))