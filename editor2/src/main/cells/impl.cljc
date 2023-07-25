(ns cells.impl
  (:require [cells.async :as a]
            [re-db.reactive :as r]
            [re-db.util :as util]))

(defn make-cell
  ([memo-key f]
   (let [!cache (-> r/*owner* meta ::cache)]
     (assert !cache "Cannot memoize a cell outside of a another cell")
     (or (@!cache memo-key)
         (let [cell (make-cell f)]
           (swap! !cache assoc memo-key cell)
           cell))))
  ([f]
   (r/make-reaction {:meta (assoc (a/init) ::cache (atom {}))
                     :detached true}
     (fn []
       (let [cell r/*owner*]
         #?(:cljs
            (try (let [^js v (f r/*owner*)]
                   (if (a/promise? v)
                     (do
                       (a/loading! cell)
                       (-> v
                           (.then #(a/complete! cell %))
                           (.catch v #(a/error! cell %))))
                     v)
                   v)
                 (catch js/Error e (a/error! cell e)))
            :clj                                            ;; untested jvm code path
            (try
              (a/complete! cell (f r/*owner*))
              (catch Exception e (a/error! cell e)))))))))

(util/support-clj-protocols
  (deftype WithMeta [cell metadata]
    IMeta
    (-meta [_] (merge (meta cell) metadata))
    IDeref
    (-deref [_] @cell)
    IWatchable
    (-add-watch [_ k f] (add-watch cell k f))
    (-remove-watch [_ k] (remove-watch cell k))
    ISwap
    (-swap! [o f] (swap! cell f))
    (-swap! [o f a] (swap! cell f a))
    (-swap! [o f a b] (swap! cell f a b))
    (-swap! [o f a b xs] (apply swap! cell f a b xs))
    IReset
    (-reset! [o v] (reset! cell v))
    r/IBecome
    (-become [_ to] (r/-become cell to))
    (-extract [_] (r/-extract cell))
    r/IPeek
    (peek [_] (r/peek cell))
    r/ITrackDerefs
    (get-derefs [_] (r/get-derefs cell))
    (set-derefs! [_ new-derefs] (r/set-derefs! cell new-derefs))
    r/IReactiveValue
    (get-watches [_] (r/get-watches cell))
    (set-watches! [_ new-watches] (r/set-watches! cell new-watches))
    (add-on-dispose! [_ f] (r/add-on-dispose! cell f))
    (dispose! [_] (r/dispose! cell))
    (detach! [_] (r/detach! cell))
    (detached? [_] (r/detached? cell))))

(defn cell? [x]
  (::cache (meta x)))




