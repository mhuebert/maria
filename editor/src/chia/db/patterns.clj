(ns chia.db.patterns)

#_(defmacro capture-patterns
    "Evaluates body, returning map with evaluation result and read patterns."
    [& body]
    `(binding [~'chia.db.patterns/*pattern-log* {}]
       (let [{value# :value
              tx-report# :tx-report} (~'chia.db.core/db-log (do ~@body))
             patterns# ~'chia.db.patterns/*pattern-log*]
         (~'chia.db.core/notify-listeners tx-report#)
         {:value value#
          :patterns patterns#})))