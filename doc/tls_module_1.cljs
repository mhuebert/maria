(ns tls-module-1
  (:require
   [maria.messages :refer [what-is]]))

;; This is a comment. It doesn't evaluate to anything.

;; This is a "string" of characters. What does it evaluate to?
"duck"

;; What does this function draw when you evaluate it?
(circle 20)

;; Don't get bummed out if the computer barfs. It happens to everyone. What happens if you give the function the wrong thing?
(circle 20 20)

;; Functions nest. What will it evaluate to?
(colorize "blue" (rectangle 200 50))

;; Take a shot at drawing a simple shape in your favorite color, like a red square.
...


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; This is a vector. Vectors are a good way to hold a sequence of values.
[2 4 8 16 32 64 128]

;; You can do a function to every value in a vector, using the `map` function:
(map circle [2 4 8 16 32 64 128])

;; XXX more


;; You've come this far, so I trust you enough to hand you a sharp knife. Pay attention so you don't cut yourself.
;; The `repeat` function will call a function an _infinite_ number of times unless told to stop. We don't want your browser to freeze up while it draws millions of squares, so we'll `take` just a few:
(take 5 (repeat (rectangle 20 20)))

;; `repeat` can be infinite because it's lazy. It only does as much as you ask it. `take` asks the infinite sequence for as many as are needed, so `repeat` knows that's when to stop.
(take 250 (repeat (rectangle 20 20)))

;; If you have a vector of values, how do you call a function on every value?
(map circle [5 10 15 25 5 10 15 25 5 10 15 25])

(map rectangle
     [100 250]
     [100 100])

;; `
(map colorize
     ["red" "orange" "yellow" "green" "blue" "purple"]
     (repeat (rectangle 20 20)))
