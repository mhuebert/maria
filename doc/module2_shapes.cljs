;; XXX follow-up to Quick Intro
;; XXX goals: introduce stuff like first, rest, last, rand-nth, filter, remove

;; XXX It might be nice to kick off with something from the examples namespace, to show that it exists and to see something more advanced.

;; e.g. color gradient?
(apply above
       (map (fn [x] (colorize (hsl (rescale x 0 1000 120 220) 90 90)
                             (rectangle 500 5)))
            (range 0 1000 8)))


;; or
(apply layer
       (concat [(colorize "white" (rectangle 750 75))]
               (map (fn [d] (->> (triangle 25)
                                (position (* 2 d) 20)
                                (rotate d)
                                (colorize (hsl d 90 75))))
                    (range 0 360 15))))

;; or

(def rainbow (map (fn [color] (colorize color (square 50)))
                  (map (fn [x] (hsl x 100 50)) (range 0 250 25))))

rainbow

(apply beside rainbow)

(first rainbow)

;; How might you get the second square?

;; XXX YOUR CODE HERE


;; Clojure has a lot of functions like these, but doesn't give us `third` or `fourth` because there would be too many to name. Instead, we use `nth`. Be warned: this is a tricky one.

(nth rainbow 2)

;; That's only the "second" square if you count like a computer. Try a few other numbers with `nth`:

;; XXX YOUR CODE HERE


;; Let's explore some more sequence functions. What do you think will be the color of the square that `last` will return? Try it.

;; XXX YOUR CODE HERE


;; TODO leave `rest`, `butlast`, `ffirst` for another time





;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;; Up until now we've only looked at expressions that have functions
;; you're familiar with, or that you're learning. Let's look now at a
;; fairly advanced expression that has some stuff you haven't seen
;; yet. Don't worry, we'll investigate all the new parts, but first
;; let's see how it runs when all the pieces are there.
(apply above
       (map (fn [x] (colorize (hsl (rescale x 0 1000 120 220) 90 90)
                             (rectangle 500 5)))
            (range 0 1000 8)))

;; This expression creates a color gradient, slowly transitioning from
;; green to blue. Sweet! 🌈 🦄 But...HOW? We can find out how, by
;; exploring the code.

;; Let's first investigate that last bit. What's a `range`? Evaluate
;; that expression by pressing Command-Enter XXX with your cursor
;; after the close-parenthesis following the digit "8". You should get
;; a list of numbers, starting at 8 and counting up by 8s. And there
;; should be an ellipsis at the right side of the list, which when you
;; click will show the list continuing. How strange!

;; What's going on is that you generated a "range" of numbers starting
;; at 0 and going all the way to 1000, counting by 8s. The ellipsis is
;; there because we don't want to flood you with numbers when you
;; evaluate something long.
