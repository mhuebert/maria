(ns maria.tree.reader
  (:refer-clojure :exclude [peek next])
  (:require [cljs.tools.reader.reader-types :as r]
            [goog.string :as gstring]
            [clojure.string :as string]))

(def peek r/peek-char)

(defn throw-reader
  "Throw reader exception, including line/column."
  [^not-native reader fmt & data]
  (let [c (r/get-column-number reader)
        l (r/get-line-number reader)]
    (throw
      (js/Error
        (str fmt data
             " [at line " l ", column " c "]")))))

(defn read-while
  "Read while the chars fulfill the given condition. Ignores
   the unmatching char."
  [^not-native reader p? & [eof?]]
  (let [buf (gstring/StringBuffer.)
        eof? (if (nil? eof?)
               (not (p? nil))
               eof?)]
    (loop []
      (if-let [c (r/read-char reader)]
        (if (p? c)
          (do
            (.append buf c)
            (recur))
          (do
            (r/unread reader c)
            (str buf)))
        (if eof?
          (str buf)
          (throw-reader reader "Unexpected EOF."))))))

(defn read-until
  "Read until a char fulfills the given condition. Ignores the
   matching char."
  [^not-native reader p?]
  (read-while reader (complement p?) (p? nil)))

(defn read-until-inclusive
  "Read until a char fulfills the given condition. Includes the matching char."
  [^not-native reader p?]
  (str (read-while reader (complement p?) (p? nil))
       (r/read-char reader)))

(defn next
  "Read next char."
  [^not-native reader]
  (r/read-char reader))

(defn ignore
  "Ignore the next character."
  [^not-native reader]
  (r/read-char reader)
  nil)

(defn unread
  "Unreads a char. Puts the char back on the reader."
  [^not-native reader ch]
  (r/unread reader ch))

(defn read-repeatedly
  "Call the given function on the given reader until it returns
   a non-truthy value."
  [^not-native reader read-fn]
  (loop [reader reader
         out []]
    (if-let [next-node (read-fn reader)]
      (recur reader (conj out next-node))
      out)))

(defn position
  "Create map of `row-k` and `col-k` representing the current reader position."
  [^not-native reader]
  #js [(dec (r/get-line-number reader))
       (r/get-column-number reader)])

(defn read-with-position
  "Use the given function to read value, then attach row/col metadata."
  [^not-native reader read-fn]
  ;; dec row, because we wrap forms with [\n ...]
  ;; dec end-col, because that char belongs to the next form
  (let [start-pos (position reader)
        form (read-fn reader)
        end-pos (position reader)]
    (some-> form
            (merge {:row     (aget start-pos 0) :col (aget start-pos 1)
                    :end-row (aget end-pos 0) :end-col (dec (aget end-pos 1))}))))

(defn read-n
  "Call the given function on the given reader until `n` values matching `p?` have been
   collected."
  [^not-native reader node-tag read-fn p? n]
  {:pre [(pos? n)]}
  (loop [c 0
         vs []]
    (if (< c n)
      (if-let [v (read-fn reader)]
        (recur
          (if (p? v) (inc c) c)
          (conj vs v))
        (throw-reader
          reader
          "%s node expects %d value%s."
          node-tag
          n
          (if (= n 1) "" "s")))
      vs)))

(defn- read-string-data
  [^not-native reader]
  (ignore reader)
  (let [buf (gstring/StringBuffer.)]
    (loop [escape? false
           lines []]
      (if-let [c (r/read-char reader)]
        (cond (and (not escape?) (identical? c \"))
              (.toString buf)

              :else
              (do
                (.append buf c)
                (recur (and (not escape?) (identical? c \\))
                       lines)))
        (throw-reader reader "Unexpected EOF while reading string.")))))

