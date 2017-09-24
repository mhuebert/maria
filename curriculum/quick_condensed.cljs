;; # Hi!

;; `ctrl-enter`/`command-enter`

"puppy"

;; evaluation, expression, Clojure

(what-is "puppy") ;; XXX maybe `circle` should come first?

;; string, parentheses, functions, arguments

(circle 50)

(circle 20 10) ;; errors

(doc circle) ;; documentation

(what-is 50)
(what-is circle)
(what-is (circle 50)) ;; FIXME in dev, this just says "a map", not "a shape" (see #89)
(what-is what-is)


;; ## Colors and Shapes

;; <exercise>

(doc rectangle)

(rectangle 200 50)

(colorize "blue" (rectangle 250 100))

;; evaluating sub-expressions

(colorize "blue" (rectangle 250 (* 2 50)))

color-names ;; XXX consider rand-nth, count, first, second here

(doc layer)

(layer
 (colorize "aqua" (square 50))
 (colorize "magenta" (circle 25)))

(layer
 (colorize "springgreen" (circle 25))
 (position 50 25 (colorize "pink" (circle 25))))

(layer
 (colorize "aqua" (circle 40))
 (position 10 10 (colorize "magenta" (triangle 24)))
 (position 45 10 (colorize "magenta" (triangle 24)))
 (position 40 55 (colorize "white" (circle 10))))

;; TODO play break


;; ## ?????

(what-is [1 2 3 4])

;; vectors

["Ada Lovelace" "Gracie Hopper" "Margaret Hamilton"]
["Stone Age" "Bronze Age" "Iron Age"] ;; we want to convey *sequential* nature here

;; introduce map using repetition use case

[(circle 16) (circle 32) (circle 64) (circle 128)]

;; map

(map circle [16 32 64 128])

;; creating functions on the fly

(what-is (fn [radius] (circle radius)))

(layer
 (position 50 60 (text "(fn [radius] (circle radius))"))
 (colorize "grey" (position 60 70 (triangle 10)))
 (position 0 102 (text "function"))
 (position 95 25 (rotate 60 (colorize "grey" (triangle 10))))
 (position 90 20 (text "argument(s)"))
 (colorize "grey" (position 170 70 (triangle 10)))
 (position 170 102 (text "expression")))

(map (fn [radius] (circle radius)) [16 32 64 128])

(map (fn [radius] (colorize "purple" (circle radius))) [16 32 64 128])

(map (fn [radius] (colorize "purple" (circle radius)))
     [16 32 64 128])


;; ## Names

;; let

;; XXX lots has changed in this section
;; XXX lots has changed in this section
;; XXX lots has changed in this section
;; XXX lots has changed in this section
;; XXX lots has changed in this section
;; XXX lots has changed in this section
;; XXX lots has changed in this section
;; XXX lots has changed in this section
(let [palette ["blue" "turquoise" "midnightblue"]]
  palette) ;; user input

(let [palette ["red" "orange" "yellow" "green" "blue" "indigo" "violet"]]
  (map (fn [color] (colorize color (rectangle 20 20))) palette))

(let [palette ["red" "orange" "yellow" "green" "blue" "purple"]]
  (map colorize
       palette
       (repeat (circle 50))))

(let [palette ["grey" "black" "white" "darkgrey" "lightgrey" "slate"]]
  (colorize (rand-nth palette) (circle 25)))

;; build this fn to check if "slate" is really a color
(let [color-name? (fn [color] (contains? (set (map first color-names)) color))]
  (color-name? "slate"))
;; XXX some potential copy for this:
;; How best to approach this problem? Well, first let's state what
;; data we have, and what we'd like to do with it.
;; Data we have: `color-names`
;; What we'd like to do with it:
;; TODO continue in this vein with building from small explorations into def'ing a fn

;; def

(def color-name?
  (fn [color] (contains? (set (map first color-names)) color)))

(color-name? "darkgrey")

;; repeat earlier example using def
(def rainbow-colors ["red" "orange" "yellow" "green" "blue" "purple"])

(map colorize
     rainbow-colors
	 (repeat (rectangle 20 20)))

;; defn

(defn color-name? [color]
  (contains? (set (map first color-names)) color))

(color-name? "blue")

(color-name? "blau")
