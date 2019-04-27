(ns lark.tree.reader
  (:refer-clojure :exclude [peek next])
  (:require
   [lark.tree.util :as util]
   #?(:cljs [cljs.tools.reader.reader-types :as r]
      :clj
   [clojure.tools.reader.reader-types :as r]))
  #?(:cljs (:import [goog.string StringBuffer])))

(def ^:dynamic *invalid-nodes* nil)
(def ^:dynamic *active-cursor-node* nil)

(def ^:dynamic *delimiter* (list))

(def peek r/peek-char)

(defn current-offset [indexing-pushback-reader]
  (let [pushback-reader (.-rdr indexing-pushback-reader)
        indexing-reader (.-rdr pushback-reader)
        pushback (- (.-buf_len pushback-reader)
                    (.-buf_pos pushback-reader))]
    (- (.-s_pos indexing-reader)
       pushback)))

(def edges
  {:deref ["@"]
   :list [\( \)]
   :fn ["#"]
   :map [\{ \}]
   :meta ["^"]
   :quote ["'"]
   :reader-meta ["#^"]
   :raw-meta ["^"]
   :reader-macro ["#"]
   :regex ["#"]
   :set ["#"]
   :string [\" \"]
   :syntax-quote ["`"]
   :unquote ["~"]
   :unquote-splicing ["~@"]
   :uneval ["#_"]
   :var ["#'"]
   :vector [\[ \]]
   :reader-conditional ["#?"]
   :reader-conditional-splice ["#?@"]
   :selection [\‹ \›]})

(defn whitespace-tag? [tag]
  (util/contains-identical-keyword?
   [:space :newline :tab :comma :cursor :selection]
   tag))

(defn close-bracket? [ch]
  (util/contains-identical? [\) \] \}] ch))

(defn open-bracket? [ch]
  (util/contains-identical? [\( \[ \{] ch))

(defn throw-reader
  "Throw reader exception, including line/column."
  [reader fmt & data]
  (let [c (.-column reader)
        l (.-line reader)]
    (throw
     (#?(:cljs js/Error.
         :clj  Exception.)
      (str fmt data
           " [at line " l ", column " c "]")))))

(def buf #?(:cljs (StringBuffer.)
            :clj  (StringBuilder.)))

(defn read-while
  "Read while the chars fulfill the given condition. Does not consume the unmatching char."
  [reader p? & [eof?]]
  (let [eof? (if ^boolean (nil? eof?)
               (not (p? nil))
               eof?)]
    #?(:cljs (.clear buf)
       :clj  (.setLength buf 0))
    (loop []
      (if-let [c (r/read-char reader)]
        (if ^boolean (p? c)
          (do
            (.append buf c)
            (recur))
          (do
            (r/unread reader c)
            #?(:cljs (.toString buf)
               :clj  (str buf))))
        (if ^boolean eof?
          #?(:cljs (.toString buf)
             :clj  (str buf))
          (throw-reader reader "Unexpected EOF."))))))

(defn read-until
  "Read until a char fulfills the given condition. Does not consume the matching char."
  [reader p?]
  (read-while reader (complement p?) (p? nil)))

(defn next
  "Read next char."
  [reader]
  (r/read-char reader))

(defn ignore
  "Ignore the next character."
  [reader]
  (r/read-char reader))

(defn unread
  "Unreads a char. Puts the char back on the reader."
  [reader ch]
  (r/unread reader ch))

(defn position
  "Returns 0-indexed vector of [line, column] for current reader position."
  [reader]
  [(dec (.-line reader))
   (dec (.-column reader))])

;; TODO
;; :value => (first children)
;; first, last, rest, etc. -- operate on children
;; seq -- returns children

(defprotocol IRange
  ;; mutates range of node -- for internal parser use
  (assoc-range! [this position]))

(defprotocol IAppend
  (append [this x]))

