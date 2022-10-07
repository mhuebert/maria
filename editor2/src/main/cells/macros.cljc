(ns cells.macros)

(defn defcell:impl
  "Defines a named cell."
  [form env the-name & body]
  (let [[docstring body] (if (string? (first body))
                           [(first body) (rest body)]
                           [nil body])
        [options body] (if (and (map? (first body)) (> (count body) 1))
                         [(first body) (rest body)]
                         [nil body])
        f `(fn [~'self] ~@body)]
    `(do
       ;; support re-evaluation without breaking links
       (declare ~the-name)
       (let [prev-cell# ~the-name]
         (def ~(with-meta the-name options)
           ~@(when docstring (list docstring))
           (if (some? prev-cell#)
             (~'cells.cell/update-cell* prev-cell# ~f)
             (~'cells.cell/cell* ~f)))))))

(defn cell:impl
  [form env key expr]
  #_(let [id (util/unique-id)]
    `(~'cells.cell/cell*
      (fn [~'self] ~expr)
      (str ~id "#" (hash ~key)))))

(defn bound-fn:impl
  [form env body]
  `(let [cell# ~'cells.cell/*self*
         error-handler# ~'cells.cell/*error-handler*]
     (fn [& args#]
       (binding [~'cells.cell/*self* cell#
                 ~'cells.cell/*error-handler* error-handler#]
         (try (apply (fn ~@body) args#)
              (catch ~'js/Error e#
                (~'cells.cell/error! cell# e#)))))))

(defn memoized-on:impl [form env o k body]
  `(let [o# ~o]
     (~'applied-science.js-interop/get o# ~k
      (let [v# (do ~@body)]
        (~'applied-science.js-interop/assoc! o# ~k)
        v#))))