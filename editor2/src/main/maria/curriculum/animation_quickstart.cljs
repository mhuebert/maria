(ns maria.curriculum.animation-quickstart
  {:description "Get a running start at making basic animations using the Shapes library."}
  (:require [shapes.core :refer :all]
            [cells.api :refer :all]))

;; # Animations Quick-Start

;; This is a quick-start guide for drawing animations. The goal of this document is to equip you with templates to give you a running start at making basic animations. From there you can do many fun and complex things.

;; For a full explanation of how the techniques in this guide work, see the [Cells](https://www.maria.editor.cloud/cells) lesson.


;; ### `cell` and `interval`

;; We use `interval` inside a `cell` to make animations that repeat. The interval, `250`, is in millisseconds, meaning this square gets colorized with a random item from `palette` every quarter-second:
(let [palette ["red" "orange" "yellow" "green" "blue" "indigo" "violet"]]
  (cell (interval 250 #(colorize (rand-nth palette) (square 50)))))

;; That's the most basic template.


;; ### `defcell`

;; By naming cells with `defcell`, we can use them in multiple places:

(defcell clock (interval 10 inc))

;; We can then get the _current_ value of `clock` with the `@` symbol. (Why? Read [Cells](https://www.maria.editor.cloud/cells) for how it works.)

(cell (rotate @clock (square 100)))

;; Because `clock` has a name, we can use it in a totally separate sketch. Here we add a color variable with the `hsl` (Hue/Saturation/Lightness) and `mod` ("modulo" or remainder) functions:

(cell (colorize (hsl (mod @clock 360) 80 80)
                (rotate @clock (square 100))))


;; We can also use multiple named cells in a single sketch.

;; First make a counter:

(defcell counter (interval 250 inc))

;; Second, define a random-color-name cell:

(defcell a-color (interval 250 #(rand-nth (map first color-names))))

;; Finally, put the counter and random-color-name cells together using sine and cosine:

(cell (->> (circle 25)
           (position (+ 60 (* 20 (Math/sin @counter)))
                     (+ 60 (* 20 (Math/cos @counter))))
           (colorize @a-color)))

;; Those are the basic tricks. Go forth and hack on some animated drawings, fellow programmer. For ideas, see the [Gallery](https://maria.editor.cloud/gallery?eval=true).
