

;; # Say Hello to Cells
;;
;; Welcome! On this page, we are going to introduce something called a ‘cell’, which will help you make code come alive.
;;
;; What is a cell, you may ask? Well, at the most basic level, a `cell` is a thing that:
;;
;; * has a value,
;; * can change over time,
;; * wraps a bit of code, which tells the cell what to do every time it runs.
;;
;; Here is a 'cell' that counts upward to infinity, one second at a time:

(cell (interval 1000 inc))


;; \(`interval` is a special function that resets the value of a cell repeatedly, on a timer.)
;;
;; Cells can have names, which we define using `defcell`:

(defcell counter (interval 1000 inc))


;; By giving a cell a name, we can re-use it in other cells. A special thing happens when we do that:

(defcell bigger-counter (* @counter 10))


;; Do you see how `bigger-counter` is changing every second, even though it doesn't use `interval`? That's because `bigger-counter` automatically refreshes whenever `@counter` changes, multiplying that value by 10 to create its own value.

;; This kind of cell-to-cell awareness makes cells special. They form
;; a big web when you connect them. Each cell in the web stays in sync
;; with whatever other cells it is connected to.
;;
;; You may be wondering what that **@** (the "at sign") is doing. We use the @ symbol to ‘dereference’ a cell, so that we can read its value.

@counter


;; Note how `@counter` doesn’t change: it is frozen in time. When you
;; @dereference a cell, you don’t have the whole ‘cell’ anymore - you
;; get a snapshot of its value, at one point in time.
;;
;; If we wrap `@counter` in another cell, we’ll see it change again:

(cell @counter)

;; Every cell in your program dutifully keeps track of which other
;; cells it depends on, and updates when they change. Only *cells*
;; know how to react to other cells automatically: ordinary Clojure
;; code doesn’t change like that.

;; ## Interconnected Cells 🌎️🌍️🌏️
;; Let's make a more tangled web of cells, to see this
;; interconnectedness in action.

;; We'll start with a cell that is a random number generator:
(defcell random-number []
  (interval 200 #(rand-int 20)))

;; Those numbers go by so fast. Change the cell to slow it down. (Or,
;; if you're a jet-pilot kind of programmer, speed it up! ⚡️ 😀)

;; Next we'll create a cell that keeps track of the last 10 random
;; numbers generated, using the `random-number` cell:
(defcell last-ten [self]
  (take 10 (cons @random-number @self)))

;; The `last-ten` cell works by building up a list one at a time with
;; `cons`. (If you're not familiar with `cons`, take a minute using
;; `doc` (press `Command-i`) to get to know it.) The value that `cons`
;; adds to the list comes from looking at the *current* value of the
;; `random-number` cell, which we get by dereferencing it.

;; (Bonus 'how it works' puzzle: what would happen if we didn't `take
;; 10` from the list? Think about it, then experiment.)

;; Often it's easier to think about numbers if we can make them more
;; real. Let's do that by visualizing our last ten random numbers into
;; shapes:
(defcell squares []
  (map square @last-ten))

;; Notice how you can see each number-as-a-square move across the list
;; as it grows old. Notice also how `squares` uses one cell, which
;; uses another cell, and that all those dependencies are handled
;; *automatically* by your friend the computer.

;; Play around with those squares to see how else to visualize our
;; random numbers. Might different shapes look cool? How could you add
;; color? What would you have to do to assign each number a color that
;; sticks with its shape? Experiment.

;; ## What else can we do with cells?
;;

;; ## Interactivity TODO finish
;;
;; Just like `interval`, there is another special function called...

;; TODO explain
(def toggle (cell true))

;; TODO explain
(with-view toggle
  (->> (if @self (circle 40) (square 80))
       (listen :click #(swap! self not))))

;; TODO explain
(with-view toggle
  (html [:div
         [:div.f2.pa3.white.pointer.mb2
          {:on-click #(swap! self not)
           :class (if @self "bg-pink" "bg-black")}
          (if @self "YAAA" "NOOO")]
         "(^^click me)"]))

;; TODO explain
(with-view toggle
  (html [:.pa5.br-100.dib
         {:class (if @self "bg-black" "bg-pink")}]))

;; FIXME
;; ## Fetch things from the internet
;;
;; Just like `interval`, there is another special function called `fetch` which only works inside cells. Given a URL, `fetch` can download data from the internet:


(defcell location (geo-location))

(defcell birds
  "An options map including :query params may be passed
  as the second arg to fetch."
  (fetch "https://ebird.org/ws1.1/data/obs/geo/recent"
         {:query {:lat (:latitude @location "52.4821146")
                  :lng (:longitude @location "13.4121388")
                  :maxResults 10
                  :fmt "json"}}))

(cell (map :comName @birds))

(defn find-image [term]
  (let [result (cell term (fetch "https://commons.wikimedia.org/w/api.php"
                                 {:query {:action "query"
                                          :origin "*"
                                          :generator "images"
                                          :prop "imageinfo"
                                          :iiprop "url"
                                          :gimlimit 5
                                          :format "json"
                                          :redirects 1
                                          :titles term}}))]
    (some->> @result
             :query
             :pages
             vals
             (keep (comp :url first :imageinfo))
             first)))

(defcell bird-pics
  (doall (for [bird @birds]
           (some->> (:sciName bird)
                    (find-image)
                    (image 100)))))

(cell (image (find-image "berlin")))


;; TODO Loop example


;; TODO wrap up with conclusion
