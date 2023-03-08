(ns maria.examples)


(def js-interop "
;; Using [applied-science/js-interop](https://www.github.com/applied-science/js-interop):

(def obj #js{:x \"world\"})
(j/get obj :x)
(j/defn hello [^js {:keys [x]}] x)
(hello obj)
")

(def yawn
  "
  ;; Using [yawn](https://github.com/mhuebert/yawn), a light React wrapper:

  (require '[yawn.view :as v])
   (v/x [:div {:style {:color \"red\"}} \"a\"])
   (v/defview hello-world [name] [:div \"Hello, \" name])
   (hello-world \"creature of the void!\")")

(def emoji "
;; Emoji in var names

(defn 🌈
  \"Applies colors of the rainbow to a sequence of shapes\"
  [shapes]
  (map (fn [shape color] (colorize color shape))
    shapes
    (cycle [\"red\"
            \"orange\"
            \"yellow\"
            \"green\"
            \"blue\"
            \"indigo\"
            \"violet\"])))

(🌈 (repeat (circle 20)))
")

(def error "
;; Showing a \"raw\" javascript type

js/Promise

;; Showing errors

(10)

(.-x nil)

;; errors in cells
(require '[cells.api :refer :all])
[(cell (x)) (cell (x))]
"
  )

(def sci-errors "
;; # sci stacktraces

;; ✅ When an error is thrown inside a `defn`, we see its name and position
(defn x [] (1 2 3))
(x)

;; 🪲For anonymous functions that have names (🤔), the name is not shown
((fn my-function [] (1 2 3)))

;; ✅ Other errors (at least the following) do show stacktraces
(1 2 3)
(keyword)
(name 1)
(throw (js/Error. \"Hello\"))\n(throw (ex-info \"Hello\" {}))

;; 🪲
((fn call-numbers [] (1 2 3)))
(mapv (fn call-fn [f] (f)) [1])

"
  )

(def promise
  "
;; Showing the status of a promise

(require '[promesa.core :as p])

(p/delay 3000 (square 20))

;; When evaluating a namespace sequentially, `await` will pause
;; until the awaited promise returns. You can also await a var
;; which contains to a promise.

(await (p/delay 1000 (square 20)))

(await (def num-circles (p/delay 300 5)))

(->> (range 1 (inc num-circles))
     (map #(circle (* % 10))))
")

(def collections "
;; ## Collections

{1 2 3 4 5 6 7 8 9 10}

(list (range 1000) (range 1000) (range 1000))

[(circle 30)(triangle 330) (rectangle 500 10)]

{:hello \"world 👋\"
 :tacos (map #(repeat % '🌮) (range 1 30))
 :zeta \"The\npurpose\nof\nvisualization\nis\ninsight,\nnot\npictures.\"}")

(def cell "

;; Using the cells library.

(require '[cells.api :refer :all])

(defcell counter (interval 400 inc))

(cell
  (->> (repeat (circle 20))
       (take (rem @counter 6))))

(cell (fetch \"http://example.com/movies.json\"))

(cell (timeout 1000 \"hello\"))


;; Cells show their 'async status' (ie. loading, error state)

(for [i (range 10)]
  (cell (timeout (* i 300) (circle 10))))

")


(def repl ";; Special REPL utilities
(doc inc)
(doc do)
(doc when)
(dir user)")

(def namespaces "
;; Working with namespaces. When evaluating a code block individually,
;; Maria uses the nearest-evaluated namespace above it.

(ns my.app \"Main app namespace\")
(doc circle)
(doc my.app)
(ns user)
(doc circle)
(resolve-symbol 'circle)
")

(def string
  (str
   ";; Rendering long strings - click to expand. (Should improve the affordance here.)\n"
   (pr-str "e great and striking peculiarity of this play is that its action lies wholly in the ideal world. It differs, therefore, from every other work of Shakespeare in the character of its mediation. Our poet, in most of his dramas, portrays the real world, and exhibits man as acting from clear conscious motives, and not from supernatural influences. But here he completely reverses his procedure; from beginning to end the chief instrumentalities of the poem are external; its conflicts and solutions are brought about by powers seemingly beyond human might and intelligence.\" J. D. Snider. Read on...\n \nSonnet 73\nAn analysis of Shakespeare's inspired sonnet, hailed as one of the best in the Western canon. Included is a paraphrase of the poem in contemporary English.\n \nWhy is King Leir Important?\nMr. Lawrence Barrett as King Lear.The story of King Lear and his three daughters is an old tale, well known in England for centuries before Shakespeare wrote the definitive play on the subject. The first English account of Lear can be found in the History of the Kings of Britain, written by Geoffrey Monmouth in 1135. However, it is clear that Shakespeare relied chiefly on King Leir, an anonymous play published twelve years before the first recorded performance of Shakespeare's King Lear. Read on to find out more about Leir and see side-by-side versions of Leir and Shakespeare's masterpiece.\n \nWhat Did Shakespeare Look Like?\nThe Stratford Bust, located on the wall of the chancel of Holy Trinity Church at Stratford-upon-Avon, is the oldest and, along with the Droeshout Portrait, most credible of all the known images of Shakespeare. But there are many representations of the Bard that have been handed down throughout the centuries, each with its own fascinating story to tell.\n \nShakespeare in Print: The Perils of Publishing in Elizabethan England\nOld printing press. From The Triumphs of the Printing Press. Walter Gerrold. London, Partridge & Co.During Shakespeare's lifetime Elizabethan playwrights cared little about seeing their work in print. Only the rare drama was actually intended to be read as well as performed. ")
   "\"a
b
c
d
e
f
g
h
i
j
k
l
m
n
o\""
   ))

(def link
  ";; Links and images in markdown become clickable and editable by moving the cursor into them.
   ;; Hello, [world](https://example.com) of [apples](https://www.apple.com) ![title](https://via.placeholder.com/350x150).
  ")

(def examples (str
               emoji
               link
               repl
               namespaces
               cell
               collections
               error
               sci-errors
               promise
               string
               js-interop
               yawn))