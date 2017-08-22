(ns cells.eval-context)

(defprotocol IDispose
  "Cells interact with evaluation contexts. In particular, cells are designed
   to be programmed from within interactive interfaces where users will re-evaluate
   code frequently. Facility must be provided for 'disposing' of things like
   intervals. "
  (on-dispose [context f] "Register a callback to be fired when context is disposed.")
  (-dispose! [context]))

(defn dispose! [value]
  (when (satisfies? IDispose value)
    (-dispose! value)))

(defprotocol IHandleError
  (handle-error [this e]))

(defn new-context []
  (let [-context-state (volatile! {:dispose-fns #{}})]
    (reify
      IDispose
      (on-dispose [context f]
        (vswap! -context-state update :dispose-fns conj f))
      (-dispose! [context]
        (doseq [f (:dispose-fns @-context-state)]
          (f))
        (vswap! -context-state update :dispose-fns empty))
      IHandleError
      (handle-error [this e] (throw e)))))