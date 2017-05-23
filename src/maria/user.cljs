(ns maria.user
  (:require
    [maria.html :refer [html]]
    [maria.messages :as messages])
  (:require-macros
    [maria.user :refer [user-macro]]))

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

(def colors
  "Recognized SVG color keyword names, mapped to their RGB value."
  {"aliceblue" [240, 248, 255]
   "antiquewhite" [250, 235, 215]
   "aqua" [0, 255, 255]
   "aquamarine" [127, 255, 212]
   "azure" [240, 255, 255]
   "beige" [245, 245, 220]
   "bisque" [255, 228, 196]
   "black" [0, 0, 0]
   "blanchedalmond" [255, 235, 205]
   "blue" [0, 0, 255]
   "blueviolet" [138, 43, 226]
   "brown" [165, 42, 42]
   "burlywood" [222, 184, 135]
   "cadetblue" [95, 158, 160]
   "chartreuse" [127, 255, 0]
   "chocolate" [210, 105, 30]
   "coral" [255, 127, 80]
   "cornflowerblue" [100, 149, 237]
   "cornsilk" [255, 248, 220]
   "crimson" [220, 20, 60]

   "cyan" [0, 255, 255]
   "darkblue" [0, 0, 139]
   "darkcyan" [0, 139, 139]
   "darkgoldenrod" [184, 134, 11]
   "darkgray" [169, 169, 169]
   "darkgreen" [0, 100, 0]
   "darkgrey" [169, 169, 169]
   "darkkhaki" [189, 183, 107]
   "darkmagenta" [139, 0, 139]
   "darkolivegreen" [85, 107, 47]
   "darkorange" [255, 140, 0]
   "darkorchid" [153, 50, 204]
   "darkred" [139, 0, 0]
   "darksalmon" [233, 150, 122]
   "darkseagreen" [143, 188, 143]
   "darkslateblue" [72, 61, 139]
   "darkslategray" [47, 79, 79]
   "darkslategrey" [47, 79, 79]
   "darkturquoise" [0, 206, 209]

   "darkviolet" [148, 0, 211]
   "deeppink" [255, 20, 147]
   "deepskyblue" [0, 191, 255]
   "dimgray" [105, 105, 105]
   "dimgrey" [105, 105, 105]
   "dodgerblue" [30, 144, 255]
   "firebrick" [178, 34, 34]
   "floralwhite" [255, 250, 240]
   "forestgreen" [34, 139, 34]
   "fuchsia" [255, 0, 255]
   "gainsboro" [220, 220, 220]
   "ghostwhite" [248, 248, 255]
   "gold" [255, 215, 0]
   "goldenrod" [218, 165, 32]
   "gray" [128, 128, 128]
   "grey" [128, 128, 128]
   "green" [0, 128, 0]
   "greenyellow" [173, 255, 47]
   "honeydew" [240, 255, 240]

   "hotpink" [255, 105, 180]
   "indianred" [205, 92, 92]
   "indigo" [75, 0, 130]
   "ivory" [255, 255, 240]
   "khaki" [240, 230, 140]
   "lavender" [230, 230, 250]
   "lavenderblush" [255, 240, 245]
   "lawngreen" [124, 252, 0]
   "lemonchiffon" [255, 250, 205]
   "lightblue" [173, 216, 230]
   "lightcoral" [240, 128, 128]
   "lightcyan" [224, 255, 255]
   "lightgoldenrodyellow" [250, 250, 210]
   "lightgray" [211, 211, 211]
   "lightgreen" [144, 238, 144]
   "lightgrey" [211, 211, 211]    	
   "lightpink" [255, 182, 193]
   "lightsalmon" [255, 160, 122]
   "lightseagreen" [32, 178, 170]
   "lightskyblue" [135, 206, 250]
   "lightslategray" [119, 136, 153]

   "lightslategrey" [119, 136, 153]
   "lightsteelblue" [176, 196, 222]
   "lightyellow" [255, 255, 224]
   "lime" [0, 255, 0]
   "limegreen" [50, 205, 50]
   "linen" [250, 240, 230]
   "magenta" [255, 0, 255]
   "maroon" [128, 0, 0]
   "mediumaquamarine" [102, 205, 170]
   "mediumblue" [0, 0, 205]
   "mediumorchid" [186, 85, 211]
   "mediumpurple" [147, 112, 219]
   "mediumseagreen" [60, 179, 113]
   "mediumslateblue" [123, 104, 238]
   "mediumspringgreen" [0, 250, 154]
   "mediumturquoise" [72, 209, 204]
   "mediumvioletred" [199, 21, 133]
   "midnightblue" [25, 25, 112]
   "mintcream" [245, 255, 250]
   "mistyrose" [255, 228, 225]
   "moccasin" [255, 228, 181]
   "navajowhite" [255, 222, 173]
   "navy" [0, 0, 128]

   "oldlace" [253, 245, 230]
   "olive" [128, 128, 0]
   "olivedrab" [107, 142, 35]
   "orange" [255, 165, 0]
   "orangered" [255, 69, 0]
   "orchid" [218, 112, 214]
   "palegoldenrod" [238, 232, 170]
   "palegreen" [152, 251, 152]
   "paleturquoise" [175, 238, 238]
   "palevioletred" [219, 112, 147]
   "papayawhip" [255, 239, 213]
   "peachpuff" [255, 218, 185]
   "peru" [205, 133, 63]
   "pink" [255, 192, 203]
   "plum" [221, 160, 221]
   "powderblue" [176, 224, 230]
   "purple" [128, 0, 128]
   "red" [255, 0, 0]
   "rosybrown" [188, 143, 143]
   "royalblue" [65, 105, 225]
   "saddlebrown" [139, 69, 19]
   "salmon" [250, 128, 114]
   "sandybrown" [244, 164, 96]
   "seagreen" [46, 139, 87]
   "seashell" [255, 245, 238]
   "sienna" [160, 82, 45]

   "silver" [192, 192, 192]
   "skyblue" [135, 206, 235]
   "slateblue" [106, 90, 205]
   "slategray" [112, 128, 144]
   "slategrey" [112, 128, 144]
   "snow" [255, 250, 250]
   "springgreen" [0, 255, 127]
   "steelblue" [70, 130, 180]
   "tan" [210, 180, 140]
   "teal" [0, 128, 128]
   "thistle" [216, 191, 216]
   "tomato" [255, 99, 71]
   "turquoise" [64, 224, 208]
   "violet" [238, 130, 238]
   "wheat" [245, 222, 179]
   "white" [255, 255, 255]
   "whitesmoke" [245, 245, 245]
   "yellow" [255, 255, 0]
   "yellowgreen" [154, 205, 50]})


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
