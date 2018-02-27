(ns shapes.core)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Support for _An Introduction to Racket with Pictures_-style pedagogy

(defprotocol IListen
  (-listen [this listeners]
           "Attach event listeners to shape."))

(defn listen
  ([event listener shape]
   (-listen shape [event listener]))
  ([event-1 listener-1
    event-2 listener-2 & args]
   (-listen (last args)
            (into [event-1 listener-1
                   event-2 listener-2]
                  (butlast args)))))

(deftype Shape [attrs]
  IListen
  (-listen [this listeners]
    (new Shape (reduce (fn [attrs [event listener]]
                         (assoc attrs (keyword (str "on-" (name event))) listener))
                       attrs
                       (partition 2 listeners))))
  IDeref
  (-deref [this] attrs)
  ILookup
  (-lookup [this k]
    (get attrs k))
  (-lookup [this k not-found]
    (get attrs k not-found))
  IAssociative
  (-assoc [this key val]
    (new Shape (assoc attrs key val)))
  (-contains-key? [this key]
    (contains? attrs key))
  IMap
  (-dissoc [this key]
    (new Shape (dissoc attrs key)))
  ISeqable
  (-seq [this] (seq attrs))
  IPrintWithWriter
  (-pr-writer [this writer _]
    (write-all writer (pr-str attrs))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; helpers for param checks

(defn assert-number [message x]
  (if (js/isNaN x)
    (throw (js/Error. message))
    (js/parseFloat x)))

(defn assert-number-range [message x-min x-max x]
  (let [x-parsed (assert-number message x)]
    (if (<= x-min x-parsed x-max)
      x-parsed
      (throw (js/Error. message)))))

(defn circle
  "Returns a circle of `radius`."
  [radius]
  (assert-number "radius must be a number!" radius)
  (->Shape {:is-a   :shape
            :kind   :circle
            :r      radius
            :cx     radius
            :cy     radius
            :stroke "none"
            :fill   "black"}))

(defn ellipse [radius-x radius-y]
  (assert-number "radius-x must be a number!" radius-x)
  (assert-number "radius-y must be a number!" radius-y)
  (->Shape {:is-a   :shape
            :kind   :ellipse
            :rx     radius-x
            :ry     radius-y
            :cx     radius-x
            :cy     radius-y
            :x      (* 1.5 radius-x)
            :y      (* 1.5 radius-y)
            :stroke "none"
            :fill   "black"}))

(defn rectangle
  "Returns a rectangle of `width` and `height`. See also `square`."
  [width height]
  (assert-number "width must be a number!" width)
  (assert-number "height must be a number!" height)
  (->Shape {:is-a   :shape
            :kind   :rect
            :x      0
            :y      0
            :width  width
            :height height
            :stroke "none"
            :fill   "black"}))

(defn square
  "Returns a square of dimension `side`."
  [side]
  (assert-number "side must be a number!" side)
  (rectangle side side))

(defn triangle
  "Returns an equilateral triangle with sides of `size`."
  [size]
  (assert-number "size must be a number!" size)
  (->Shape (let [h (* 0.8660259 size)]
             {:is-a   :shape
              :kind   :polygon
              :x      0
              :y      0
              :width  size
              :height size
              :points [[0 size]
                       [(/ size 2) (- size h)]
                       [size size]]
              :stroke "none"
              :fill   "black"})))

(defn image
  "Add an image to the drawing"
  ([src] (image 200 200 src))
  ([size src]
   (assert-number "size must be a number!" size)
   (image size size src))
  ([width height src]
  (assert-number "width must be a number!" width)
  (assert-number "height must be a number!" height)  
   (->Shape {:kind   :image
             :href   src
             :width  width
             :height height})))

(defn text
  "Add a label containing `the-text` to a drawing."
  [the-text]
  (->Shape {:is-a        :shape
            :kind        :text
            :text        the-text
            :x           0
            :y           18
            :font-family "Fira Code"
            :font-size   15
            :font-weight "normal"
            :width       (* 9 (count the-text))
            :height      18
            :fill        "#3f4245"}))

;; (defn line [x1 y1 x2 y2]
;;   (str " M" x1 " " y1 " L" x2 " " y2))

;; XXX no verification yet, too complicated for the moment
(defn path
  "Create an arbitrary path from a set of points."
  [& points]
  (->Shape {:is-a   :shape
            :kind   :path
            :width  100
            :height 100
            :d      points
            :stroke "black"
            :fill   "none"}))

;; TODO generalize (current forces numeric types) and probably rename
(defn value-to-cell! [the-cell & cell-path]
  (fn [event]
    (let [v (js/parseFloat (.-value (.-target event)))]
      (swap! the-cell (if cell-path
                        #(assoc-in % cell-path v)
                        #(identity v))))))

(defn points-to-path
  "A helper function to create a path-compatible description of a shape from a set of points."
  [points]
  (->> (drop 2 points)
       (partition 2)
       (mapcat (fn [[x y]] [:L x y]))
       (into [:M (first points) (second points)])
       (apply path)))

;; TODO add general polygon fn
;; TODO add spec annotations!

(defn assure-shape-seq
  "Returns `shape-or-shapes` wrapped in a vector if it appears to be a single shape."
  [shape-or-shapes]
  (cond (instance? Shape shape-or-shapes) [shape-or-shapes]
        (instance? Shape (first shape-or-shapes)) shape-or-shapes
        :else (assure-shape-seq (first shape-or-shapes))))

(defn scale-points
  "Scale a set of `points` by `factor`."
  [points factor]
  (map #(if (number? %) (* factor %) %) points))

(defn points-bounds [pts]
  (reduce (fn [[x-min y-min x-max y-max] [x y]]
            [(if (< x x-min) x x-min)
             (if (< y y-min) y y-min)
             (if (> x x-max) x x-max)
             (if (> y y-max) y y-max)])
          [100000 100000 -100000 -100000]                   ;; buggy because not max positive/negative
          (partition 2 (filter number? pts))))

(defn shape-bounds
  "Returns a map containing :height :width keys that represent the outer (i.e. highest) x/y position for this shape."
  [shape]
  {:width  (+ (or (:x shape) 0)
              (or (:cx shape) 0)
              (or (:width shape) 0)
              (or (:r shape) 0)
              (if-let [pts (:d shape)]
                (let [[x-min y-min x-max y-max] (points-bounds pts)]
                  x-max)
                0))
   :height (+ (or (:y shape) 0)
              (or (:cy shape) 0)
              (or (:height shape) 0)
              (or (:r shape) 0)
              (if-let [pts (:d shape)]
                (let [[x-min y-min x-max y-max] (points-bounds pts)]
                  y-max)
                0))})

(defn center-point [shape]
  (let [bounds (shape-bounds shape)]
    [(+ (/ (- (:width bounds) (or (:x shape) 0)) 2) (or (:x shape) 0))
     (+ (/ (- (:height bounds) (or (:y shape) 0)) 2) (or (:y shape) 0))]))

(defn bounds
  "Returns a map containing :height :width keys that represent the outer (i.e. highest) x/y position for this group of shapes."
  [shapes]
  (let [shapes (assure-shape-seq shapes)
        bounds (map shape-bounds shapes)]
    {:width  (:width (apply max-key :width bounds))
     :height (:height (apply max-key :height bounds))}))

(defn points->string [pts]
  (->> (map (partial interpose ",") pts)
       (interpose " ")
       flatten
       (apply str)))

(defn path-points->string [pts]
  (->> (map #(if (keyword? %) (str (name %)) %) pts)
       (interpose " ")
       (apply str)))

;; TODO rotate can take two more numbers for the centrum, would
;; probably be better for each thing to rotate around itself
;; TODO SVG translation/rotation causes slipping, may need to re-calc bounds
;; TODO re-implement this mess using a transform matrix
(defn shape->vector [shape]
  (let [{:keys [kind children d text] :as attrs} @shape
        unkinded (dissoc attrs :is-a :kind)]
    (into [kind (reduce                                     ;; clean up/preprocess the string-y bits of the SVG element
                  (fn [m [k v]]
                    (case k
                      :d (update m :d path-points->string)
                      :points (update m :points points->string)
                      :rotate (case kind
                                :path (let [[x-min y-min x-max y-max] (points-bounds d)]
                                        (update m :transform #(str (or % "") " rotate(" (:rotate m) "," (- x-max x-min) "," (- y-max y-min) ")")))
                                #_(update m :transform #(str (or % "") " rotate(" (:rotate m) "," (:x attrs) "," (:y attrs) ")"))
                                (let [[x y] (center-point m)]
                                  (update m :transform #(str (or % "") " rotate(" (:rotate m) "," x "," y ")"))))
                      :translate (update m :transform #(str (or % "") " translate(" (apply str (interpose "," (:translate m))) ")"))
                      :scale (update m :transform #(str (or % "") " scale(" (:scale m) ")"))
                      m))
                  unkinded
                  (select-keys unkinded [:d :points :rotate :translate :scale]))
           text]
          (mapv shape->vector children))))

(defn to-hiccup [shape]
  [:svg (assoc (bounds shape) :x 0 :y 0)
   (shape->vector shape)])

(defn stroke
  "Return `shape` with its stroke set to `color`."
  [color shape]
  (if (= :svg (:kind shape))
    (assoc shape :children (mapv (partial stroke color) (:children shape)))
    (assoc shape :stroke color)))

(defn stroke-width
  "Return `shape` with its stroke set to `color`."
  [width shape]
  (if (= :svg (:kind shape))
    (assoc shape :children (mapv (partial stroke-width width) (:children shape)))
    (assoc shape :stroke-width width)))

(defn no-stroke
  "Return `shape` with its stroke color turned off."
  [shape]
  (if (= :svg (:kind shape))
    (assoc shape :children (mapv no-stroke (:children shape)))
    (assoc shape :stroke "none")))

(defn fill
  "Return `shape` with its fill set to `color`."
  [color shape]
  (if (= :svg (:kind shape))
    (assoc shape :children (mapv (partial fill color) (:children shape)))
    (assoc shape :fill color)))

(defn no-fill
  "Return `shape` with its fill color turned off."
  [shape]
  (if (= :svg (:kind shape))
    (assoc shape :children (mapv no-fill (:children shape)))
    (assoc shape :fill "none")))

(defn colorize
  "Return `shape` with its color set to `color`."
  [color shape]
  (stroke color shape)
  (fill color shape))

;; XXX SVG transforms are a fucking mess, so this needs to be fixed
(defn scale
  "Return `shape` scaled by `amount`."
  [amount shape]
  (assert-number "amount must be a number!" amount)
  (case (:kind shape)
    :path (update shape :d scale-points amount)
    (assoc shape :scale amount)))

;; XXX SVG transforms are a fucking mess, so this needs to be fixed
(defn rotate
  "Return `shape` with rotated by `amount`."
  [amount shape]
  (assert-number "amount must be a number!" amount)
  (if (not= :circle (:kind shape))
    (assoc shape :rotate amount)))

(defn move-points [m new-x new-y]
  (let [old-x (:x m)
        old-y (:y m)]
    (assoc m :points (mapv (fn [[x y]]
                             [(+ new-x (- x old-x))
                              (+ new-y (- y old-y))])
                           (:points m)))))

(defn position
  "Return `shape` with its x and y positions set to `x` and `y`."
  [x y shape]
  (assert-number "x must be a number!" x)
  (assert-number "y must be a number!" y)
  (case (:kind shape)
    :circle (-> shape
                (assoc :cx x)
                (assoc :cy y))
    :polygon (-> (move-points shape x y)
                 (assoc :x x)
                 (assoc :y y))
    :path (-> (assoc shape :translate [x y])
              (assoc :x x)
              (assoc :y y))
    (-> shape
        (assoc :x x)
        (assoc :y y))))

(defn layer
  "Returns a new shape with these `shapes` layered over each other."
  [& shapes]
  (->Shape (assoc (bounds shapes)
                  :is-a :shape
                  :kind :svg
                  :x 0
                  :y 0
                  :children (remove nil? shapes))))

;; XXX broken for triangles!
(defn beside
  "Return `shapes` with their positions adjusted so they're lined up beside one another."
  [& shapes]
  (->> shapes
       (remove nil?)
       assure-shape-seq
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
       (apply layer)))

(defn above
  "Return `shapes` with their positions adjusted so they're stacked above one another."
  [& shapes]
  (->> shapes
       (remove nil?)
       assure-shape-seq
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
       (apply layer)))

;;;; from Henderson's functional geometry
;; flip   : picture → picture (Flip a picture along its vertical center axis)
;; rot    : picture → picture (Rotate a picture anti-clockwise by 90°)
;; rot45  : picture → picture (rotate the picture anti-clockwise by 45°)

;;;; from "Creative Scala" intro:
;; "click to reveal" solutions are a good idea
;; stroke vs fill? more complicated, but useful.

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; color helpers and scaling fn

(defn opacity
  "Set the opacity of the shape to `o`, which should be a decimal number between 0 and 1.0"
  [o shape]
  (assert-number-range "opacity must be a number between 0 and 1.0!" 0 1.0 o)
  (assoc shape :opacity (str o)))

(defn rgb [red green blue]
  "Returns a color of `red`, `green`, `blue`, each represented as a number from (0-255)."
  (assert-number-range "red must be a number between 0 and 255!" 0 255 red)
  (assert-number-range "green must be a number between 0 and 255!" 0 255 green)
  (assert-number-range "blue must be a number between 0 and 255!" 0 255 blue)
  (apply str "rgb(" red "," green "," blue ")"))

(defn rgba [red green blue alpha]
  "Returns a color of `red`, `green`, `blue`, each represented as a number from (0-255), with an opacity of `alpha` (0.0-1.0)."
  (assert-number-range "red must be a number between 0 and 255!" 0 255 red)
  (assert-number-range "green must be a number between 0 and 255!" 0 255 green)
  (assert-number-range "blue must be a number between 0 and 255!" 0 255 blue)
  (assert-number-range "alpha must be a number between 0 and 1.0!" 0 1.0 alpha)
  (apply str "rgba(" red "," green "," blue "," alpha ")"))

(defn hsl
  "Returns a color of `hue` (a number between 0-359 representing an angle on the color wheel), `saturation` percentage and `lightness` percentage."
  [hue saturation lightness]
  (assert-number-range "hue must be a number between 0 and 359!" 0 359 hue)
  (assert-number-range "saturation must be a number between 0 and 100!" 0 100 saturation)
  (assert-number-range "lightness must be a number between 0 and 100!" 0 100 lightness)
  (apply str "hsl(" hue "," saturation "%," lightness "%)"))

(defn hsla
  "Returns a color of `hue` (a number between 0-359 representing an angle on the color wheel)/`saturation` (percentage)/`lightness` (percentage)/`alpha` (0.0-1.0)."
  [hue saturation lightness alpha]
  (assert-number-range "hue must be a number between 0 and 359!" 0 359 hue)
  (assert-number-range "saturation must be a number between 0 and 100!" 0 100 saturation)
  (assert-number-range "lightness must be a number between 0 and 100!" 0 100 lightness)
  (assert-number-range "alpha must be a number between 0 and 1.0!" 0 1.0 alpha)
  (apply str "hsla(" hue "," saturation "%," lightness "%, " alpha ")"))

(defn rescale 
  "Rescales value from range [old-min, old-max] to [new-min, new-max]"
  [value old-min old-max new-min new-max]
  (assert-number "value must be a number!" value)
  (assert-number "old-min must be a number!" old-min)
  (assert-number "old-max must be a number!" old-max)
  (assert-number "new-min must be a number!" new-min)
  (assert-number "new-max must be a number!" new-max)
  (let [old-spread (- old-max old-min)
        new-spread (- new-max new-min)]
    (+ (* (- value old-min) (/ new-spread old-spread))
       new-min)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; color name dictionary

(def color-names
  "Recognized SVG color keyword names, mapped to their RGB value."
  (mapv (fn [[color-name _]]
          [color-name (colorize color-name (square 25))])
        {"aliceblue"            [240, 248, 255]
         "antiquewhite"         [250, 235, 215]
         "aqua"                 [0, 255, 255]
         "aquamarine"           [127, 255, 212]
         "azure"                [240, 255, 255]
         "beige"                [245, 245, 220]
         "bisque"               [255, 228, 196]
         "black"                [0, 0, 0]
         "blanchedalmond"       [255, 235, 205]
         "blue"                 [0, 0, 255]
         "blueviolet"           [138, 43, 226]
         "brown"                [165, 42, 42]
         "burlywood"            [222, 184, 135]
         "cadetblue"            [95, 158, 160]
         "chartreuse"           [127, 255, 0]
         "chocolate"            [210, 105, 30]
         "coral"                [255, 127, 80]
         "cornflowerblue"       [100, 149, 237]
         "cornsilk"             [255, 248, 220]
         "crimson"              [220, 20, 60]

         "cyan"                 [0, 255, 255]
         "darkblue"             [0, 0, 139]
         "darkcyan"             [0, 139, 139]
         "darkgoldenrod"        [184, 134, 11]
         "darkgray"             [169, 169, 169]
         "darkgreen"            [0, 100, 0]
         "darkgrey"             [169, 169, 169]
         "darkkhaki"            [189, 183, 107]
         "darkmagenta"          [139, 0, 139]
         "darkolivegreen"       [85, 107, 47]
         "darkorange"           [255, 140, 0]
         "darkorchid"           [153, 50, 204]
         "darkred"              [139, 0, 0]
         "darksalmon"           [233, 150, 122]
         "darkseagreen"         [143, 188, 143]
         "darkslateblue"        [72, 61, 139]
         "darkslategray"        [47, 79, 79]
         "darkslategrey"        [47, 79, 79]
         "darkturquoise"        [0, 206, 209]

         "darkviolet"           [148, 0, 211]
         "deeppink"             [255, 20, 147]
         "deepskyblue"          [0, 191, 255]
         "dimgray"              [105, 105, 105]
         "dimgrey"              [105, 105, 105]
         "dodgerblue"           [30, 144, 255]
         "firebrick"            [178, 34, 34]
         "floralwhite"          [255, 250, 240]
         "forestgreen"          [34, 139, 34]
         "fuchsia"              [255, 0, 255]
         "gainsboro"            [220, 220, 220]
         "ghostwhite"           [248, 248, 255]
         "gold"                 [255, 215, 0]
         "goldenrod"            [218, 165, 32]
         "gray"                 [128, 128, 128]
         "grey"                 [128, 128, 128]
         "green"                [0, 128, 0]
         "greenyellow"          [173, 255, 47]
         "honeydew"             [240, 255, 240]

         "hotpink"              [255, 105, 180]
         "indianred"            [205, 92, 92]
         "indigo"               [75, 0, 130]
         "ivory"                [255, 255, 240]
         "khaki"                [240, 230, 140]
         "lavender"             [230, 230, 250]
         "lavenderblush"        [255, 240, 245]
         "lawngreen"            [124, 252, 0]
         "lemonchiffon"         [255, 250, 205]
         "lightblue"            [173, 216, 230]
         "lightcoral"           [240, 128, 128]
         "lightcyan"            [224, 255, 255]
         "lightgoldenrodyellow" [250, 250, 210]
         "lightgray"            [211, 211, 211]
         "lightgreen"           [144, 238, 144]
         "lightgrey"            [211, 211, 211]
         "lightpink"            [255, 182, 193]
         "lightsalmon"          [255, 160, 122]
         "lightseagreen"        [32, 178, 170]
         "lightskyblue"         [135, 206, 250]
         "lightslategray"       [119, 136, 153]

         "lightslategrey"       [119, 136, 153]
         "lightsteelblue"       [176, 196, 222]
         "lightyellow"          [255, 255, 224]
         "lime"                 [0, 255, 0]
         "limegreen"            [50, 205, 50]
         "linen"                [250, 240, 230]
         "magenta"              [255, 0, 255]
         "maroon"               [128, 0, 0]
         "mediumaquamarine"     [102, 205, 170]
         "mediumblue"           [0, 0, 205]
         "mediumorchid"         [186, 85, 211]
         "mediumpurple"         [147, 112, 219]
         "mediumseagreen"       [60, 179, 113]
         "mediumslateblue"      [123, 104, 238]
         "mediumspringgreen"    [0, 250, 154]
         "mediumturquoise"      [72, 209, 204]
         "mediumvioletred"      [199, 21, 133]
         "midnightblue"         [25, 25, 112]
         "mintcream"            [245, 255, 250]
         "mistyrose"            [255, 228, 225]
         "moccasin"             [255, 228, 181]
         "navajowhite"          [255, 222, 173]
         "navy"                 [0, 0, 128]

         "oldlace"              [253, 245, 230]
         "olive"                [128, 128, 0]
         "olivedrab"            [107, 142, 35]
         "orange"               [255, 165, 0]
         "orangered"            [255, 69, 0]
         "orchid"               [218, 112, 214]
         "palegoldenrod"        [238, 232, 170]
         "palegreen"            [152, 251, 152]
         "paleturquoise"        [175, 238, 238]
         "palevioletred"        [219, 112, 147]
         "papayawhip"           [255, 239, 213]
         "peachpuff"            [255, 218, 185]
         "peru"                 [205, 133, 63]
         "pink"                 [255, 192, 203]
         "plum"                 [221, 160, 221]
         "powderblue"           [176, 224, 230]
         "purple"               [128, 0, 128]
         "red"                  [255, 0, 0]
         "rosybrown"            [188, 143, 143]
         "royalblue"            [65, 105, 225]
         "saddlebrown"          [139, 69, 19]
         "salmon"               [250, 128, 114]
         "sandybrown"           [244, 164, 96]
         "seagreen"             [46, 139, 87]
         "seashell"             [255, 245, 238]
         "sienna"               [160, 82, 45]

         "silver"               [192, 192, 192]
         "skyblue"              [135, 206, 235]
         "slateblue"            [106, 90, 205]
         "slategray"            [112, 128, 144]
         "slategrey"            [112, 128, 144]
         "snow"                 [255, 250, 250]
         "springgreen"          [0, 255, 127]
         "steelblue"            [70, 130, 180]
         "tan"                  [210, 180, 140]
         "teal"                 [0, 128, 128]
         "thistle"              [216, 191, 216]
         "tomato"               [255, 99, 71]
         "turquoise"            [64, 224, 208]
         "violet"               [238, 130, 238]
         "wheat"                [245, 222, 179]
         "white"                [255, 255, 255]
         "whitesmoke"           [245, 245, 245]
         "yellow"               [255, 255, 0]
         "yellowgreen"          [154, 205, 50]}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; pre-cooked SVG shapes

;; (def fish
;;   "An Escher-style fish."
;;   {:is-a   :shape
;;    :kind   :path
;;    :width  100
;;    :height 100
;;    :d      [:M 9.67 40.17 :C 6.86 37.67 4.49 35.56 2.63 32.93 :c 0 -0.12 0.17 -0.54 0.37 -0.94 :c 1.15 -2.28 2.17 -3.75 4.73 -6.83 :l 1.37 -1.64 :l -0.07 -0.7 :C 8.65 18.39 8.87 15.11 9.97 10.9 :C 10.11 9.63 10.41 10.28 12.04 11.5 :C 13.89 12.99 15.6 13.84 17.34 16.64 :l 2.17 0.09 :c 2.46 0.11 3.46 0.27 4.87 0.72 :l 1.06 0.34 :c 0.01 1.38 0.02 2.76 0.03 4.15 :c 0.01 1.19 0.02 2.38 0.03 3.57 :c -1.23 1.23 -2.53 2.71 -3.76 3.94 :c 1.65 2.68 6.11 4.18 9.99 4.57 :l 1.6 0.14 :l 1.12 1.32 :c 1.26 1.48 2.04 2.15 4.23 3.65 :l 1.52 1.04 :c 0.16 0.09 0.97 0.57 -0.18 0.46 :l -2.26 -0.43 :c -3.09 -0.59 -4.51 -0.78 -5.89 -0.78 :c -1.1 -0 -3.28 0.14 -4.34 0.29 :c -0.45 0.06 -0.52 0.03 -1.64 -0.91 :c -3.5 -2.93 -4.67 -3.77 -6.86 -4.93 :l -1.23 -0.66 :c -2.44 2.44 -4.88 4.88 -7.32 7.32 :c -0.26 0.37 -0.82 -0.35 -0.82 -0.35 :z :M 17.41 33.02 :C 16.08 32.32 14.9 31.64 13.36 30.72 :l -0.79 0.76 :c -0.41 0.4 -1.24 0.97 -1.7 1.43 :c -0.74 0.73 -0.93 1.02 -1.58 2.38 :c -0.68 1.41 -1.04 1.88 -1.03 1.34 :c 0 -0.35 1.35 -2.99 1.85 -3.62 :C 10.36 32.68 11.14 32.01 11.81 31.49 :l 1.26 -0.97 :c -0.49 -0.45 -0.94 -0.85 -1.33 -1.29 :c -0.15 -0.06 -2 1.44 -2.65 2.15 :c -0.34 0.37 -1.01 1.32 -1.51 2.12 :c -0.83 1.34 -1.21 1.74 -1.21 1.27 :c 0 -0.24 1.41 -2.49 2.13 -3.41 :c 0.31 -0.39 0.99 -1.05 1.52 -1.47 :c 0.53 -0.41 1.34 -0.98 1.48 -1.09 :C 10.33 26.87 9.96 25.63 9.24 24.03 :l -1.64 2 :c -2.16 2.63 -2.79 3.52 -3.79 5.35 :l -0.83 1.52 :c 0.7 0.58 4.85 5.64 7.26 7.21 :C 12.8 37.65 14.7 35.72 17.41 33.02 :z :M 9.31 38.59 :C 10.54 36.67 13.23 32.93 14.9 31.91 :c -1.08 1.8 -3.52 3.88 -4.84 6.19 :c -0.21 0.37 -0.42 0.61 -0.55 0.61 :c -0.11 0 -0.21 -0.05 -0.21 -0.12 :z :M 4.85 33.31 :c 0 -0.24 1.31 -2.7 1.75 -3.28 :c 0.74 -0.92 3.91 -4.13 4.01 -3.17 :c -1.95 1.23 -4.25 3.78 -5.05 5.85 :c -0.2 0.4 -0.45 0.73 -0.54 0.73 :c -0.1 0 -0.17 -0.06 -0.17 -0.13 :z :m 32.97 5.78 :c -1.94 -1.35 -3.1 -2.45 -4.44 -4.17 :c -0.25 -0.32 -0.31 -0.33 -2.4 -0.55 :c -4.84 -0.43 -9.35 -2.98 -10.59 -6.68 :c -2.28 -5.68 -0.94 -9.92 -6.21 -14.17 :c -0.45 -0.37 -1.49 -1.13 -2.3 -1.69 :L 10.39 10.81 :C 9.03 15.59 8.84 20.53 9.71 23.84 :c 0.58 2.22 1.63 4.4 3.24 5.81 :c 2.36 2.07 8.78 4.31 14.24 9.65 :l 0.64 -0.06 :c 4.11 -0.38 5.35 -0.34 8.91 0.34 :c 1.13 0.21 2.13 0.4 2.22 0.4 :c 0.09 0.01 -0.42 -0.39 -1.14 -0.89 :z :m -6.77 -2.06 :C 23.62 34.03 21.59 33.07 19.55 31.02 :C 16.84 27.84 15.3 24.33 13.75 20.48 :c -0.78 -2.92 0.56 0.34 0.97 1.38 :c 0.96 2.41 2.05 4.09 2.89 5.63 :c 2.35 4.33 5.52 5.98 14.71 9.58 :c 1.52 0.6 2.43 1.03 2.43 1.13 :c 0 0.09 -0.09 0.16 -0.21 0.16 :c -0.11 -0 -1.68 -0.6 -3.49 -1.33 :z :M 10.68 20.22 :C 10.61 19.64 10.41 15.52 11.2 15.35 :c 0.18 -0.03 0.37 0.24 0.55 0.63 :c 0.35 0.75 0.66 1.92 0.74 2.18 :l 0.25 1.01 :C 11.86 19.74 11.2 20.55 10.95 20.52 :C 10.81 20.53 10.73 20.43 10.68 20.22 :z :M 10.97 20.02 :C 11.43 19.57 12.15 19.2 12.4 18.97 :C 11.85 16.61 11.44 15.7 11.24 15.8 :C 10.92 16.97 10.89 18.45 10.97 20.02 :z :M 12.82 14.72 :c 0.33 -0.29 3.16 2.32 3.16 2.63 :c 0 0.06 -0.51 0.36 -1.08 0.63 :c -0.41 0.19 -1 0.47 -1.1 0.47 :c -0.28 -0.27 -1.11 -3.56 -0.98 -3.73 :z :m 0.39 0.5 :c -0.22 -0.15 0.41 2.12 0.67 2.84 :c 0.94 -0.27 1.68 -0.82 1.67 -0.86 :C 14.96 16.6 13.62 15.41 13.2 15.22 :z :m 7.37 10.36 :c -0.29 0 -0.08 -0.36 0.57 -0.98 :c 0.27 -0.25 0.5 -0.46 0.73 -0.61 :c 0.59 -0.4 1.13 -0.53 2.08 -0.55 :l 1.06 -0.02 :c -0.01 -0.49 0.04 -1.15 -0.09 -1.54 :c -0.05 0.03 -0.65 -0.17 -1.07 -0.25 :c -1.23 -0.26 -1.58 -0.14 -2.54 0.32 :c -0.92 0.44 -1.11 0.57 -1.11 0.33 :c 0 -0.08 0.43 -0.36 0.95 -0.62 :c 0.86 -0.42 1.03 -0.46 1.79 -0.44 :c 0.46 0.01 1.11 0.13 1.45 0.19 :l 0.68 0.12 :c 0.01 -0.14 0.02 -0.4 0.04 -0.67 :c 0.02 -0.42 0.03 -0.87 -0 -1 :c -0.99 -0.46 -2.15 -0.84 -3.07 -0.85 :c -0.97 -0.01 -1.86 0 -2.58 0.3 :c -0.32 0.13 -0.5 0.35 -0.75 0.62 :c 0.41 1.7 1.43 7.99 2.76 9.21 :c 1.16 -1.16 2.37 -2.56 3.53 -3.72 :c 0.23 -0.8 0.08 -1.62 -0.06 -1.65 :c -2.29 -0.53 -3.69 0.99 -4.39 1.82 :z :m 4.44 -7.47 :m -0.63 -0.22 :m -1.18 -0.42 :C 22.66 17.28 22.13 17.18 21.55 17.12 :C 20.88 17.06 20.14 17.04 19.22 16.96 :C 18.7 16.92 17.65 16.91 17.5 16.88 :c 0.15 0.31 0.32 0.65 0.49 1.01 :c 0.26 0.54 0.51 1.11 0.62 1.62 :c 0.81 -0.55 1.58 -0.71 2.31 -0.77 :c 0.87 -0.08 1.89 0.1 2.69 0.3 :c 0.69 0.17 1.21 0.35 1.32 0.38 :c 0.13 0.04 0.15 -0.14 0.14 -0.42 :c -0.01 -0.25 -0.05 -0.59 -0.05 -0.89 :z :m 1.48 13.43 :c -0.34 -0.09 -0.81 -0.37 -1.21 -0.71 :c -0.22 -0.19 -0.43 -0.41 -0.57 -0.62 :c -0.12 -0.18 -0.2 -0.35 -0.21 -0.51 :c -0.03 -0.38 0.5 0.46 0.88 0.85 :c 0.13 0.13 0.3 0.25 0.48 0.36 :c 0.36 0.21 0.77 0.39 0.98 0.58 :c -0.01 0.09 -0.14 0.15 -0.36 0.05 :z :m -0.18 -2.17 :c -0.12 -0.08 -0.23 -0.13 -0.33 -0.27 :c -0.13 -0.16 -0.23 -0.36 -0.31 -0.55 :c -0.17 -0.42 -0.28 -0.79 -0.05 -0.79 :c 0.06 0 0.15 0.23 0.24 0.46 :c 0.06 0.16 0.12 0.31 0.16 0.39 :c 0.1 0.19 0.34 0.43 0.53 0.53 :c 0.09 0.46 -0.16 0.28 -0.24 0.23 :z]
;;    :stroke "black"
;;    :fill   "none"})



;The required namespace "thi.ng.math.macros" is not available, it was required by "thi/ng/geom/core/macros/matrix$macros.cljc".
