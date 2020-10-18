(ns chia.reactive.track
  (:require [chia.reactive :as r]
            [chia.reactive.atom :as ra]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Source implementations
;;

#?(:cljs

   (deftype Track [f args source-atom ^:volatile-mutable initialized?]

     IDeref
     (-deref [this]
       (when (false? initialized?)
         (r/recompute! this)
         (set! initialized? true))
       (ra/deref source-atom))

     IWatchable
     (-add-watch [_ key f]
       (-add-watch source-atom key f))
     (-remove-watch [this key]
       (-remove-watch source-atom key)
       (when (empty? (.-watches source-atom))
         (r/dispose-reader! this)
         (reset! source-atom nil)
         (set! initialized? false)))

     r/IRecompute
     (r/recompute! [this]
       (r/with-dependency-tracking! {:reader this}
         (reset! source-atom (apply f args))))

     IEquiv
     (-equiv [this ^js other]
       (identical? source-atom (.-source-atom other)))

     IHash
     (-hash [this] (-hash source-atom))))

#?(:cljs
   (defn track
     "Returns a lazily-evaluated reactive data source containing the result
      of applying function `f` to optional `args`.

      `f` will be re-evaluated when any of its dependencies (ie. reactive data
      sources which are read while evaluating `f`) change.

      `f` will stop when it has no watchers, and restart when it is watched to again."
     [f & args]
     (Track. f args (atom nil) false)))