

;; # Say Hello to Cells
;;
;; Welcome! On this page, we are going to introduce something called a ‘cell’, which will help you make code come alive.
;;
;; What is a cell, you may ask? Well, at the most basic level, a `cell` is a thing that:
;;
;; * Has a value,
;; * Can change over time,
;; * Wraps a bit of code, which tells the cell what to do every time it runs.
;;
;; Here is a “cell” that counts upward to infinity, one second at a time:

(cell (interval 1000 inc))


;; \(`interval` is a special function that resets the value of a cell repeatedly, on a timer.)
;;
;; Cells can have names (we use `defcell` for that):

(defcell counter (interval 1000 inc))


;; By giving a cell a name, we can re-use it in other cells. A special thing happens when we do that:

(defcell bigger-counter (* @counter 10))


;; Do you see how `bigger-counter` is changing every second? It automatically refreshes whenever `@counter` changes, and multiplies it by 10. Cells are like that: when you connect them, they form a big web. Each cell in the web stays in sync with whatever other cells it is connected to.
;;
;; You may be wondering what that **@** is doing. To read a cell, we have to ‘dereference’ it using the @ symbol:

@counter


;; Note how `@counter` doesn’t change: it is frozen in time. When you @dereference a cell, you don’t have the whole ‘cell’ anymore - you get a snapshot of its value, at one point in time.
;;
;; If we wrap `@counter` in another cell, we’ll see it change again:

(cell @counter)


;; Every cell in your program dutifully keeps track of which other cells it depends on, and updates when they change. Only *cells* know how to react to other cells: ordinary Clojure code doesn’t change like that.
;;
;; What else can we do with cells?
;;
;; ## Fetch things from the internet
;;
;; Just like `interval`, there is another special function called `fetch` which only works inside cells. Given a URL, `fetch` can download data from the internet:

(cell (fetch ""))


;; TODO
;;
;; * Fetch
;; * More fun examples
;; * Loop example
