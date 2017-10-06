;; # Data Flow

;; Howdy there. We're going to use Clojure to grab some data from across the Web so we can play with it a bit. We'll rely heavily on Maria-powered [cells](/cells), so if you're not familiar with those yet you'll have to either go read that link or figure it out on your own as we go. This introduction is also unfinished, so it may cover some topics rather quickly. Sorry about that, we're working on it. :)

;; Ready? Hold on to your hat because we're going to fly across The Internet.

;; The first thing we'll do is use [open data](https://en.wikipedia.org/wiki/Open_data) to find out which birds are common where you are.

;; Evaluating this next cell will make your browser confirm that you're OK with Maria tracking your location through your browser. We won't use that data for anything except to customize the results in this exercise, which you can see in the code below. If you decline, the exercise will still work.

(defcell location (geo-location))

;; With your location (or using a default location) we'll query a data source with an [HTTP](https://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol) request. Through some prior digging, we know that the URL in the `fetch` below is a resource for [eBird](http://ebird.org/ebird/explore) data. We'll tell it our latitude and longitude, what format we want the results in, and that 10 results is just plenty, thanks. We'll see a "waiting" icon when we first evaluate it, and then a few seconds later the data will come back. (Give it a few seconds. Our request might have to go through an undersea cable.)

;; This next code snippet uses hash-maps, [keywords](https://clojure.org/reference/data_structures#Keywords), and keywords as functions with defaults. That's a lot of new stuff! Normally we'd introduce these more slowly but we're a little pressed for time. Sorry about that, we're working on it. :)
(defcell birds
  (fetch "https://ebird.org/ws1.1/data/obs/geo/recent"
         {:query {:lat (:latitude @location "52.4821146")
                  :lng (:longitude @location "13.4121388")
                  :maxResults 10
                  :fmt "json"}}))

;; Once that comes back, let's poke at it a bit. That's the best way to get to know some new data. Let's look at just one of the results:

(first @birds)

;; OK, so we've got some birdwatching data. Maybe we can guess about what each attribute means.

;; I wonder what birds we've found? Let's grab the "common name" attribute from all the results:

(cell (map :comName @birds))

;; Play a bit more with the data if you like. Or, charge ahead to the next section to find some images for these birds.

;; ## Go out and harvest more data for our data

;; Warning: Here comes a bunch of new stuff again! Again, we'd like to introduce these ideas more completely and piece-by-piece, but we're still working on this.

;; Here's a function that runs off to the open data resource Wikidata and gets the first picture it finds for a given term.
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

;; Let's try it out. First, let's get a look at the kind of term it will work for:

(:sciName (rand-nth @birds))

;; Wrap that `:sciName` in a `find-image` call and evaluate it.

;; Now let's do that for a bunch of bird pictures!

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
