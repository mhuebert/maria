
;; # ‘Cells’ for asynchronous dataflow

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
