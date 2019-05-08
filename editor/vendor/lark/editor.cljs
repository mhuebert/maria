(ns lark.editor
  (:require [chia.view :as v]))

(def view-index (volatile! {}))

(defn view [this]
  (@view-index (:id this)))

(defn mount [view]
  (let [id (get-in view [:block :id])]
    (assert (nil? (@view-index id)))                        ;; multiple simultaneous editors of a block are not yet supported
    (vswap! view-index assoc id view)))

(defn unmount [view]
  (vswap! view-index dissoc (:id (:block view))))

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
  (set-cursor [this position])
  (cursor-coords [this])
  (coords-cursor [this client-x client-y])

  (start [this])
  (end [this])

  (-focus! [this coords]))

(defn focus!
  ([editor]
   (focus! editor nil))
  ([editor coords]
   (-focus! editor coords)
   true))

(defn at-start? [editor]
  (some-> (get-cursor editor)
          (= (start editor))))

(defn at-end? [editor]
  (= (get-cursor editor)
     (end editor)))

(defn scroll-into-view [coords]
  (when-let [scroll-y (cond (neg? (.-top coords))
                            (-> (.-scrollY js/window)
                                (+ (.-top coords))
                                (- 100))
                            (> (.-top coords) (.-innerHeight js/window))
                            (-> (.-scrollY js/window)
                                (+ (- (.-top coords)
                                      (.-innerHeight js/window)))
                                (+ 100))
                            :else nil)]
    (.scrollTo js/window 0 scroll-y)))