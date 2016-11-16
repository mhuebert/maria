(ns maria.user
  (:require-macros [maria.user :refer [user-macro]]))

(defn user-f [x]
  (take 10 (repeat x)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Support for _An Introduction to Racket with Pictures_-style pedagogy

(defn shape-bounds
  "Returns a map containing :height :width keys that represent the outer (i.e. highest) x/y position for this shape."
  [shape]
  (let [attrs (second shape)]
    {:width (+ (or (:x attrs) 0)
               (or (:cx attrs) 0)
               (or (:width attrs) 0)
               (or (:r attrs) 0))
     :height (+ (or (:y attrs) 0)
                (or (:cy attrs) 0)
                (or (:height attrs) 0)
                (or (:r attrs) 0))}))

(defn bounds
  "Returns a map containing :height :width keys that represent the outer (i.e. highest) x/y position for this group of shapes."
  [shapes]
  (let [shapes (if (keyword? (first shapes)) [shapes] shapes)
        bounds (map shape-bounds shapes)]
    {:width (:width (apply max-key :width bounds))
     :height (:height (apply max-key :height bounds))}))

(defn circle
  "Returns a circle of `radius`."
  [radius]
  [:circle {:r radius
            :cx radius
            :cy radius
            :stroke "none"
            :fill "black"}])

(defn rectangle
  "Returns a rectangle of `width` and `height`."
  [width height]
  [:rect {:x 0
          :y 0
          :width width
          :height height
          :stroke "none"
          :fill "black"}])

(defn colorize
  "Return `shape` with its color changed to `color`."
  [color shape]
  (assoc-in shape [1 :fill] color))

(defn position
  "Return `shape` with its position set to `[x y]`."
  [[x y] shape]
  (if (= :circle (first shape))
    (-> shape
        (assoc-in [1 :cx] x)
        (assoc-in [1 :cy] y))
    (-> shape
        (assoc-in [1 :x] x)
        (assoc-in [1 :y] y))))

(defn group
  "Returns a group containing `shapes` that can be treated as a single shape."
  [& shapes]
  (into [] (concat [:svg (assoc (bounds shapes) :x 0 :y 0)] shapes)))

(defn line-up
  "Return `shapes` with their positions adjusted so they're lined up horizontally."
  [& shapes]
  (->> (reduce (fn [state shape]
                     {:out (conj (state :out)
                                 (update-in shape [1 :x] + (apply + (:widths state))))
                      :widths (butlast (state :widths))})
                   {:out '()
                    :widths (map :width (map shape-bounds (butlast shapes)))}
                   (reverse shapes))
       :out
       (apply group)))

(defn stack
  "Return `shapes` with their positions adjusted so they're stacked vertically."
  [& shapes]
  (->> (reduce (fn [state shape]
                 {:out (conj (state :out)
                             (update-in shape [1 :y] + (apply + (:heights state))))
                  :heights (butlast (state :heights))})
               {:out '()
                :heights (map :height (map shape-bounds (butlast shapes)))}
               (reverse shapes))
       :out
       (apply group)))

(defn show
  "Returns SVG markup for this collection of shapes."
  [shapes]
  (let [shapes (if (keyword? (first shapes)) [shapes] shapes)]
    (html (into [:svg (assoc (bounds shapes) :x 0 :y 0)] shapes))))

;; some examples using the above functions
(comment
  
(stack
 (stack (colorize "red" (rectangle 10 20))
        (colorize "blue" (rectangle 10 20)))
 (line-up (colorize "green" (rectangle 10 20))
        (colorize "pink" (rectangle 10 20))))

(show
 (line-up (colorize "red" (rectangle 20 20))
           (colorize "blue" (rectangle 20 20))
           (colorize "green" (rectangle 20 20))))

(show
 (stack
  (stack (colorize "red" (rectangle 20 20))
         (colorize "blue" (rectangle 20 20))
         (colorize "green" (rectangle 20 20)))
  (line-up (colorize "red" (rectangle 20 20))
           (colorize "blue" (rectangle 20 20))
           (colorize "green" (rectangle 20 20)))))

(defn rainbow [shape]
  (apply line-up
         (map (fn [color]
                (colorize color shape))
              ["red" "orange" "yellow" "green" "blue" "purple"])))

(show (rainbow (rectangle 20 20)))
 
);/comment