(deftype Node [^:mutable tag
               ^:mutable options
               ^:mutable range
               ^:mutable value
               ^:mutable children]

  ;; ------------- Add child nodes via `append` --------------

  IAppend
  (append [coll o]
    (Node. tag options range value (conj children o)))

  ;; ------------- Position information stored via `meta` --------------

  IRange
  (assoc-range! [this position]
    (set! range position)
    this)

  ;; ------------- Equality --------------

  IEquiv
  (-equiv [o other]
   ;; position not taken into account
    (and (some? other)
         (= tag (.-tag other))
         (= children (.-children other))
         (= value (.-value other))
         (= range (.-range other))
         (= options (.-options other))))

  ;; ------------- Comparison by range --------------

  IComparable
  (-compare [x y]
    (let [l (- (-lookup x :line) (-lookup y :line))]
      (if (not= l 0)
        l
        (- (-lookup x :column) (-lookup y :column)))))

  ;; ------------- Associative operations on `options` --------------

  IAssociative
  (-contains-key? [this key]
    (case key :tag true
              (:line
               :column
               :end-line
               :end-column) (some? meta)
              :value (some? value)
              :children (some? children)
              :range (some? range)
              (contains? options key)))
  (-assoc [this k VAL]
    (case k
      :tag (Node. VAL options range value children)
      :value (Node. tag options range VAL children)
      :children (Node. tag options range value VAL)
      :range (Node. tag options VAL value children)
      (Node. tag (assoc options k VAL) range value children)))

  ITransientAssociative
  (-assoc! [this k val]
    (case k
      :tag (set! tag val)
      :value (set! value val)
      :children (set! children val)
      :range (set! range val)
      :options (set! options val))
    this)

  ICollection
  (-conj [coll entry]
    (if (vector? entry)
      (-assoc coll (-nth entry 0) (-nth entry 1))
      (reduce -conj
              coll
              entry)))

  ;; `get` supports direct access to tag, value, and positional elements

  ILookup
  (-lookup [this key]
    (-lookup this key nil))
  (-lookup [this key not-found]
    (or (case key :tag tag
                  :value value
                  :children children
                  :range range
                  :line (nth range 0)
                  :column (nth range 1)
                  :end-line (nth range 2)
                  :end-column (nth range 3)
                  :offset (nth range 4)
                  :end-offset (nth range 5)
                  :options options

                  ;; todo
                  ;; see if we should keep this
                  :start {:line (nth range 0)
                          :column (nth range 1)}
                  :end {:line (nth range 2)
                        :column (nth range 3)}
                  nil)
        (get options key not-found)))

  ;; for debugging
  IPrintWithWriter
  (-pr-writer [o writer _]
    (let [options (cond-> (dissoc options :source :invalid-nodes :cursor)
                          range (assoc :range range))]
      (-write writer (str (if (or children (seq options))
                            (cond-> [tag]
                                    options (conj options)
                                    value (conj value)
                                    children (into children))
                            tag))))))

(defn delimiter-error [tag reader]
  (let [[line col] (position reader)]
    (Node. :error {:tag tag
                   :expected (first *delimiter*)} [line
                                                   col
                                                   line
                                                   (inc col)] nil nil)))

(defn current-pos [reader]
  [(dec (.-line reader))
   (dec (.-column reader))])

(defn read-with-position
  "Use the given function to read value, then attach row/col metadata."
  [reader read-fn]
  (let [start-line (dec (.-line reader))
        start-column (dec (.-column reader))
        start-offset (current-offset reader)]
    (when-let [node (read-fn reader)]
      (assoc-range!
       node
       [start-line
        start-column

        (dec (.-line reader))
        (dec (.-column reader))

        start-offset
        (current-offset reader)]))))

(defn report-invalid! [node]
  (let [node (assoc node :invalid? true)]
    (some-> *invalid-nodes*
            (vswap! conj node))
    node))

(defn InvalidToken!
  ([tag value] (InvalidToken! tag value nil))
  ([tag value position]
   (report-invalid!
    (->Node :token {:invalid? true
                    :info {:tag tag}} position value nil))))

(defn Splice
  ([children]
   (->Node :splice nil nil nil children))
  ([node children]
   (Splice (into [node] children))))

(defn CollectionNode [tag nodes]
  (->Node tag nil nil nil nodes))

(defn ValueNode [tag value]
  (->Node tag nil nil value nil))

(defn EmptyNode [tag]
  (->Node tag nil nil nil nil))

(defn split-after-n
  "Splits after `n` values which pass `pred`.

  Returns vector of the form
  [<took-n-values?> <taken-values> <remaining-values>]"
  [n pred stop? coll]
  (loop [remaining coll
         i 0
         taken []]
    (cond (= i n)
          [true taken remaining i]
          (empty? remaining)
          [false taken remaining i]
          :else
          (let [next-item (nth remaining 0)]
            (if (and (some? stop?) (stop? next-item))
              [false taken remaining i]
              (let [count-it? (pred next-item)]
                (recur (subvec remaining 1)
                       (cond-> i
                               count-it? (inc))
                       (conj taken next-item))))))))

