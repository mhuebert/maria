(ns re-view-routing.core
  (:require [goog.events :as events]
            [goog.dom :as gdom]
            [clojure.string :as string])
  (:import
    [goog History]
    [goog.history Html5History]
    [goog Uri]))

(defn segments
  "Splits route into segments, ignoring leading and trailing slashes."
  [route]
  (let [segments (-> route
                     (string/replace #"[#?].*" "")
                     (string/split \/ -1))
        segments (cond-> segments
                         (= "" (first segments)) (subvec 1))]
    (cond-> segments
            (= "" (last segments)) (pop))))

(comment (assert (= (segments "/") []))
         (assert (= (segments "//") [""]))
         (assert (= (segments "///") ["" ""]))
         (assert (= (segments "/a/b")
                    (segments "a/b/")
                    (segments "a/b") ["a" "b"])))

(defn query
  "Returns query parameters as map."
  [path]
  (let [data (.getQueryData (Uri. path))]
    (reduce (fn [m k]
              (assoc m (keyword k) (.get data k))) {} (.getKeys data))))

(defn parse-path
  "Returns map of parsed location information for path."
  [path]
  {:path     path
   :segments (segments path)
   :query    (query path)})

;; From http://www.lispcast.com/mastering-client-side-routing-with-secretary-and-goog-history
;; Replaces this method: https://closure-library.googlecode.com/git-history/docs/local_closure_goog_history_html5history.js.source.html#line237
;; Without this patch, google closure does not handle changes to query parameters.
(set! (.. Html5History -prototype -getUrl_)
      (fn [token]
        (this-as this
          (if (.-useFragment_ this)
            (str "#" token)
            (str (.-pathPrefix_ this) token)))))

(def browser? (exists? js/window))
(def history-support? (when browser? (.isSupported Html5History)))

(defn get-route
  "In a browsing environment, reads the current location."
  []
  (if history-support?
    (str js/window.location.pathname js/window.location.search)
    (if (= js/window.location.pathname "/")
      (.substring js/window.location.hash 1)
      (str js/window.location.pathname js/window.location.search))))

(defn- make-history
  "Set up browser history, using HTML5 history if available."
  []
  (when browser?
    (if history-support?
      (doto (Html5History.)
        (.setPathPrefix (str js/window.location.protocol
                             "//"
                             js/window.location.host))
        (.setUseFragment false))
      (if (not= "/" js/window.location.pathname)
        (aset js/window "location" (str "/#" (get-route)))
        (History.)))))

(defonce history
         (some-> (make-history)
                 (doto (.setEnabled true))))

(defn nav!
  "Trigger pushstate navigation to token (path)"
  [token]
  (.setToken history token))

(defn- remove-empty
  "Remove empty values/strings from map"
  [m]
  (reduce-kv (fn [m k v]
               (cond-> m
                       (#{nil ""} v) (dissoc k))) m m))

(defn query-string [m]
  (let [js-query (-> m
                     (remove-empty)
                     (clj->js))]
    (-> Uri .-QueryData (.createFromMap js-query) (.toString))))

(defn query-nav!
  [query]
  (nav! (str (.. js/window -location -pathname) "?" (query-string query))))

(defn swap-query!
  [f & args]
  (query-nav! (apply f (query (get-route)) args)))

(defn link?
  "Return true if element is a link"
  [el]
  (some-> el .-tagName (= "A")))

(defn closest
  "Return element or first ancestor of element that matches predicate, like jQuery's .closest()."
  [el pred]
  (if (pred el)
    el
    (gdom/getAncestor el pred)))

(defn click-event-handler
  "Trigger navigation event for click within a link with a valid pushstate href."
  [e]
  (when-let [link (closest (.-target e) link?)]
    (let [location (.-location js/window)
          ;; check to see if we should let the browser handle the link
          ;; (eg. external link, or valid hash reference to an element on the page)
          handle-natively? (or (not= (.-host location) (.-host link))
                               (not= (.-protocol location) (.-protocol link))
                               ;; if only the hash has changed, & element exists on page, allow browser to scroll there
                               (and (.-hash link)
                                    (= (.-pathname location) (.-pathname link))
                                    (not= (.-hash location) (.-hash link))
                                    (.getElementById js/document (subs (.-hash link) 1))))]
      (when-not handle-natively?
        (.preventDefault e)
        (nav! (string/replace (.-href link) (.-origin link) ""))))))

(def intercept-clicks
  "Intercept local links (handle with router instead of reloading page)."
  (memoize (fn intercept
             ([]
              (when browser?
                (intercept js/document)))
             ([element]
              (when browser?
                (events/listen element "click" click-event-handler))))))

(defn listen
  "Set up a listener on route changes. Options:

  intercept-clicks? (boolean, `true`): For `click` events on local links, prevent page reload & fire listener instead.
  fire-now? (boolean, `true`): executes listener immediately, with current parsed route.

  Returns a key which can be passed to `unlisten` to remove listener."
  ([listener]
   (listen listener {}))
  ([listener {:keys [fire-now? intercept-clicks?]
              :or   {fire-now?         true
                     intercept-clicks? true}}]
   (when intercept-clicks? (intercept-clicks))
   (when fire-now? (listener (parse-path (get-route))))
   (events/listen history "navigate" #(listener (parse-path (get-route))))))

(defn unlisten [k]
  (if (fn? k)
    (events/unlisten history "navigate" k)
    (events/unlistenByKey k)))

