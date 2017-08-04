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

1/2

-7/3

;; Arithmetic

(+ 1 1)  ;=> 1 + 1 = 2

(- 12 4) ;=> 12 - 4 = 8

(* 13 2) ;=> 13 * 2 = 26

(/ 27 9) ;=> 27 / 9 = 3

;; Prefix notation

(+ (- (+ (+ 1 (/ (* 2 3) 4)) 5) (/ (* 6 7) 8)) 9) ; compare to: 1 + 2 * 3 / 4 + 5 - 6 * 7 / 8 + 9

(+ 1 (/ 2 3)) ; compare to: 1 + 2 / 3

(+ 1 2 3 4 5 6 7 8 9) ; compare to: 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9

;; Arithmetic with all number types

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
