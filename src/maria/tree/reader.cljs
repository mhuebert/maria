(ns maria.tree.reader
  (:refer-clojure :exclude [peek next])
  (:require [cljs.tools.reader.reader-types :as r]
            [goog.string :as gstring]
            [clojure.string :as string]))

(def peek r/peek-char)

(defn throw-reader
  "Throw reader exception, including line/column."
  [reader fmt & data]
  (let [c (r/get-column-number reader)
        l (r/get-line-number reader)]
    (throw
      (js/Error
        (str fmt data
             " [at line " l ", column " c "]")))))

(defn read-while
  "Read while the chars fulfill the given condition. Ignores
   the unmatching char."
  [reader p? & [eof?]]
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
  ([reader p?] (read-until reader p? false))
  ([reader p? include-char?]
   (cond-> (read-while
             reader
             (complement p?)
             (p? nil))
           include-char? (str (r/read-char reader)))))

(defn next
  "Read next char."
  [reader]
  (r/read-char reader))

(defn ignore
  "Ignore the next character."
  [reader]
  (r/read-char reader)
  nil)

(defn unread
  "Unreads a char. Puts the char back on the reader."
  [reader ch]
  (r/unread reader ch))

(defn read-repeatedly
  "Call the given function on the given reader until it returns
   a non-truthy value."
  [reader read-fn]
  (->> (repeatedly #(read-fn reader))
       (take-while identity)
       (doall)))

(defn position
  "Create map of `row-k` and `col-k` representing the current reader position."
  [reader row-k col-k]
  {row-k (dec (r/get-line-number reader))
   col-k (cond-> (r/get-column-number reader)
                 (= col-k :end-col) dec)})

(defn read-with-meta
  "Use the given function to read value, then attach row/col metadata."
  [reader read-fn]
  ;; dec row, because we wrap forms with [\n ...]
  ;; dec end-col, because that char belongs to the next form
  (let [start-position (position reader :row :col)]
    (some-> (read-fn reader)
            (merge start-position (position reader :end-row :end-col)))))

(defn read-n
  "Call the given function on the given reader until `n` values matching `p?` have been
   collected."
  [reader node-tag read-fn p? n]
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

(defn- flush-into
  "Flush buffer and add string to the given vector."
  [lines buf]
  (let [s (.toString buf)]
    (.set buf "")
    (conj lines s)))

(defn- read-string-data
  [^not-native reader]
  (ignore reader)
  (let [buf (gstring/StringBuffer.)]
    (string/join "\n"
                 (loop [escape? false
                        lines []]
                   (if-let [c (r/read-char reader)]
                     (cond (and (not escape?) (identical? c \"))
                           (flush-into lines buf)

                           (identical? c \newline)
                           (recur escape? (flush-into lines buf))

                           :else
                           (do
                             (.append buf c)
                             (recur (and (not escape?) (identical? c \\))
                                    lines)))
                     (throw-reader reader "Unexpected EOF while reading string."))))))