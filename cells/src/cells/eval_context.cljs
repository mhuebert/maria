(ns cells.eval-context)

(defprotocol IDispose
  "Cells are designed to be used within interactive interfaces where users re-evaluate
   code frequently. Implement the IDispose protocol on an editor context to control the
    'disposal' of side-effects like intervals when code is (re)-evaluated."
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