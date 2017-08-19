(ns cells.cell
  (:require [re-db.d :as d]
            [maria.eval :as e]
            [re-db.patterns :as patterns :include-macros true]
            [maria.blocks.blocks]
            [goog.net.XhrIo :as xhr]))

(def ^:dynamic *cell* nil)

(defprotocol ICompute
  (-compute! [this]
             "Compute a value without changing state")
  (-compute-and-listen! [this]
                        "Compute a value, creating listeners for dependent data."))

(defn interval [n f]
  (let [cell *cell*
        the-interval (js/setInterval #(binding [*cell* cell]
                                        (try
                                          (-reset! cell (f @cell))
                                          (catch js/Error e
                                            (e/teardown! cell)))) n)]
    (e/on-teardown cell #(js/clearInterval the-interval))
    (f @cell)))

(defn slurp
  ([url]
    (slurp url (comp #(js->clj % :keywordize-keys true) js/JSON.parse) nil))
  ([url format-value f]
   (let [cell *cell*]
     (js/setTimeout
       #(xhr/send url (fn [response]
                       (let [formatted-value (-> response (.-target) (.getResponseText) (format-value))]
                         (-reset! cell (cond->> formatted-value
                                                f (f @cell)))))) 0)
     nil)))

(defonce -teardowns (volatile! {}))

(deftype Cell [id f]

  INamed
  (-name [this] id)

  e/ITearDown
  (on-teardown [this f]
    (vswap! -teardowns update id conj f))
  (-teardown! [this]
    (doseq [f (get @-teardowns id)]
      (f))
    (vswap! -teardowns dissoc id))

  IDeref
  (-deref [this]
    (d/get ::cells id))

  IReset
  (-reset! [this newval]
    (d/transact! [[:db/add ::cells id newval]])
    newval)

  ISwap
  (-swap! [this f] (reset! this (f @this)))
  (-swap! [this f a] (reset! this (f @this a)))
  (-swap! [this f a b] (reset! this (f @this a b)))
  (-swap! [this f a b xs] (reset! this (apply f @this a b xs)))

  ICompute
  (-compute! [this]
    (binding [*cell* this]
      (try
        (-reset! this (f this))
        (catch js/Error e
          (e/teardown! this)
          (throw e)))))
  (-compute-and-listen! [this]
    (let [{:keys [patterns]} (patterns/capture-patterns (-compute! this))

          ;; remove self from patterns to avoid loop
          patterns (update patterns :ea_ disj [::cells id])]

      (e/on-teardown this (d/listen patterns #(-compute! this))))))

(defn make-cell
  ([f]
   (make-cell (d/unique-id) f))
  ([id f]
   (let [cell (->Cell id f)]
     (-compute-and-listen! cell)
     cell)))

