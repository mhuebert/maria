;; Colorization functions, demonstrated with ranges
(map #(colorize % (square 50))
     (map #(hsl % 100 50) (range 0 250 25)))

(map #(colorize % (square 50))
     (map #(hsl 120 % 50) (range 0 250 25)))

(map #(colorize % (square 50))
     (map #(hsl 120 50 %) (range 0 100 10)))

(map #(colorize % (square 50))
     (map #(rgb % 0 0) (range 0 250 25)))

(map #(colorize % (square 50))
     (map #(rgb 0 % 0) (range 0 250 25)))

(map #(colorize % (square 50))
     (map #(rgb 0 0 %) (range 0 250 25)))

;; variable opacity
(map
  #(opacity % (colorize "blue" (square 10)))
  (range 0 1 0.1))

;; color gradient
(apply above
  (map #(colorize (hsl (rescale % 0 1000 120 220) 90 90)
                (rectangle 500 5))
     (range 0 1000 5)))

;; rolling triangle rainbow
(apply layer
  (concat [(colorize "white" (rectangle 750 75))]
          (map #(->> (triangle 25)
                     (position (* 2 %) 20)
                     (rotate %)
   	                 (colorize (hsl % 90 75)))
               (range 0 360 15))))

;; cartwheel rectangles
(layer
  (fill "white" (rectangle 750 70))
  (apply beside
       (map #(->> (rectangle 30 50)
                  (rotate %)
                  (opacity 0.5)
                  (fill (hsl % 90 45)))
            (range 0 375 15))))

;; confetti
(let [palette (cycle ["aqua" "springgreen" "magenta"])]
  (->> (repeat 20 (triangle 20))
       (map #(position (rand-int 500) (rand-int 500) %))
       (map colorize palette)
       (apply layer)))

;; Halloween pumpkin
(layer
 (position 40 60 (colorize "orange" (circle 40)))
 (position 10 30 (colorize "black"  (triangle 24)))
 (position 45 30 (colorize "black"  (triangle 24)))
 (position 20 75 (colorize "black"  (rectangle 40 10)))
 (position 25 74 (colorize "orange" (rectangle 10 5)))
 (position 45 74 (colorize "orange" (rectangle 10 5)))
 (position 33 82 (colorize "orange" (rectangle 10 5)))
 (position 35 2  (colorize "black"  (rectangle 10 20))))

;;;; Fernseheturm
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
