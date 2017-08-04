;;;; Intro

"Good morning, ClojureBridgers!"

(max 8 17 2)

(fill "red" (circle 50))

;;;; Simple values

;; In order to do anything in a programming language, you need to have
;; values to do stuff with. In Clojure, simple values are numbers,
;; strings, booleans, nil and keywords.

;; Integers

;; First up are integers. Integers include zero, the positive whole
;; numbers, and the negative whole numbers, and you write them just
;; like we write them normally.

0

12

-42

;; Decimals

;; Then we have decimal numbers, which are also called floats. They
;; include any numbers that have a decimal point in them.

0.0000072725

10.5

-99.9

;; Ratios

;; Finally, we have fractions, which are also called ratios. Computers
;; cannot perfectly represent all floats, but ratios are always exact.
;; We write them with a slash, like so:

1/2

;; Note that, just like with pen-and-paper math, the [denominator](http://en.wikipedia.org/wiki/Fraction_%28mathematics%29) of your ratio cannot be equal to `0`.

-7/3


;; Arithmetic

;; You can add, subtract, multiply, and divide numbers. In Clojure,
;; arithmetic looks a little different than it does when you write it
;; out with pen and paper. Look at these examples:

(+ 1 1)  ;=> 1 + 1 = 2

(- 12 4) ;=> 12 - 4 = 8

(* 13 2) ;=> 13 * 2 = 26

(/ 27 9) ;=> 27 / 9 = 3


;; Prefix notation

;; In Clojure, `+`, `-`, `*` and `/` appear before two numbers. This
;; is called _prefix notation_. What you're used to seeing is called
;; _infix notation_, as the arithmetic operator is in-between the two
;; operands.

;; Languages such as **JavaScript** use **infix** notation, while
;; **Clojure** only uses **prefix** notation.  Prefix notation is
;; useful for many reasons. Look at this example of an infix
;; expression and the prefix equivalent:

(+ (- (+ (+ 1 (/ (* 2 3) 4)) 5) (/ (* 6 7) 8)) 9) ; compare to: 1 + 2 * 3 / 4 + 5 - 6 * 7 / 8 + 9

;; > Imagine both are unclear, but notice that in the prefix version,
;; > you do not have to ever think about the precedence of operators.
;; > Because each expression has the operator before all the operands and
;; > the entire expression is wrapped in parentheses, all precedence is
;; > explicit.

(+ 1 (/ 2 3)) ; compare to: 1 + 2 / 3

;; > Another reason prefix notation can be nice is that it can make long
;; > expressions less repetitive.
;; > With prefix notation, if we plan to use the same operator on many
;; > operands, we do not have to repeat the operator between them.

(+ 1 2 3 4 5 6 7 8 9) ; compare to: 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9


;; Arithmetic with all number types

;; > So far, we looked at arithmetic operations by integers only.
;; > However, we can use floats or ratios for those operations as well.
;; > See these examples:

(+ 4/3 7/8)   ;=> 53/24

(- 9 4.2 1/2) ;=> 4.3

(/ 27/2 1.5)  ;=> 9.0

;; Strings

;; What is a string? A string is just a piece of text. To make a
;; string, you enclose it in quotation marks.  Look at the last
;; example. A backslash is how we put a quotation mark inside a
;; string. Do not try using single quotes to make a string.

"Hello, World!"

"This is a longer string that I wrote for purposes of an example."

"Aubrey said, \"I think we should go to the Orange Julius.\""


;; Booleans

;; A boolean is a true or false value, and you type them just like
;; that, `true` and `false`. Often in programming, we need to ask a
;; true or false question, like "Is this class in the current
;; semester?" or "Is this person's birthday today?" When we ask those
;; questions, we get a boolean back.

true
false

;;  There is another value `nil`, which behaves like a boolean in
;;  terms of __truthiness__.  But, `nil` means no value at all and not
;;  a boolean

nil

;;  Keywords are the strangest of the basic value types. Some computer
;;  languages have similar one. However, keywords don’t have a real
;;  world analog like numbers, strings, or booleans.  You can think of
;;  them as a special type of string, one that’s used for labels. They
;;  are often used as keys of key-value pair for maps (data structure;
;;  will learn later).

:forename
:surname
:date-of-birth


;; Assignment

;;  If we had to type the same values over and over, it would be very
;;  hard to write a program. What we need are names for values, so we
;;  can refer to them in a way we can remember. This is called
;;  assignment.

;;  We can assign a name to value using `def`.  When a name is
;;  assigned a value, that name is called a *symbol*.

(def mangoes 3)

(def oranges 5)

(+ mangoes oranges)

;;  You can assign more than simple values to symbols. Try the
;;  following.  Look at the last line, and see how we can use symbols
;;  by themselves to refer to a value.

(def fruit (+ mangoes oranges))

(def average-fruit-amount (/ fruit 2))

average-fruit-amount


;;;; EXERCISE 1: Basic arithmetic

;; * How many minutes have elapsed since you arrived at the workshop today?
;; * Convert this value from minutes to seconds.


;;;; EXERCISE 2 [BONUS]: Minutes and seconds

;; Convert 1000 seconds to minutes and seconds.
;; The minutes and the seconds will be separate numbers.
;; `(quot x y)` will give you the whole number part of x divided by y.
;; `(rem x y)` will give you the remainder of x divided by y.
