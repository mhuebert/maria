(ns maria.editors.editors
  (:require [maria-commands.exec :as exec]
            [re-view.core :as v]))

(def view-index (volatile! {}))

(defn view [this]
  (@view-index (:id this)))

(defn mount [view]
  (let [id (get-in view [:block :id])]
    (assert (nil? (@view-index id)))                        ;; multiple simultaneous editors of a block are not yet supported
    (vswap! view-index assoc id view)))

(defn unmount [view]
  (vswap! view-index dissoc (:id (:block view))))

(defn focused-block []
  (get-in @exec/context [:block-view :block]))

(defn of-block [block]
  (when-not (view block)
    (v/flush!))
  (some-> (view block)
          (.getEditor)))

(defprotocol IKind
  (kind [this]))

(defprotocol IHistory
  (get-selections [this])
  (put-selections! [this selections]))

(defprotocol ICursor
  (get-cursor [this])
  (cursor-coords [this])

  (start [this])
  (end [this])

  (selection-expand [this])
  (selection-contract [this])

  (-focus! [this coords]))

(defn focus!
  ([editor]
   (focus! editor nil))
  ([editor coords]
   (-focus! editor coords)))

(defn at-start? [editor]
  (some-> (get-cursor editor)
          (= (start editor))))

(defn at-end? [editor]
  (= (get-cursor editor)
     (end editor)))

(defn focused-block-view []
  (:block-view @exec/context))