(ns maria.blocks.history
  (:require [clojure.set :as set]
            [cells.eval-context :as eval-context]
            [re-view.core :as v]
            [maria.blocks.blocks :as Block]))

(def ^:dynamic *ignored-op* false)

(def -prior-selection (volatile! nil))

(defn update-first-meta [coll f & args]
  (cons (with-meta (first coll) (apply f (meta (first coll)) args))
        (rest coll)))

(defn merge-version? [prev-blocks]
  (let [{[block _] :selections-after
         timestamp :timestamp} (meta prev-blocks)]
    (and (< (- (.now js/Date) timestamp)
            300)
         (= (:id block) (:id (Block/focused-block))))))

(defn get-selections []
  (when-let [block (Block/focused-block)]
    [block (Block/get-history-selections block)]))

(defn before-change
  "Should be called before any change is applied to document.
  Ensures that the correct `before` selections are attached to the version."
  []
  (vreset! -prior-selection (get-selections)))

(defn initial-state [value]
  {:last-update value
   :history     (list (Block/ensure-blocks (Block/from-source value)))})

(defn after-mount
  "Should be called after doc has been mounted to page.
  Focuses doc, and sets correct selections for initial state."
  [state]
  (let [selections (get-selections)]
    (Block/focus! :after-mount (first (first (:history @state))) :start)
    (v/swap-silently! state update :history update-first-meta merge
                      {:selections-before selections
                       :selections-after  selections
                       :timestamp         (.now js/Date)})
    (before-change)))

(defn apply-selections
  "Applies selections for given version to doc."
  [key version]
  (when-let [[block selections] (get (meta version) key)]
    (when block
      (Block/focus! :apply-selections block)
      (when selections
        (Block/put-selections! block selections)))))

(defn dispose-removed
  "Compares prev- and next- block lists and 'disposes' of blocks which have been removed."
  ;; the history system works on frozen states, but blocks have a lifecycle.
  ;; so we have to manually 'dispose' of blocks that are removed due to
  ;; undo/redo operations.
  [prev-version next-version]
  (let [ids (set/difference (set (map :id prev-version))
                            (set (map :id next-version)))
        blocks (filter (comp ids :id) prev-version)]
    (doseq [block blocks]
      (eval-context/dispose! block))))

(defn clear! [state]
  (v/swap-silently! state dissoc :history/redo-stack :history))

(defn undo [state]
  (let [history (:history @state)]
    (when (second history)
      (let [prev-version (first history)
            next-version (second history)]
        (dispose-removed prev-version next-version)
        (binding [*ignored-op* true]
          (reset! state (-> @state
                            (update :history rest)
                            (update :history/redo-stack conj prev-version)))
          (v/flush!)
          (apply-selections :selections-before prev-version))))))

(defn redo [state]
  (when-let [next-blocks (first (:history/redo-stack @state))]
    (binding [*ignored-op* true]
      (dispose-removed (first (:history @state)) next-blocks)
      (reset! state (-> @state
                        (update :history/redo-stack rest)
                        (update :history conj next-blocks)))
      (v/flush!)
      (apply-selections :selections-after next-blocks))))


(defn add! [state version]
  (when-not *ignored-op*
    (binding [*ignored-op* true]
      (let [{:keys [history history/redo-stack]} @state
            prev-version (first history)
            merge? (merge-version? prev-version)
            next-version (with-meta version {:selections-before (if merge? (:selections-before (meta (first history)))
                                                                           @-prior-selection)
                                             :selections-after  (get-selections)
                                             :timestamp         (.now js/Date)})]
        (v/flush!)
        (reset! state (-> @state
                          (cond-> merge? (update :history rest))
                          (update :history conj next-version)
                          (cond-> (seq redo-stack) (update :history/redo-stack empty))))))))

(defn current-version [state]
  (first (:history @state)))

(defn update! [state f & args]
  (add! state (apply f (current-version state) args)))

(defn splice
  ([state block value] (splice state block 0 value))
  ([state block n value]
   (let [next-version (-> (current-version state)
                          (Block/splice-block block n value)
                          (Block/ensure-blocks))]
     (add! state next-version)
     next-version)))