(defn take-children
  [reader {:keys [:read-fn
                  :count-pred]
           take-n :take-n}]
  ;; returns `child-values, remaining-values, valid?`
  (loop [reader reader
         i 0
         out []]
    (if (> i 10000)
      (do
        (prn :take-children out)
        (js/console.error (js/Error. "Infinite loop?"))
        [false out nil])
      (if (and (some? take-n) (= i take-n))
        [true out nil]
        (let [{:keys [tag value children] :as next-node} (read-fn reader)
              next-i (if (and (some? take-n) (some? count-pred))
                       (cond-> i
                               (count-pred next-node) (inc))
                       (inc i))]
          (case tag
            :unmatched-delimiter
            (if
             (contains? (set *delimiter*) value)            ;; can match prev
              (do
                (unread reader value)
                [false out nil])
              (recur reader next-i (conj out (report-invalid! next-node))))

            :splice
            (if take-n
              (split-after-n take-n count-pred nil children)
              (recur reader next-i (into out children)))

            (:eof nil)
            [false out nil]

            :matched-delimiter
            (if (and take-n (not= take-n i))
              (do (unread reader value)
                  [false out nil])
              [true out nil])

            (recur reader next-i (conj out next-node))))))))

(defn conj-children
  [coll-node reader {:keys [:read-fn
                            :count-pred]
                     take-n :take-n}]
  (let [[inner-line inner-col] (current-pos reader)
        inner-offset (current-offset reader)
        coll-tag (.-tag coll-node)
        invalid-exit (fn [out]
                       (case coll-tag
                         :base (assoc coll-node :children out)
                         (Splice (let [[left right] (get edges coll-tag)
                                       width (count left)]
                                   (report-invalid!
                                    (-> (EmptyNode :unmatched-delimiter)
                                        (assoc!
                                         :options {:info {:tag coll-tag
                                                          :direction :forward
                                                          :expects right}}
                                         :range [inner-line
                                                 (- inner-col width)
                                                 inner-line
                                                 inner-col
                                                 (- inner-offset width)
                                                 inner-offset]
                                         :value left)))) out)))]
    (loop [reader reader
           i 0
           out []]
      (if (> i 10000)
        (do
          (js/console.error (js/Error. "Infinite loop?"))
          (assoc! coll-node :children out))
        (if (and (some? take-n) (= i take-n))
          (assoc! coll-node :children out)
          (let [{:keys [tag value children] :as next-node} (read-fn reader)
                next-i (if (and (some? take-n) (some? count-pred))
                         (cond-> i
                                 (count-pred next-node) (inc))
                         (inc i))]
            (case tag
              :unmatched-delimiter
              (if
               (contains? (set *delimiter*) value)          ;; can match prev
                (do
                  (unread reader value)
                  (invalid-exit out))
                (recur reader next-i (conj out (report-invalid! next-node))))

              :splice
              (if take-n
                (let [[valid? taken-values remaining-values] (split-after-n take-n count-pred nil children)]
                  (if valid?
                    (Splice (assoc! coll-node :children taken-values)
                            remaining-values)
                    (invalid-exit (into out children))))
                (recur reader next-i (into out children)))

              (:eof nil)
              (invalid-exit out)

              :matched-delimiter
              (if (and take-n (not= take-n i))
                (do (unread reader value)
                    (invalid-exit out))
                (assoc! coll-node :children out))

              (recur reader next-i (conj out next-node)))))))))

(defn NodeWithChildren
  [reader read-fn tag delimiter]
  (r/read-char reader)
  (binding [*delimiter* (cons delimiter *delimiter*)]
    (conj-children (EmptyNode tag) reader {:read-fn read-fn})))

(defn read-string-data
  [node reader]
  (ignore reader)
  #?(:cljs (.clear buf)
     :clj  (.setLength buf 0))
  (loop [escape? false]
    (if-let [c (r/read-char reader)]
      (cond (and (not escape?) (identical? c \"))
            (assoc! node :value #?(:cljs (.toString buf)
                                   :clj  (str buf)))
            :else
            (do
              (.append buf c)
              (recur (and (not escape?) (identical? c \\)))))
      (report-invalid!
       (assoc node :tag :token
                   :options {:tag (:tag node)}
                   :value (str \" #?(:cljs (.toString buf)
                                     :clj  (str buf))))))))

(def non-breaking-space \u00A0)

(defn newline?
  [c]
  (util/contains-identical? [\newline
                             \return]
                            c))

(defn space?
  [c]
  (util/contains-identical? [\space
                             \tab
                             non-breaking-space]
                            c))

(defn whitespace?
  [c]
  (or (util/contains-identical? [\,
                                 \space
                                 \tab
                                 non-breaking-space]
                                c)
      (newline? c)))

(defn brace? [ch]
  (util/contains-identical? [\( \) \[ \] \{ \} \"]
                            ch))

(defn prefix-boundary? [ch]
  (util/contains-identical? [\; \: \' \@ \^ \` \~ \\ nil]
                            ch))

(defn boundary? [ch]
  (or (brace? ch)
      (prefix-boundary? ch)))