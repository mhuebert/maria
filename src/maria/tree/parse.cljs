;; modified from https://github.com/rundis/rewrite-cljs
;; https://github.com/rundis/rewrite-cljs/blob/master/LICENSE

(ns maria.tree.parse
  (:require [maria.tree.reader :as rd]
            [maria.tree.to-string :refer [to-string]]
            [cljs.pprint :refer [pprint]]
            [cljs.tools.reader.reader-types :as r]
            [cljs.tools.reader.edn :as edn]
            [cljs.test :refer-macros [is are]]))

(enable-console-print!)

(def ^:dynamic ^:private *delimiter*
  nil)

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

(defn- read-to-boundary
  [reader & [allowed]]
  (let [allowed? (set allowed)]
    (rd/read-until
      reader
      #(and (not (allowed? %))
            (or (whitespace? %)
                (boundary? %))))))

(defn- read-to-char-boundary
  [reader]
  (let [c (rd/next reader)]
    (str c
         (if (not= c \\)
           (read-to-boundary reader)
           ""))))

(defn- dispatch
  [c]
  (cond (nil? c) :eof
        (whitespace? c) :whitespace
        (= c *delimiter*) :delimiter
        :else (get {\^ :meta
                    \# :sharp
                    \( :list
                    \[ :vector
                    \{ :map
                    \} :unmatched
                    \] :unmatched
                    \) :unmatched
                    \~ :unquote
                    \' :quote
                    \` :syntax-quote
                    \; :comment
                    \@ :deref
                    \" :string
                    \: :keyword}
                   c :token)))

(defmulti ^:private parse-next*
          (comp dispatch rd/peek))

(defn parse-next
  [reader]
  (rd/read-with-meta reader parse-next*))

(defn- parse-delim
  [reader delimiter]
  (rd/ignore reader)
  (->> #(binding [*delimiter* delimiter]
         (parse-next %))
       (rd/read-repeatedly reader)))

(defn printable-only? [n]
  (contains? #{:space :comma :newline :comment}
             (:tag n)))

(defn- parse-printables
  [reader node-tag n & [ignore?]]
  (when ignore?
    (rd/ignore reader))
  (rd/read-n
    reader
    node-tag
    parse-next
    (complement printable-only?)
    n))

#_(defn parse-next [reader]
    (case (dispatch (rd/peek reader))
      :meta nil
      :whitespace nil))
(defmethod parse-next* :whitespace
  #_"Parse as much whitespace as possible. The created node can either contain
     only linebreaks or only space/tabs."
  [reader]
  (let [c (rd/peek reader)]
    (cond (linebreak? c)
          {:tag    :newline
           :string (rd/read-while reader linebreak?)}

          (comma? c)
          {:tag    :comma
           :string (rd/read-while reader comma?)}

          :else
          {:tag    :space
           :string (rd/read-while reader space?)})))

(defmethod parse-next* :comment
  [reader]
  (rd/ignore reader)
  {:tag   :comment
   :value (rd/read-until reader
                         #(or (nil? %) (linebreak? %))
                         true)})

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
   :value (rd/read-string-data reader)})

(defn string->edn
  "Convert string to EDN value."
  [s]
  (edn/read-string s))

(defn- symbol-node
  "Symbols allow for certain boundary characters that have
   to be handled explicitly."
  [reader value value-string]
  (let [suffix (read-to-boundary reader [\' \:])]
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
  (let [first-char (rd/next reader)
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
  (rd/ignore reader)
  (if-let [c (rd/peek reader)]
    (if (= c \:)
      {:tag         :keyword
       :value       (edn/read reader)
       :namespaced? true}
      (do (r/unread reader \:)
          {:tag   :keyword
           :value (edn/read reader)}))
    (rd/throw-reader reader "unexpected EOF while reading keyword.")))

(defmethod parse-next* :delimiter
  [reader]
  (rd/ignore reader))

(defmethod parse-next* :unmatched
  [reader]
  (rd/throw-reader
    reader
    "Unmatched delimiter: %s"
    (rd/peek reader)))

(defmethod parse-next* :eof
  [reader]
  (when *delimiter*
    (rd/throw-reader reader "Unexpected EOF.")))

(defmethod parse-next* :meta
  [reader]
  (rd/ignore reader)
  {:tag   :meta
   :value (parse-printables reader :meta 2)})

(defn indexing-reader
  "Create reader for strings."
  [s]
  (r/indexing-push-back-reader
    (r/string-push-back-reader s)))

(defn clj-tree [s]
  {:tag      :base
   :children (rest (:children (rd/read-with-meta (indexing-reader (str "[\n" s "]")) parse-next*)))})


(println "\nTree Examples:\n")
(doseq [string ["1"
                "prn"
                "\"hello\""
                ""
                ":hello"
                "::wha"
                "[1 2 3]\n3 4  5, 9"
                "^:dynamic *thing*"
                "(f x)"
                ]]
  (let [tree (clj-tree string)
        emitted-string (to-string tree)]
    (is (= string emitted-string))
    (println "String: " (with-out-str (pprint string)) "\n"
             (with-out-str (pprint tree)) "\n--\n")))
