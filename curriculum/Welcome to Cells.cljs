

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

;; ## Your First Cell 🤠

;; Consider `interval`, a special function that resets the value of a
;; cell repeatedly, on a timer. Given `interval`, here is a 'cell' that
;; counts upward to infinity, one second at a time:

(cell (interval 1000 inc))

;; Cells can have names, which we define using `defcell`. Let's
;; re-make that timer cell, but this time we'll define a name for it.

(defcell counter (interval 1000 inc))

;; By giving a cell a name, we can re-use it in other cells. This
;; gives us tremendous power. Watch:

(defcell bigger-counter (* @counter 10))

;; Do you see how `bigger-counter` is changing every second, even
;; though it doesn't use `interval`? That's because `bigger-counter`
;; automatically refreshes whenever `@counter` changes, multiplying
;; that value by 10 to create its own value.

;; This kind of cell-to-cell awareness makes cells special. It means
;; they can be formed into a giant web, each cell ready to act
;; together with the others. Each cell in the web stays in sync with
;; whatever other cells it is connected to.

;; You may be wondering what that **@** (the "at sign") is doing. We
;; use the @ symbol to ‘dereference’ a cell, so that we can read its
;; value. Evaluate this, wait a few seconds, then evaluate it
;; again. (Make sure you evaluate the *whole* expression by using
;; `Shift-Command-Enter`.)

@counter

;; Note how `@counter` doesn’t change: it is frozen in time. When you
;; @dereference a cell, you don’t have the whole ‘cell’ anymore - you
;; get a snapshot of its value, at one point in time.

;; If we wrap `@counter` in another cell, we’ll see it change again:

(cell @counter)

;; Every cell in your program dutifully keeps track of which other
;; cells it depends on, and updates when they change. Only *cells*
;; know how to react to other cells automatically: ordinary Clojure
;; code doesn’t update like that.

;; ## Interconnected Cells 🌎️🌍️🌏️

;; Let's make a more tangled web of cells, to better see this
;; interconnectedness in action.

;; We'll start with a cell that is a random number generator:
(defcell random-number []
  (interval 200 #(rand-int 20)))

;; The `#` ("hash" or "pound sign") immediately before an
;; open-parenthesis might be new to you. Go ahead an evaluate that
;; subform, `#(rand-int 20)`, and you'll see it returns a
;; function. That's all the `#` does: it's a quick way to define an
;; anonymous function. This
;; [shorthand](https://clojure.org/guides/weird_characters#__code_code_anonymous_function)
;; is no different from `(fn [] ...)` except none of the arguments get
;; names.

;; Now we have a cell that updates itself every fifth of a
;; second. Let's tinker with it a bit. Those numbers go by so
;; fast--change the cell to slow it down. (Or, if you're a jet-pilot
;; kind of programmer, speed it up! ⚡️ 😀)

;; That `random-number` cell is the first part of our cell chain. Next
;; we'll create a cell that keeps track of the last 10 random numbers
;; generated, using the `random-number` cell:
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

;; ## Talking to Cells 📢 🗣️

;; Cells also **interact** with us. Just like we used cells to track
;; time using `interval`, we can use cells to detect user activity,
;; like mouse clicks. We do this by "listening" for specific browser
;; "events". To explore interactions with cells, let's first create a
;; cell that we'll store `true` or `false` in. Those are special
;; values in Clojure, used when we need a clear
;; [Boolean](https://en.wikipedia.org/wiki/Boolean_data_type) truth
;; value. Both `true` and `false` are [literal
;; values](https://clojure.org/reference/reader#_literals), and don't
;; need to be wrapped in double-quotes.

(defcell toggle true)

;; This cell doesn't do much: it's just a container. But because it's
;; a cell, we can use it for so much more than if we defined it as a
;; plain old `true` value on its own. As a cell it can be changed, and
;; notifies all the cells that depend on it when it has changed.

;; Let's see that in action FIXME
(with-view toggle
  (->> (if @self (circle 40) (square 80))
       (listen :click #(swap! self not))))

;; XXX aside
(with-view counter
  (if (odd? @self)
    (triangle 20)
    (square 20)))

;; XXX also aside
(with-view random-number
  (triangle (* 3 @self)))


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
;; ## Data From Space 🚀
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

;; XXX for more on fetching, see https://maria.cloud/gist/f958a24f0ece6d673bce574ec2d3cd71


;; TODO Loop example


;; TODO wrap up with conclusion