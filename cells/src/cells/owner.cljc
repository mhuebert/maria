(ns cells.owner
  (:require [chia.util.macros :as m]
            [applied-science.js-interop :as j]))

(defprotocol IDispose
  (on-dispose [context key f]
    "Register a callback to be fired when context is disposed.")
  (-dispose! [context]))

(defn dispose! [owner]
  (when (satisfies? IDispose owner)
    (-dispose! owner)))

#?(:cljs
   (defn simple-owner []
     (let [-state (j/obj .-fns {})]
       (reify
         IDispose
         (on-dispose [context key f]
           (j/update! -state .-fns assoc key f))
         (-dispose! [context]
           (doseq [f (vals (.-fns -state))]
             (f))
           (j/update! -state .-fns empty))))))

#?(:cljs (def ^:dynamic *owner* (simple-owner)))

