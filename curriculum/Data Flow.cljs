;; # Data Flow

;; Howdy there. We're going to use Clojure to grab some data from across the Web so we can play with it a bit. We'll rely heavily on Maria-powered [cells](/cells), so if you're not familiar with those yet you'll have to either go read that link or figure it out on your own as we go.

;; Ready? Hold on to your hat because we're going to fly across The Internet.

;; The first thing we'll do is use [open data](https://en.wikipedia.org/wiki/Open_data) to find out which birds are common where you are.

;; Evaluating this next cell will make your browser confirm that you're OK with Maria tracking your location through your browser. We won't use that data for anything except to customize the results in this exercise, which you can see in the code below. If you decline, the exercise will still work.

(defcell location (geo-location))

;; With your location, or using the defaults 52.4821146 by 13.4121388, we'll query a data source with an [HTTP](https://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol) request. Through some prior digging, we know that the URL in the `fetch` below is a resource for [eBird](http://ebird.org/ebird/explore) data. We'll tell it our latitude and longitude, what format we want the results in, and that 10 results is just plenty, thanks. We'll see a "waiting" icon when we first evaluate it, and then a few seconds later the data will come back. (Give it a few seconds. Our request might have to go through an undersea cable.)

;; TODO brief rehash (HAHAHAHA) of hash-maps, keywords, keywords as fns with defaults
(defcell birds
  (fetch "https://ebird.org/ws1.1/data/obs/geo/recent"
         {:query {:lat (:latitude @location "52.4821146")
                  :lng (:longitude @location "13.4121388")
                  :maxResults 10
                  :fmt "json"}}))

;; Once that comes back, let's poke at it a bit. That's the best way to get to know some new data.

;; TODO `first`, `rest`, `keys`?

(cell (map :comName @birds))

;; ## Go out and harvest more data for our data
;; TODO EXPLAIN
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

;; ## Options

;; Note that `fetch` accepts an options map:

;; * `:format` can be `:json->clj` (the default), `:json`, or `:text`
;; * `:query` appends a map of query parameters to the url as a querystring.

;; ## Loading status and errors

;; A bad or incomplete request returns nil:

(defcell bad-request (fetch "https://ebird.org/xyz"))

;; …experimenting with an IAsync protocol for communicating status information:

(cell (cell/loading? bad-request))

(cell (cell/error? bad-request))

(cell (cell/status bad-request))

(cell (cell/message bad-request))

;; could also do something like `cell/error?` and `cell/loading?`.

;; ## Dependencies

;; See what cells depend on:

(cell/dependents birds)

(cell/dependents location)

;; This can be useful for debugging.

;; ## Anonymous cells

;; Cells created using `defcell` are named after their var.

;; Anonymous cells may also be interesting:

(defcell inline-count
  (let [counter (cell (interval 1000 inc))]
    (str "I have been counted " @counter " times")))

;; The anonymous `cell` macro assigns itself a unique ID behind the scenes:

(cell/dependencies inline-count)

[(cell (interval 1000 inc))
 (cell (interval 500 #(rand-nth [\q \r \s \t \u \v])))]
