;; # Intro

;; Put your cursor at the end of the following lines and press `Command-enter`.

"Good morning, ClojureBridgers!"

(max 8 17 2)

(fill "red" (circle 50))


;; # Simple values

"I am a String."

"I'm another String. Strings are letters between double-quotes."


;; # Arithmetic

(+ 5 1)

(+ 5 1 7.5)

(* 3 3)

(+ 1 (* 3 3))


;; # Defining names

;; If I just need the names for a little while, I use `let` like this:
(let [mangoes 3
      oranges 5]
  (+ mangoes oranges))

;; If I need the name all over my entire program, I have to use
;; `def`. (Try not to `def` too much, because a lot of names gets hard
;; to manage.)

(def fruit
  (let [mangoes 3
        oranges 5]
    (+ mangoes oranges)))

(/ fruit 2)

;; We've calculated the average amount of each fruit.


;; # EXERCISE 1: Basic arithmetic

;; 1. How many minutes have elapsed since you arrived at the workshop today?


;; 2. Convert this value from minutes to seconds.
