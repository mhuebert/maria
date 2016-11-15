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

(def ^:dynamic ^:private *delimiter* nil)
(declare parse-next)

(defn whitespace?
  [c]
  (and c (#{\, " " \newline \return} c)))

(defn boundary?
  [c]
  "Check whether a given char is a token boundary."
  (contains?
    #{\" \: \; \' \@ \^ \` \~ \( \) \[ \] \{ \} \\ nil} c))

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
        (= c *delimiter*) :delimiter
        :else (get {\,       :comma
                    " "      :space
                    \newline :newline
                    \return  :newline
                    \^       :meta
                    \#       :sharp
                    \(       :list
                    \[       :vector
                    \{       :map
                    \}       :unmatched
                    \]       :unmatched
                    \)       :unmatched
                    \~       :unquote
                    \'       :quote
                    \`       :syntax-quote
                    \;       :comment
                    \@       :deref
                    \"       :string
                    \:       :keyword}
                   c :token)))

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

(def brackets {\( \)
               \[ \]
               \{ \}})

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

(defn parse-keyword
  [reader]
  (rd/ignore reader)
  (if-let [c (rd/peek reader)]
    (if (= c \:)
      {:value       (edn/read reader)
       :namespaced? true}
      (do (r/unread reader \:)
          {:value (edn/read reader)}))
    (rd/throw-reader reader "unexpected EOF while reading keyword.")))

(defn parse-sharp
  [reader]
  (rd/ignore reader)
  (case (rd/peek reader)
    nil (rd/throw-reader reader "Unexpected EOF.")
    \{ {:tag      :set
        :children (parse-delim reader \})}
    \( {:tag      :fn
        :children (parse-delim reader \))}
    \" {:tag   :regex
        :value (rd/read-string-data reader)}
    \^ {:tag      :meta
        :children (parse-printables reader :meta 2 true)
        :prefix   "#^"}
    \' {:tag      :var
        :children (parse-printables reader :var 1 true)}
    \_ {:tag      :uneval
        :children (parse-printables reader :uneval 1 true)}
    \? (do
         (rd/next reader)
         {:tag :reader-macro
          :children
               (let [read1 (fn [] (parse-printables reader :reader-macro 1))]
                 (cons (case (rd/peek reader)
                         ;; the easy case, just emit a token
                         \( {:tag    :token
                             :string "?"}

                         ;; the harder case, match \@, consume it and emit the token
                         \@ (do (rd/next reader)
                                {:tag    :token
                                 :string "?@"})
                         ;; otherwise no idea what we're reading but its \? prefixed
                         (do (rd/unread reader \?)
                             (read1)))
                       (read1)))})
    {:tag      :reader-macro
     :children (parse-printables reader :reader-macro 2)}))

(defn parse-next*
  [reader]
  (let [c (rd/peek reader)
        tag (dispatch c)
        node-f (case tag

                 :token parse-token
                 :keyword parse-keyword
                 :sharp parse-sharp

                 :comment (do (rd/ignore reader)
                              {:value (rd/read-until reader (fn [x] (or (nil? x) (#{\newline \return} x))) true)})
                 (:newline
                   :comma
                   :space) {:string (rd/read-while reader (partial = c))}
                 (:list
                   :vector
                   :map) {:children (parse-delim reader (get brackets c))}
                 :delimiter rd/ignore
                 :unmatched (rd/throw-reader reader "Unmatched delimiter: %s" c)
                 :eof (when *delimiter*
                        (rd/throw-reader reader "Unexpected EOF (end of file)"))
                 :meta (do (rd/ignore reader)
                           {:prefix   "^"
                            :children (parse-printables reader :meta 2)})
                 :string {:value (rd/read-string-data reader)}

                 )
        node (if (fn? node-f) (node-f reader) node-f)

        node (cond-> node
                     (and node (not (contains? node :tag))) (merge {:tag tag}))]
    node))

(defn parse-next
  [reader]
  (rd/read-with-meta reader parse-next*))

(defn indexing-reader
  "Create reader for strings."
  [s]
  (r/indexing-push-back-reader
    (r/string-push-back-reader s)))

(defn clj-tree [s]
  {:tag      :base
   :children (rest (:children (rd/read-with-meta (indexing-reader (str "[\n" s "]")) parse-next*)))})


(doseq [string ["1"
                "prn"
                "\"hello\""
                ""
                ":hello"
                "::wha"
                "[1 2 3]\n3 4  5, 9"
                "^:dynamic *thing*"
                "(f x)"
                "#{1}"
                "#(+)"
                "#\"[]\""
                "#^ :a {}"
                "#'a"
                "#_()"
                "#?(:cljs)"
                "#?@(:cljs)"
                ]]
  (let [tree (clj-tree string)
        emitted-string (to-string tree)]
    (is (= string emitted-string))
    #_(println "String: " (with-out-str (pprint string)) "\n"
               (with-out-str (pprint tree)) "\n--\n")))
