;; # Gallery

;; Here are some ways to use shapes in Maria. They’re here for you to browse and play with.

;; Most (but not all!) of the Clojure programming techniques used here are explained in [Learn Clojure with Shapes](https://dev.maria.cloud/intro) and [Welcome to Cells](https://dev.maria.cloud/cells). To figure out the parts that aren’t explained, we recommend experimenting with the code. You can do that directly in the code blocks below, or in your own playground using the Duplicate button at the top of the screen. Cheers! 🦋

;; #### Colorization functions

;; Demonstrated with ranges
(map #(colorize % (square 50))
     (map #(hsl % 100 50) (range 0 250 25)))

(map #(colorize % (square 50))
     (map #(hsl 120 % 50) (range 0 250 25)))

(map #(colorize % (square 50))
     (map #(hsl 120 50 %) (range 0 100 10)))

(map #(colorize % (circle 25))
     (map #(rgb % 0 0) (range 0 250 25)))

(map #(colorize % (circle 25))
     (map #(rgb 0 % 0) (range 0 250 25)))

(map #(colorize % (circle 25))
     (map #(rgb 0 0 %) (range 0 250 25)))

;; variable opacity
(map
  #(opacity % (colorize "blue" (triangle 40)))
  (range 0 1 0.1))

;; color gradient
(apply above
  (map #(colorize (hsl (rescale % 0 1000 120 220) 90 90)
                (rectangle 500 5))
     (range 0 1000 5)))

;; #### Rolling triangle rainbow
(apply layer
  (concat [(colorize "white" (rectangle 750 75))]
          (map #(->> (triangle 25)
                     (position (* 2 %) 20)
                     (rotate %)
   	                 (colorize (hsl % 90 75)))
               (range 0 360 15))))

;; #### Cartwheel rectangles
(layer
  (fill "white" (rectangle 750 70))
  (apply beside
       (map #(->> (rectangle 30 50)
                  (rotate %)
                  (opacity 0.5)
                  (fill (hsl % 90 45)))
            (range 0 375 15))))

;; #### Confetti
(defn confetti []
  (let [palette (cycle ["aqua" "springgreen" "magenta"])]
    (->> (repeat 20 (triangle 20))
     (map #(position (rand-int 500) (rand-int 500) %))
     (map colorize palette)
     (apply layer))))

;; try it–more than once!
(confetti)

;; now let’s have the computer do it for us every 1/10 of a second!
(cell (interval 100 confetti))

;; #### User interfaces and Adjustible shapes

(defcell shape-size 16)

(cell (html [:div
             [:h2 "Adjustable Triangle"]
             [:input {:type "range"
                      :on-input #(reset! shape-size (-> % (.-currentTarget) (.-value) int))}]
             [:div (colorize "aqua" (triangle @shape-size))]]))

;; Note that both the `shape-size` cell's reported value and the circle itself change when you move the slider. This happens because the function attached to on-input is `reset!`ing the `shapesize`, which then communicates that change to every cell that depends on it.

;; We can use this same mechanism to store reactive state for an application, for example by placing a map with multiple entries in a cell:

(defcell some-state
  {:size 20
   :colors ["magenta" "cyan" "yellow"]})

;; ... then assigning some behavior that updates what's in the state map. For example, when you click on this circle it will change its size and color:

(cell (->> (colorize (first (@some-state :colors))
                     (circle (@some-state :size)))
           (listen :click #(swap! some-state assoc :size (+ 20 (rand-int 10))
                                  :colors (shuffle (@some-state :colors))))))

;; #### Halloween pumpkin
(layer
 (position 40 60 (colorize "orange" (circle 40)))
 (position 10 30 (colorize "black"  (triangle 24)))
 (position 45 30 (colorize "black"  (triangle 24)))
 (position 20 75 (colorize "black"  (rectangle 40 10)))
 (position 25 74 (colorize "orange" (rectangle 10 5)))
 (position 45 74 (colorize "orange" (rectangle 10 5)))
 (position 33 82 (colorize "orange" (rectangle 10 5)))
 (position 35 2  (colorize "black"  (rectangle 10 20))))

;; #### Sample from a palette every quarter-second
(let [palette ["red" "orange" "yellow" "green" "blue" "indigo" "violet"]]
  (cell (interval 250 #(colorize (rand-nth palette) (square 50)))))

;; #### Labeled color swatches:
(take 10 color-names)

;; And see how color swatches are made by digging into the source:
(source color-names)

;; `color-names`-style swatches for every possible shade of blue (and
;; note *how* we get all those shades):
(map (fn [color-name]
       [color-name (colorize color-name (square 25))])
     (filter #(clojure.string/includes? % "blue")
             (map first color-names)))

;; #### RGB color picker
(defcell app-state {:red 0 :green 0 :blue 0})

(defn update-state [color]
  #(swap! app-state assoc color
          (int (* 2.55 (-> % (.-currentTarget) (.-value) js/parseInt)))))

(cell
  (html [:div
	  [:p "red"]
          [:input {:type "range" :default-value "0" :on-input (update-state :red)}]
   	  [:p "green"]
          [:input {:type "range" :default-value "0" :on-input (update-state :green)}]
   	  [:p "blue"]
          [:input {:type "range" :default-value "0" :on-input (update-state :blue)}]
          [:div (colorize (rgb (:red @app-state) (:green @app-state) (:blue @app-state))
                          (square 200))]]))

;; #### Data From Space 🚀

;; You should really check out [Data Flow](/data-flow) if you’re at all interested in data. It covers how to use Maria to grab data from across the Web and play with it.

;; ### Fernseheturm
(let [base (layer (position 35 90 (colorize "grey" (circle 25)))
                  (position 34 0 (colorize "grey" (rectangle 4 300)))
                  (position 29 108 (colorize "grey" (rectangle 12 222)))
                  (position 29 50 (colorize "grey" (rectangle 12 16)))
                  (position 15 315 (colorize "grey" (triangle 40)))
                  (position 26 125 (colorize "grey" (rectangle 18 16)))
                  (position 24 182 (colorize "grey" (rectangle 5 158)))
                  (position 41 182 (colorize "grey" (rectangle 5 158)))
                  (position 41 170 (colorize "grey" (rectangle 2 158)))
                  (position 27 170 (colorize "grey" (rectangle 2 158))))

      small-brown-rectangles (layer
                              ;;center of sphere small dark rectangle
                              (position 35 90 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                              ;; first set out and lower
                              (position 30 90 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                              (position 40 90 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                              ;; second set out and lower
                              (position 27 91 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                              (position 43 91 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                              ;; third set out but NOT lower
                              (position 24 91 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                              (position 46 91 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                              ;; fourth set out and lower
                              (position 21 92 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                              (position 49 92 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                              ;; fifth set out NOT lower
                              (position 17 92 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                              (position 52 92 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                              ;;sixth set out and lower
                              (position 14 93 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                              (position 55 93 (opacity 0.5 (colorize "brown" (rectangle 4 6)))))

      reflections-on-sphere-base (layer
                                  ;; white circle
                                  (position 40 90 (opacity 0.75 (colorize "white" (circle 20))))
                                  ;; lower blue circle
                                  (position 28 90 (opacity 0.75 (colorize "lightblue" (circle 15))))
                                  ;; higher blue circle
                                  (position 24 90 (opacity 0.75 (colorize "lightblue" (circle 15))))
                                  ;; orange triangle
                                  (position 35 97 (opacity 0.5 (colorize "orange" (circle 15))))
                                  ;; yellow triangle
                                  (position 28 90 (opacity 0.5 (colorize "yellow" (triangle 20))))
                                  ;;center of sphere small dark rectangle
                                  (position 35 90 (opacity 0.5 (colorize "brown" (rectangle 4 6)))))

      lower-brown-rectangles-on-sphere (layer
                                        ;;center of sphere small dark rectangle
                                        (position 35 98 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                                        ;; first set out and lower
                                        (position 30 98 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                                        (position 40 98 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                                        ;; second set out and lower
                                        (position 27 99 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                                        (position 43 99 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                                        ;; third set out but NOT lower
                                        (position 24 99 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                                        (position 46 99 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                                        ;; fourth set out and lower
                                        (position 21 100 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                                        (position 49 100 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                                        ;; fifth set out NOT lower
                                        (position 17 100 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                                        (position 52 100 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                                        (position 14 93 (opacity 0.5 (colorize "brown" (rectangle 4 6))))
                                        (position 55 93 (opacity 0.5 (colorize "brown" (rectangle 4 6)))))

      reflections-on-sphere-base (layer
                                  ;; white circle
                                  (position 40 90 (opacity 0.75 (colorize "white" (circle 20))))
                                  ;; lower blue circle
                                  (position 28 90 (opacity 0.75 (colorize "lightblue" (circle 15))))
                                  ;; higher blue circle
                                  (position 28 83 (opacity 0.75 (colorize "lightblue" (circle 15))))
                                  ;; orange triangle
                                  (position 35 97 (opacity 0.5 (colorize "orange" (circle 15))))
                                  ;; yellow triangle
                                  (position 28 90 (opacity 0.5 (colorize "yellow" (triangle 20))))
                                  ;;center of sphere small dark rectangle
                                  (position 35 90 (opacity 0.5 (colorize "brown" (rectangle 4 6)))))

      antenna-shaft-highlights (layer
                                ;; skinny orange horizontal highlight
                                (position 26 128 (opacity 0.75 (colorize "orange" (rectangle 18 3))))
                                ;; orange top  transparent vertical layer
                                (position 28 140 (opacity 0.75 (colorize "orange" (rectangle 13 32))))
                                ;; white transparent vertical side highlights
                                (position 25 180 (opacity 0.50 (colorize "white" (rectangle 12 155))))
                                (position 33 180 (opacity 0.50 (colorize "white" (rectangle 12 155))))
                                ;; orange lower transparent vertical layer
                                (position 28 180 (opacity 0.35 (colorize "orange" (rectangle 13 155))))
                                ;; white transparent vertical highlight mid
                                (position 28 200 (opacity 0.35 (colorize "white" (rectangle 13 150))))
                                ;; white transparent vertical highlight lower
                                (position 28 200 (opacity 0.35 (colorize "white" (rectangle 13 150))))
                                ;;antenna white highlight
                                (position 33 0 (opacity .65 (colorize "white" (rectangle 1 50))))
                                ;;antenna orange highlight
                                (position 35 0 (opacity .75 (colorize "orange" (rectangle 1 50))))
                                ;;just below antenna cylinder - center blue rectangle
                                (position 33 55 (opacity 0.25 (colorize "blue" (rectangle 4 6))))
                                ;; just below antenna cylinder - lower rectangles
                                (position 31 57 (opacity 0.35 (colorize "blue" (rectangle 2 6))))
                                (position 37 57 (opacity 0.35 (colorize "blue" (rectangle 2 6))))
                                ;; just below antenna cylinder - even lower rectangles
                                (position 29 58 (opacity 0.35 (colorize "blue" (rectangle 2 6))))
                                (position 39 58 (opacity 0.35 (colorize "blue" (rectangle 2 6))))
                                ;; extra dark blue on brown rectangles on main sphere
                                (position 30 90 (opacity 0.5 (colorize "blue" (rectangle 4 6))))
                                (position 40 90 (opacity 0.5 (colorize "blue" (rectangle 4 6)))))]
  (layer base
         reflections-on-sphere-base
         small-brown-rectangles
         lower-brown-rectangles-on-sphere
         antenna-shaft-highlights))
