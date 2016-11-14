;; modified from https://github.com/rundis/rewrite-cljs
;; https://github.com/rundis/rewrite-cljs/blob/master/LICENSE

(ns maria.tree
  (:refer-clojure :exclude [peek next])
  (:require [cljs.tools.reader.reader-types :as r]
            [cljs.tools.reader.edn :as edn]
            [goog.string :as gstring]
            [clojure.string :as string])
  (:require-macros [cljs.test :refer [is are]]))

(def ^:dynamic ^:private *delimiter*
  nil)

(defn throw-reader
  "Throw reader exception, including line/column."
  [reader fmt & data]
  (let [c (r/get-column-number reader)
        l (r/get-line-number reader)]
    (throw
      (js/Error
        (str fmt data
             " [at line " l ", column " c "]")))))

(defn comma? [c] (= \, c))
(defn space? [c] (= " " c))
(defn linebreak?
  [c]
  (contains? #{\newline \return} c))

(defn whitespace?
  [c]
  (and c (or (comma? c)
             (space? c)
             (linebreak? c))))

(defn boundary?
  [c]
  "Check whether a given char is a token boundary."
  (contains?
    #{\" \: \; \' \@ \^ \` \~
      \( \) \[ \] \{ \} \\ nil}
    c))

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
  [reader p?]
  (read-while
    reader
    (complement p?)
    (p? nil)))

(defn read-include-linebreak
  "Read until linebreak and include it."
  [reader]
  (str
    (read-until
      reader
      #(or (nil? %) (linebreak? %)))
    (r/read-char reader)))

(defn- read-to-boundary
  [reader & [allowed]]
  (let [allowed? (set allowed)]
    (read-until
      reader
      #(and (not (allowed? %))
            (or (whitespace? %)
                (boundary? %))))))

(defn next
  "Read next char."
  [reader]
  (r/read-char reader))

(defn- read-to-char-boundary
  [reader]
  (let [c (next reader)]
    (str c
         (if (not= c \\)
           (read-to-boundary reader)
           ""))))

(defn- dispatch
  [c]
  (cond (nil? c) :eof
        (whitespace? c) :whitespace
        (= c *delimiter*) :delimiter
        :else (get {\^ :meta \# :sharp
                    \( :list \[ :vector \{ :map
                    \} :unmatched \] :unmatched \) :unmatched
                    \~ :unquote \' :quote \` :syntax-quote
                    \; :comment \@ :deref \" :string
                    \: :keyword}
                   c :token)))

(defmulti ^:private parse-next*
          (comp dispatch r/peek-char))

(defn ignore
  "Ignore the next character."
  [reader]
  (r/read-char reader)
  nil)

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
  {row-k (r/get-line-number reader)
   col-k (r/get-column-number reader)})

(defn read-with-meta
  "Use the given function to read value, then attach row/col metadata."
  [reader read-fn]
  (let [start-position (position reader :row :col)]
    (if-let [entry (read-fn reader)]
      (->> (position reader :end-row :end-col)
           (merge start-position)
           (merge entry)))))

(defn parse-next
  [reader]
  (read-with-meta reader parse-next*))

(defn- parse-delim
  [reader delimiter]
  (ignore reader)
  (->> #(binding [*delimiter* delimiter]
         (parse-next %))
       (read-repeatedly reader)))


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

(defn printable-only? [n]
  (contains? #{:space :comma :newline :comment}
             (:tag n)))

(defn- parse-printables
  [reader node-tag n & [ignore?]]
  (when ignore?
    (ignore reader))
  (read-n
    reader
    node-tag
    parse-next
    (complement printable-only?)
    n))


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


(defmethod parse-next* :whitespace
  #_"Parse as much whitespace as possible. The created node can either contain
     only linebreaks or only space/tabs."
  [reader]
  (let [c (r/peek-char reader)]
    (cond (linebreak? c)
          {:tag    :newline
           :string (read-while reader linebreak?)}

          (comma? c)
          {:tag    :comma
           :string (read-while reader comma?)}

          :else
          {:tag    :space
           :string (read-while reader space?)})))

(defmethod parse-next* :comment
  [reader]
  (ignore reader)
  {:tag   :comment
   :value (read-include-linebreak reader)})

(defmethod parse-next* :list
  [reader]
  {:tag      :list
   :children (parse-delim reader \))})

(defmethod parse-next* :vector
  [reader]
  {:tag      :vector
   :children (parse-delim reader \])})

(defmethod parse-next* :map
  [reader]
  {:tag      :map
   :children (parse-delim reader \})})

(defmethod parse-next* :string
  [reader]
  {:tag   :string
   :value (read-string-data reader)})



(defn string->edn
  "Convert string to EDN value."
  [s]
  (edn/read-string s))

(defn- symbol-node
  "Symbols allow for certain boundary characters that have
   to be handled explicitly."
  [reader value value-string]
  (let [suffix (read-to-boundary
                 reader
                 [\' \:])]
    (if (empty? suffix)
      {:tag    :token
       :value  value
       :string value-string}
      (let [s (str value-string suffix)]
        {:tag    :token
         :value  (string->edn s)
         :string s}))))

(defn parse-token
  "Parse a single token."
  [reader]
  (let [first-char (next reader)
        s (->> (if (= first-char \\)
                 (read-to-char-boundary reader)
                 (read-to-boundary reader))
               (str first-char))
        v (string->edn s)]
    (if (symbol? v)
      (symbol-node reader v s)
      {:tag    :token
       :value  v
       :string s})))

(defmethod parse-next* :token
  [reader]
  (parse-token reader))

(defmethod parse-next* :keyword
  [reader]
  (ignore reader)
  (if-let [c (r/peek-char reader)]
    (if (= c \:)
      {:tag         :keyword
       :value       (edn/read reader)
       :namespaced? true}
      (do (r/unread reader \:)
          {:tag   :keyword
           :value (edn/read reader)}))
    (throw-reader reader "unexpected EOF while reading keyword.")))

(defmethod parse-next* :delimiter
  [reader]
  (ignore reader))

(defmethod parse-next* :unmatched
  [reader]
  (throw-reader
    reader
    "Unmatched delimiter: %s"
    (r/peek-char reader)))

(defmethod parse-next* :eof
  [reader]
  (when *delimiter*
    (throw-reader reader "Unexpected EOF.")))

(defmethod parse-next* :meta
  [reader]
  (ignore reader)
  {:tag   :meta
   :value (parse-printables reader :meta 2)})

(defn indexing-reader
  "Create reader for strings."
  [s]
  (r/indexing-push-back-reader
    (r/string-push-back-reader s)))

(defn clj-tree [s]
  (read-with-meta (indexing-reader s) parse-next*))


(doseq [example ["\"hello\""
                 ""
                 "[1 2 3]"
                 ":hello"
                 "::wha"
                 "[^:wha {} \"there\"]"]]
  (println example "\n"
           (clj-tree example) "\n--\n"))
