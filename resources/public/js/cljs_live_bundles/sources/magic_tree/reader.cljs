(ns magic-tree.reader
  (:refer-clojure :exclude [peek next])
  (:require [cljs.tools.reader.reader-types :as r])
  (:import [goog.string StringBuffer]))

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

(def buf (StringBuffer.))

(defn read-while
  "Read while the chars fulfill the given condition. Ignores
   the unmatching char."
  [^not-native reader p? & [eof?]]
  (let [eof? (if ^:boolean (nil? eof?)
               (not (p? nil))
               eof?)]
    (.clear buf)
    (loop []
      (if-let [c (r/read-char reader)]
        (if ^:boolean (p? c)
          (do
            (.append buf c)
            (recur))
          (do
            (r/unread reader c)
            (.toString buf)))
        (if ^:boolean eof?
          (.toString buf)
          (throw-reader reader "Unexpected EOF."))))))

(defn read-until
  "Read until a char fulfills the given condition. Ignores the
   matching char."
  [^not-native reader p?]
  (read-while reader (complement p?) (p? nil)))

(defn next
  "Read next char."
  [^not-native reader]
  (r/read-char reader))

(defn ignore
  "Ignore the next character."
  [^not-native reader]
  (r/read-char reader))

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
    (let [next-node (read-fn reader)]
      (cond (nil? next-node)
            out
            (= "error" (namespace (get next-node :tag)))
            (conj out next-node)
            :else
            (recur reader (conj out next-node))))))

(defn position
  "Create map of `row-k` and `col-k` representing the current reader position."
  [^not-native reader]
  #js [(dec (r/get-line-number reader) )
       (dec (r/get-column-number reader))])

(defn read-with-position
  "Use the given function to read value, then attach row/col metadata."
  [^not-native reader read-fn]
  ;; X dec row, because we wrap forms with [\n ...]
  ;; X dec end-col, because that char belongs to the next form
  (let [start-line (dec (r/get-line-number reader))
        start-column (dec (r/get-column-number reader))
        [tag value opts :as form] (read-fn reader)]
    (when-not (nil? form)
      (merge {:tag        tag
              :value      value
              :line       start-line
              :column     start-column
              :end-line   (dec (r/get-line-number reader))
              :end-column (dec (r/get-column-number reader))}
             opts))))

(defn read-n
  "Call the given function on the given reader until `n` values matching `p?` have been
   collected."
  [^not-native reader node-tag read-fn p? n]
  {:pre [(pos? n)]}
  (loop [c 0
         vs []]
    (if ^:boolean (< c n)
      (if-let [v (read-fn reader)]
        (recur
          (if ^:boolean (p? v) (inc c) c)
          (conj vs v))
        (throw-reader
          reader
          "%s node expects %d value%s."
          node-tag
          n
          (if ^:boolean (= n 1) "" "s")))
      vs)))

(defn- read-string-data
  [^not-native reader]
  (ignore reader)
  (.clear buf)
  (loop [escape? false]
    (if-let [c (r/read-char reader)]
      (cond (and (not escape?) (identical? c \"))
            (.toString buf)
            :else
            (do
              (.append buf c)
              (recur (and (not escape?) (identical? c \\)))))
      (throw-reader reader "Unexpected EOF while reading string."))))