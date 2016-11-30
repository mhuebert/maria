;; modified from https://github.com/rundis/rewrite-cljs
;; https://github.com/rundis/rewrite-cljs/blob/master/LICENSE

(ns maria.tree.parse
  (:require [maria.tree.reader :as rd]
            [maria.tree.emit :as unwrap]
            [cljs.pprint :refer [pprint]]
            [cljs.tools.reader.reader-types :as r]
            [cljs.tools.reader.edn :as edn]
            [cljs.test :refer [is are]]))

(enable-console-print!)

(def ^:dynamic ^:private *delimiter* nil)
(declare parse-next)

(def whitespace-chars #js [\, " " "\n" "\r"])
(defn ^:boolean whitespace?
  [c]
  (< -1 (.indexOf whitespace-chars c)))

(def boundary-chars #js [\" \: \; \' \@ \^ \` \~ \( \) \[ \] \{ \} \\ nil])
(defn ^:boolean boundary?
  [c]
  "Check whether a given char is a token boundary."
  (< -1 (.indexOf boundary-chars c)))

(defn- read-to-boundary
  [reader allowed]
  (rd/read-until
    reader
    #(and (not (< -1 (.indexOf allowed %)))
          (or (whitespace? %)
              (boundary? %)))))

(defn- read-to-char-boundary
  [reader]
  (let [c (rd/next reader)]
    (str c
         (if ^:boolean (not (identical? c \\))
           (read-to-boundary reader #js [])
           ""))))

(defn string->edn
  "Convert string to EDN value."
  [s]
  (edn/read-string s))

(defn- dispatch
  [c]
  (cond (identical? c *delimiter*) :delimiter
        (nil? c) :eof
        :else (case c
                \, :comma
                " " :space
                ("\n" "\r") :newline
                \^ :meta
                \# :sharp
                \( :list
                \[ :vector
                \{ :map
                (\} \] \)) :unmatched
                \~ :unquote
                \' :quote
                \` :syntax-quote
                \; :comment
                \@ :deref
                \" :string
                \: :keyword
                :token)))

(defn- parse-delim
  [reader delimiter]
  (rd/ignore reader)
  (->> #(binding [*delimiter* delimiter]
          (parse-next %))
       (rd/read-repeatedly reader)))

(defn ^:boolean printable-only? [n]
  (contains? #{:space :comma :newline :comment}
             (:tag n)))

(defn- parse-printables
  [reader node-tag n & [ignore?]]
  (when-not (nil? ignore?)
    (rd/ignore reader))
  (rd/read-n
    reader
    node-tag
    parse-next
    (complement printable-only?)
    n))

(def brackets {\( \)
               \[ \]
               \{ \}})

(defn parse-token
  "Parse a single token."
  [reader]
  (let [first-char (rd/next reader)
        s (->> (if ^:boolean (identical? first-char \\)
                 (read-to-char-boundary reader)
                 (read-to-boundary reader #js []))
               (str first-char))]
    [:token (str s (when ^:boolean (symbol? (string->edn s))
                     (read-to-boundary reader #js [\' \:])))]))

(defn parse-keyword
  [reader]
  (rd/ignore reader)
  (if-let [c (rd/peek reader)]
    (if ^:boolean (identical? c \:)
      [:namespaced-keyword (edn/read reader)]
      (do (r/unread reader \:)
          [:keyword (edn/read reader)]))
    (rd/throw-reader reader "unexpected EOF while reading keyword.")))

(defn parse-sharp
  [reader]
  (rd/ignore reader)
  (case (rd/peek reader)
    nil (rd/throw-reader reader "Unexpected EOF.")
    \{ [:set (parse-delim reader \})]
    \( [:fn (parse-delim reader \))]
    \" [:regex (rd/read-string-data reader)]
    \^ [:reader-meta (parse-printables reader :reader-meta 2 true)]
    \' [:var (parse-printables reader :var 1 true)]
    \_ [:uneval (parse-printables reader :uneval 1 true)]
    \? (do
         (rd/next reader)
         [:reader-macro
          (let [read-next #(parse-printables reader :reader-macro 1)]
            (cons (case (rd/peek reader)
                    ;; the easy case, just emit a token
                    \( [:token "?"]
                    ;; the harder case, match \@, consume it and emit the token
                    \@ (do (rd/next reader)
                           [:token "?@"])
                    ;; otherwise no idea what we're reading but its \? prefixed
                    (do (rd/unread reader \?)
                        (read-next)))
                  (read-next)))])
    [:reader-macro (parse-printables reader :reader-macro 2)]))

(defn- parse-unquote
  [^not-native reader]
  (rd/ignore reader)
  (let [c (rd/peek reader)]
    (if ^:boolean (identical? c \@)
      [:unquote-splicing (parse-printables reader :unquote 1 true)]
      [:unquote (parse-printables reader :unquote 1)])))

(defn parse-next*
  [reader]
  (let [c (rd/peek reader)
        tag (dispatch c)]
    (case tag
      :token (parse-token reader)
      :keyword (parse-keyword reader)
      :sharp (parse-sharp reader)
      :comment (do (rd/ignore reader)
                   [tag (rd/read-until reader (fn [x] (or (nil? x) (#{\newline \return} x))))])
      (:deref
        :quote
        :syntax-quote) [tag (parse-printables reader tag 1 true)]

      :unquote (parse-unquote reader)

      (:newline
        :comma
        :space) [tag (rd/read-while reader #(identical? % c))]
      (:list
        :vector
        :map) [tag (parse-delim reader (get brackets c))]
      :delimiter (rd/ignore reader)
      :unmatched (rd/throw-reader reader "Unmatched delimiter: %s" c)
      :eof (when-not (nil? *delimiter*)
             (rd/throw-reader reader "Unexpected EOF (end of file)"))
      :meta (do (rd/ignore reader)
                [tag (parse-printables reader :meta 2)])
      :string [tag (rd/read-string-data reader)])))

(defn parse-next
  [reader]
  (rd/read-with-position reader parse-next*))

(defn indexing-reader
  "Create reader for strings."
  [s]
  (r/indexing-push-back-reader
    (r/string-push-back-reader s)))

(defn ast [s]
  (-> (rd/read-with-position (indexing-reader (str "[\n" s "]")) parse-next*)
      (assoc :tag :base)
      (update :value rest)
      (update :end-col dec)))

(comment
  (doseq [[string res-sexp res-str] [["1" '[1]]
                                     ["prn" '[prn]]
                                     ["\"hello\"" '["hello"]]
                                     ["" '[]]
                                     [":hello" [:hello]]
                                     ["::wha" [::wha]]
                                     ["#(+)" '[#(+)]]
                                     ["[1 2 3]\n3 4  5, 9" '[[1 2 3] 3 4 5 9]]
                                     ["^:dynamic *thing*" '[^:dynamic *thing*]]
                                     ["(f x)" '[(f x)]]
                                     ["#{1}" '[#{1}]]
                                     ["#\"[a-z]\""]         ;; two regular expressions can never be equal
                                     ["#^:a {}" '[#^:a {}]]
                                     ["#'a" '[#'a]]
                                     ["@a" '[(deref a)]]
                                     ["#_()" '[#_()]]
                                     ["'a" '[(quote a)]]
                                     ["'a" '['a]]
                                     ["`a" '['a] "[`a]"]    ;;not sure about the sexp here
                                     ["~a" '[~a] "[~a]"]
                                     #_["#?(:cljs)"]
                                     #_["#?@(:cljs)"]
                                     ["(defn parse-sharp\n  [reader]\n  (rd/ignore reader)\n  (case (rd/peek reader)\n    nil (rd/throw-reader reader \"Unexpected EOF.\")\n    \\{ {:tag      :set\n        :children (parse-delim reader \\})}\n    \\( {:tag      :fn\n        :children (parse-delim reader \\))}\n    \\\" {:tag   :regex\n        :value (rd/read-string-data reader)}\n    \\^ {:tag      :meta\n        :children (parse-printables reader :meta 2 true)\n        :prefix   \"#^\"}\n    \\' {:tag      :var\n        :children (parse-printables reader :var 1 true)}\n    \\_ {:tag      :uneval\n        :children (parse-printables reader :uneval 1 true)}\n    \\? (do\n         (rd/next reader)\n         {:tag :reader-macro\n          :children\n               (let [read1 (fn [] (parse-printables reader :reader-macro 1))]\n                 (cons (case (rd/peek reader)\n                         ;; the easy case, just emit a token\n                         \\( {:tag    :token\n                             :string \"?\"}\n\n                         ;; the harder case, match \\@, consume it and emit the token\n                         \\@ (do (rd/next reader)\n                                {:tag    :token\n                                 :string \"?@\"})\n                         ;; otherwise no idea what we're reading but its \\? prefixed\n                         (do (rd/unread reader \\?)\n                             (read1)))\n                       (read1)))})\n    {:tag      :reader-macro\n     :children (parse-printables reader :reader-macro 2)}))"]
                                     ["(defview editor\n              :component-did-mount\n              (fn [this {:keys [value read-only? on-mount] :as props}]\n                (let [editor (js/CodeMirror (js/ReactDOM.findDOMNode (v/react-ref this \"editor-container\"))\n                                            (clj->js (cond-> options\n                                                             read-only? (-> (select-keys [:theme :mode :lineWrapping])\n                                                                            (assoc :readOnly \"nocursor\")))))]\n                  (when value (.setValue editor (str value)))\n\n                  (when-not read-only?\n\n                    ;; event handlers are passed in as props with keys like :event/mousedown\n                    (doseq [[event-key f] (filter (fn [[k v]] (= (namespace k) \"event\")) props)]\n                      (.on editor (name event-key) f))\n\n                    (.on editor \"beforeChange\" ignore-self-op)\n\n                    (v/update-state! this assoc :editor editor)\n\n                    (when on-mount (on-mount editor this)))))\n              :component-will-receive-props\n              (fn [this {:keys [value]} {next-value :value}]\n                (when (and next-value (not= next-value value))\n                  (when-let [editor (:editor (v/state this))]\n                    (binding [*self-op* true]\n                      (set-preserve-cursor editor next-value)))))\n              :should-component-update\n              (fn [_ _ state _ prev-state]\n                (not= (dissoc state :editor) (dissoc prev-state :editor)))\n              :render\n              (fn [this props state]\n                [:.h-100 {:ref \"editor-container\"}]))"]
                                     ["(list (ns maria.core\n        (:require\n          [maria.codemirror :as cm]\n          [maria.eval :refer [eval-src]]\n          [maria.walkthrough :refer [walkthrough]]\n          [maria.tree.parse]\n          [maria.html]\n\n          [clojure.set]\n          [clojure.string :as string]\n          [clojure.walk]\n\n          [cljs.spec :include-macros true]\n          [cljs.pprint :refer [pprint]]\n          [re-db.d :as d]\n          [re-view.subscriptions :as subs]\n          [re-view.routing :as routing :refer [router]]\n          [re-view.core :as v :refer-macros [defview]]\n          [goog.object :as gobj]))\n\n      (enable-console-print!)\n\n      ;; to support multiple editors\n      (defonce editor-id \"maria-repl-left-pane\")\n\n      (defonce _ (d/listen! [editor-id :source] #(gobj/set (.-localStorage js/window) editor-id %)))\n\n      (defn display-result [{:keys [value error warnings]}]\n        [:div.bb.b--near-white\n         (cond error [:.pa3.dark-red.ph3.mv2 (str error)]\n               (v/is-react-element? value) (value)\n               :else [:.bg-white.pv2.ph3.mv2 (if (nil? value) \"nil\" (try (with-out-str (prn value))\n                                                                         (catch js/Error e \"error printing result\")))])\n         (when (seq warnings)\n           [:.bg-near-white.pa2.pre.mv2\n            [:.dib.dark-red \"Warnings: \"]\n            (for [warning (distinct (map #(dissoc % :env) warnings))]\n              (str \"\\n\" (with-out-str (pprint warning))))])])\n\n      (defn scroll-bottom [component]\n        (let [el (js/ReactDOM.findDOMNode component)]\n          (set! (.-scrollTop el) (.-scrollHeight el))))\n\n      (defn last-n [n v]\n        (subvec v (max 0 (- (count v) n))))\n\n      (defview result-pane\n                    :component-did-update scroll-bottom\n                    :component-did-mount scroll-bottom\n                    :render\n                    (fn [this]\n                      [:div.h-100.overflow-auto.code\n                       (map display-result (last-n 50 (first (v/children this))))]))\n\n      (defview repl\n                    :subscriptions {:source      (subs/db [editor-id :source])\n                                    :eval-result (subs/db [editor-id :eval-result])}\n                    :component-will-mount\n                    #(d/transact! [[:db/add editor-id :source (gobj/getValueByKeys js/window #js [\"localStorage\" editor-id])]])\n                    :render\n                    (fn [_ _ {:keys [eval-result source]}]\n                      [:.flex.flex-row.h-100\n                       [:.w-50.h-100.bg-solarized-light\n                        (cm/editor {:value         source\n                                    :event/keydown #(when (and (= 13 (.-which %2)) (.-metaKey %2))\n                                                     (when-let [source (or (cm/selection-text %1)\n                                                                           (cm/bracket-text %1))]\n                                                       (d/transact! [[:db/update-attr editor-id :eval-result (fnil conj []) (eval-src source)]])))\n                                    :event/change  #(d/transact! [[:db/add editor-id :source (.getValue %1)]])})]\n                       [:.w-50.h-100\n                        (result-pane eval-result)]]))\n\n      (defview not-found\n                    :render\n                    (fn [] [:div \"We couldn't find this page!\"]))\n\n      (defview layout\n                    :subscriptions {:main-view (router \"/\" repl\n                                                       \"/walkthrough\" walkthrough\n                                                       not-found)}\n                    :render\n                    (fn [_ _ {:keys [main-view]}]\n                      [:div.h-100\n                       [:.w-100.fixed.bottom-0.z-3\n                        [:.dib.center\n                         [:a.dib.pa2.black-70.no-underline.f6.bg-black-05 {:href \"/\"} \"REPL\"]\n                         [:a.dib.pa2.black-70.no-underline.f6.bg-black-05 {:href \"/walkthrough\"} \"Walkthrough\"]]]\n                       (main-view)]))\n\n      (defn main []\n        (v/render-to-dom (layout) \"maria-main\"))\n\n      (main))"]
                                     ["my:symbol" '[my:symbol]]
                                     ["my::symbol" '[my::symbol]]]]

    (binding [maria.tree.emit/*ns* (symbol "maria.tree.parse")]
      (let [wrapped-str (str "[" string "]")
            tree (ast wrapped-str)
            emitted-string (unwrap/string tree)
            emitted-sexp (unwrap/sexp tree)]
        (when res-sexp
          (is (= emitted-sexp res-sexp)
              (str "Correct res-sexp for: " (subs string 0 30))))
        (when res-str
          (is (= emitted-string res-str)))
        (is (= wrapped-str emitted-string)
            (str "Correct emitted string for: " string))))))

