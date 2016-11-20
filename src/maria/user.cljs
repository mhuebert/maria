(ns maria.user
  (:require [maria.html :refer [html]]
            [maria.eval]
            [cljs.repl :as repl :include-macros true])
  (:require-macros [maria.user :refer [user-macro]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Support for _An Introduction to Racket with Pictures_-style pedagogy

;; TODO we should have a protocol something like this:

;; (defprotocol ToComponent
;;   "Records that satisfy this protocol know how to turn themselves into React components."
;;   (to-component [this] nil))

;; TODO implemented something like this for shapes:

;; (defrecord Shape []
;;   ToComponent
;;   (to-component [this]
;;     (let [shapes (assure-shape-seq shapes)]
;;       (html (into [:svg (assoc (bounds shapes) :x 0 :y 0)]
;;                   (map shape->vector shapes))))))

;; ... so we can build arbitrary defrecords that know how to format
;; themselves for output. This can work for all sorts of things
;; (images, mathematics, and so on) in addition to shapes.

(defn circle
  "Returns a circle of `radius`."
  [radius]
  {:is-a :shape
   :kind :circle
   :r      radius
   :cx     radius
   :cy     radius
   :stroke "none"
   :fill   "black"})

(defn rectangle
  "Returns a rectangle of `width` and `height`."
  [width height]
  {:is-a :shape
   :kind :rect
   :x      0
   :y      0
   :width  width
   :height height
   :stroke "none"
   :fill   "black"})

(defn assure-shape-seq
  "Returns `shape-or-shapes` wrapped in a vector if it appears to be a single shape."
  [shape-or-shapes]
  (cond (= :shape (:is-a shape-or-shapes))         [shape-or-shapes]
        (= :shape (:is-a (first shape-or-shapes))) shape-or-shapes
        :else (assure-shape-seq (first shape-or-shapes))))

(defn shape-bounds
  "Returns a map containing :height :width keys that represent the outer (i.e. highest) x/y position for this shape."
  [shape]
  {:width  (+ (or (:x shape) 0)
              (or (:cx shape) 0)
              (or (:width shape) 0)
              (or (:r shape) 0))
   :height (+ (or (:y shape) 0)
              (or (:cy shape) 0)
              (or (:height shape) 0)
              (or (:r shape) 0))})

(defn bounds
  "Returns a map containing :height :width keys that represent the outer (i.e. highest) x/y position for this group of shapes."
  [shapes]
  (let [shapes (assure-shape-seq shapes)
        bounds (map shape-bounds shapes)]
    {:width  (:width (apply max-key :width bounds))
     :height (:height (apply max-key :height bounds))}))

(defn shape->vector [shape]
  [(:kind shape)
   (-> shape (dissoc :is-a) (dissoc :kind))
   (when-let [kids (:children shape)]
     (mapv shape->vector kids))])

;; TODO becomes a method of the shape protocol
(defn show
  "Returns a component from this collection of shapes."
  [shapes]
  (let [shapes (assure-shape-seq shapes)]
    (html (into [:svg (assoc (bounds shapes) :x 0 :y 0)]
                (map shape->vector shapes)))))

(defn colorize
  "Return `shape` with its color changed to `color`."
  [color shape]
  (assoc shape :fill color))

(defn position
  "Return `shape` with its position set to `[x y]`."
  [[x y] shape]
  (if (= :circle (:kind shape))
    (-> shape
        (assoc :cx x)
        (assoc :cy y))
    (-> shape
        (assoc :x x)
        (assoc :y y))))

(defn group
  "Returns a group containing `shapes` that can be treated as a single shape."
  [& shapes]
  (assoc (bounds shapes)
         :is-a :shape
         :kind :svg
         :x 0
         :y 0
         :children shapes))

(defn line-up
  "Return `shapes` with their positions adjusted so they're lined up horizontally."
  [& shapes]
  (->> (assure-shape-seq shapes)
       reverse
       (reduce (fn [state shape]
                 {:out    (conj (state :out)
                                (update shape (if (= (:kind shape) :circle)
                                                :cx
                                                :x) + (apply + (:widths state))))
                  :widths (butlast (state :widths))})
               {:out    '()
                :widths (map #(if (= (:kind %) :circle)
                                (:r %)
                                (:width %))
                             (map shape-bounds (butlast shapes)))})
       :out
       (apply group)))

(defn stack
  "Return `shapes` with their positions adjusted so they're stacked vertically."
  [& shapes]
  (->> (assure-shape-seq shapes)
       reverse
       (reduce (fn [state shape]
                 {:out     (conj (state :out)
                                 (update shape (if (= (:kind shape) :circle)
                                                :cy
                                                :y) + (apply + (:heights state))))
                  :heights (butlast (state :heights))})
               {:out     '()
                :heights (map #(if (= (:kind %) :circle)
                                 (:r %)
                                 (:height %))
                              (map shape-bounds (butlast shapes)))})
       :out
       (apply group)))

;; some examples using the above functions
(comment
  
  (stack (colorize "red" (rectangle 20 20))
         (colorize "blue" (rectangle 20 20)))

  (line-up (colorize "red" (rectangle 20 20))
           (colorize "blue" (rectangle 20 20))
           (colorize "green" (rectangle 20 20)))

  (stack
   (stack (colorize "red" (rectangle 20 20))
          (colorize "blue" (rectangle 20 20))
          (colorize "green" (rectangle 20 20)))
   (line-up (colorize "orange" (rectangle 20 20))
            (colorize "red" (rectangle 20 20))
            (colorize "blue" (rectangle 20 20))
            (colorize "green" (rectangle 20 20))))

  ;; vector rainbow
  (mapv colorize
        ["red" "orange" "yellow" "green" "blue" "purple"]
        (repeat (rectangle 20 20)))

  ;; combined rainbow, no vector
  (apply stack
         (mapv colorize
               ["red" "orange" "yellow" "green" "blue" "purple"]
               (repeat (rectangle 20 20))))

  ;; maybe nice way to talk about naming things?
  (let [rainbow (fn [shape]
                  (mapv colorize
                        ["red" "orange" "yellow" "green" "blue" "purple"]
                        (repeat shape)))]
    (rainbow (rectangle 20 20)))
  
  ;; mixed types
  ["hi!"
   (stack (colorize "red" (rectangle 20 20))
          (colorize "blue" (rectangle 20 20)))
   '🦄
   (line-up (colorize "red" (rectangle 20 20))
            (colorize "blue" (rectangle 20 20))
            (colorize "green" (rectangle 20 20)))]

  ;; parcheesi pieces!
  (map stack
       (map colorize
            ["red" "orange" "yellow" "green" "blue" "purple"]
            (repeat (circle 10)))
       (map colorize
            ["red" "orange" "yellow" "green" "blue" "purple"]
            (repeat (rectangle 20 20))))

  ;;... maybe gradually refactor this, by way of let, into:

  (defn rainbow [shape]
    (map colorize 
         ["red" "orange" "yellow" "green" "blue" "purple"] 
         (repeat shape)))

  (map stack
       (rainbow (circle 10))
       (rainbow (rectangle 20 30)))
  
  ) ;/comment
