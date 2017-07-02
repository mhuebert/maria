;;   Copyright (c) Nicola Mometto, Rich Hickey & contributors.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns ^{:doc "An EDN reader in clojure"
      :author "Bronsa"}
  cljs.tools.reader.edn
  (:refer-clojure :exclude [read read-string char default-data-readers])
  (:require [cljs.tools.reader.reader-types :refer
             [read-char reader-error unread peek-char indexing-reader?
              get-line-number get-column-number get-file-name string-push-back-reader]]
            [cljs.tools.reader.impl.utils :refer
             [char ex-info? whitespace? numeric? desugar-meta namespace-keys second']]
            [cljs.tools.reader.impl.commons :refer
             [number-literal? read-past match-number parse-symbol read-comment throwing-reader]]
            [cljs.tools.reader :refer [default-data-readers char-code]]
            [goog.string :as gstring])
  (:import goog.string.StringBuffer))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(declare read macros dispatch-macros)

(defn- ^boolean macro-terminating? [ch]
  (and (not (identical? \# ch))
       (not (identical? \' ch))
       (not (identical? \: ch))
       (macros ch)))

(defn- ^boolean not-constituent? [ch]
  (or (identical? \@ ch)
      (identical? \` ch)
      (identical? \~ ch)))

(defn- read-token
  ([rdr initch]
     (read-token rdr initch true))
  ([rdr initch validate-leading?]
     (cond
      (not initch)
      (reader-error rdr "EOF while reading")

      (and validate-leading?
           (not-constituent? initch))
      (reader-error rdr "Invalid leading character: " initch)

      :else
      (loop [sb (StringBuffer.)
             ch (do (unread rdr initch) initch)]
        (if (or (whitespace? ch)
                (macro-terminating? ch)
                (nil? ch))
          (str sb)
          (if (not-constituent? ch)
            (reader-error rdr "Invalid constituent character: " ch)
            (recur (doto sb (.append (read-char rdr))) (peek-char rdr))))))))

(declare read-tagged)

(defn- read-dispatch
  [rdr _ opts]
  (if-let [ch (read-char rdr)]
    (if-let [dm (dispatch-macros ch)]
      (dm rdr ch opts)
      (if-let [obj (read-tagged (doto rdr (unread ch)) ch opts)]
        obj
        (reader-error rdr "No dispatch macro for " ch)))
    (reader-error rdr "EOF while reading character")))

(defn- read-unmatched-delimiter
  [rdr ch opts]
  (reader-error rdr "Unmatched delimiter " ch))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; readers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- read-unicode-char
  ([token offset length base]
     (let [l (+ offset length)]
       (when-not (== (count token) l)
         (throw (ex-info (str "Invalid unicode character: \\" token)
                         {:type :illegal-argument})))
       (loop [i offset uc 0]
         (if (== i l)
           (js/String.fromCharCode uc)
           (let [d (char-code (nth token i) base)]
             (if (== d -1)
               (throw (ex-info (str "Invalid digit: " (nth token i))
                               {:type :illegal-argument}))
               (recur (inc i) (+ d (* uc base)))))))))

  ([rdr initch base length exact?]
     (loop [i 1 uc (char-code initch base)]
       (if (== uc -1)
         (throw (ex-info (str "Invalid digit: " initch)
                         {:type :illegal-argument}))
         (if-not (== i length)
           (let [ch (peek-char rdr)]
             (if (or (whitespace? ch)
                     (macros ch)
                     (nil? ch))
               (if exact?
                 (throw (ex-info (str "Invalid character length: " i ", should be: " length)
                                 {:type :illegal-argument}))
                 (js/String.fromCharCode uc))
               (let [d (char-code ch base)]
                 (read-char rdr)
                 (if (== d -1)
                   (throw (ex-info (str "Invalid digit: " ch)
                                   {:type :illegal-argument}))
                   (recur (inc i) (+ d (* uc base)))))))
           (js/String.fromCharCode uc))))))

(def ^:private ^:const upper-limit (int \uD7ff))
(def ^:private ^:const lower-limit (int \uE000))

(defn- read-char*
  [rdr backslash opts]
  (let [ch (read-char rdr)]
    (if-not (nil? ch)
      (let [token (if (or (macro-terminating? ch)
                          (not-constituent? ch)
                          (whitespace? ch))
                    (str ch)
                    (read-token rdr ch false))
            token-len (count token)]
        (cond

         (== 1 token-len)  (nth token 0)

         (identical? token "newline") \newline
         (identical? token "space") \space
         (identical? token "tab") \tab
         (identical? token "backspace") \backspace
         (identical? token "formfeed") \formfeed
         (identical? token "return") \return

         (gstring/startsWith token "u")
         (let [c (read-unicode-char token 1 4 16)
               ic (.charCodeAt c)]
           (if (and (> ic upper-limit)
                    (< ic lower-limit))
             (reader-error rdr "Invalid character constant: \\u" c)
             c))

         (gstring/startsWith token "o")
         (let [len (dec token-len)]
           (if (> len 3)
             (reader-error rdr "Invalid octal escape sequence length: " len)
             (let [uc (read-unicode-char token 1 len 8)]
               (if (> (int uc) 0377)
                 (reader-error rdr "Octal escape sequence must be in range [0, 377]")
                 uc))))

         :else (reader-error rdr "Unsupported character: \\" token)))
      (reader-error rdr "EOF while reading character"))))

(defn- read-delimited
  [delim rdr opts]
  (let [first-line (when (indexing-reader? rdr)
                     (get-line-number rdr))
        delim (char delim)]
    (loop [a (transient [])]
      (let [ch (read-past whitespace? rdr)]
        (when-not ch
          (reader-error rdr "EOF while reading"
                        (if first-line
                          (str ", starting at line" first-line))))
        (if (= delim (char ch))
          (persistent! a)
          (if-let [macrofn (macros ch)]
            (let [mret (macrofn rdr ch opts)]
              (recur (if-not (identical? mret rdr) (conj! a mret) a)))
            (let [o (read (doto rdr (unread ch)) true nil opts)]
              (recur (if-not (identical? o rdr) (conj! a o) a)))))))))

(defn- read-list
  [rdr _ opts]
  (let [the-list (read-delimited \) rdr opts)]
    (if (empty? the-list)
      '()
      (apply list the-list))))

(defn- read-vector
  [rdr _ opts]
  (read-delimited \] rdr opts))

(defn- read-map
  [rdr _ opts]
  (let [l (to-array (read-delimited \} rdr opts))]
    (when (== 1 (bit-and (alength l) 1))
      (reader-error rdr "Map literal must contain an even number of forms"))
    (apply hash-map l)))

(defn- read-number
  [reader initch opts]
  (loop [sb (doto (StringBuffer.) (.append initch))
         ch (read-char reader)]
    (if (or (whitespace? ch) (macros ch) (nil? ch))
      (let [s (str sb)]
        (unread reader ch)
        (or (match-number s)
            (reader-error reader "Invalid number format [" s "]")))
      (recur (doto sb (.append ch)) (read-char reader)))))

(defn- escape-char [sb rdr]
  (let [ch (read-char rdr)]
    (case ch
      \t "\t"
      \r "\r"
      \n "\n"
      \\ "\\"
      \" "\""
      \b "\b"
      \f "\f"
      \u (let [ch (read-char rdr)]
           (if (== -1 (js/parseInt (int ch) 16))
             (reader-error rdr "Invalid unicode escape: \\u" ch)
             (read-unicode-char rdr ch 16 4 true)))
      (if (numeric? ch)
        (let [ch (read-unicode-char rdr ch 8 3 false)]
          (if (> (int ch) 0337)
            (reader-error rdr "Octal escape sequence must be in range [0, 377]")
            ch))
        (reader-error rdr "Unsupported escape character: \\" ch)))))

(defn- read-string*
  [reader _ opts]
  (loop [sb (StringBuffer.)
         ch (read-char reader)]
    (case ch
      nil (reader-error reader "EOF while reading string")
      \\ (recur (doto sb (.append (escape-char sb reader)))
                (read-char reader))
      \" (str sb)
      (recur (doto sb (.append ch)) (read-char reader)))))

(defn- read-symbol
  [rdr initch]
  (when-let [token (read-token rdr initch)]
    (case token

      ;; special symbols
      "nil" nil
      "true" true
      "false" false
      "/" '/
      "NaN" js/Number.NaN
      "-Infinity" js/Number.NEGATIVE_INFINITY
      ("Infinity" "+Infinity") js/Number.POSITIVE_INFINITY

      (or (when-let [p (parse-symbol token)]
            (symbol (p 0) (p 1)))
          (reader-error rdr "Invalid token: " token)))))

(defn- read-keyword
  [reader initch opts]
  (let [ch (read-char reader)]
    (if-not (whitespace? ch)
      (let [token (read-token reader ch)
            s (parse-symbol token)]
        (if (and s (== -1 (.indexOf token "::")))
          (let [ns (s 0)
                name (s 1)]
            (if (identical? \: (nth token 0))
              (reader-error reader "Invalid token: :" token) ;; no ::keyword in edn
              (keyword ns name)))
          (reader-error reader "Invalid token: :" token)))
      (reader-error reader "Invalid token: :"))))

(defn- wrapping-reader
  [sym]
  (fn [rdr _ opts]
    (list sym (read rdr true nil opts))))

(defn- read-meta
  [rdr _ opts]
  (let [m (desugar-meta (read rdr true nil opts))]
    (when-not (map? m)
      (reader-error rdr "Metadata must be Symbol, Keyword, String or Map"))
    (let [o (read rdr true nil opts)]
      (if (implements? IMeta o)
        (with-meta o (merge (meta o) m))
        (reader-error rdr "Metadata can only be applied to IMetas")))))

(defn- read-set
  [rdr _ opts]
  (set (read-delimited \} rdr opts)))

(defn- read-discard
  [rdr _ opts]
  (doto rdr
    (read true nil true)))

(defn- read-namespaced-map
  [rdr _ opts]
  (let [token (read-token rdr (read-char rdr))]
    (if-let [ns (some-> token parse-symbol second')]
      (let [ch (read-past whitespace? rdr)]
        (if (identical? ch \{)
          (let [items (read-delimited \} rdr opts)]
            (when (odd? (count items))
              (reader-error rdr "Map literal must contain an even number of forms"))
            (let [keys (take-nth 2 items)
                  vals (take-nth 2 (rest items))]
              (zipmap (namespace-keys (str ns) keys) vals)))
          (reader-error rdr "Namespaced map must specify a map")))
      (reader-error rdr "Invalid token used as namespace in namespaced map: " token))))

(defn- macros [ch]
  (case ch
    \" read-string*
    \: read-keyword
    \; read-comment
    \^ read-meta
    \( read-list
    \) read-unmatched-delimiter
    \[ read-vector
    \] read-unmatched-delimiter
    \{ read-map
    \} read-unmatched-delimiter
    \\ read-char*
    \# read-dispatch
    nil))

(defn- dispatch-macros [ch]
  (case ch
    \^ read-meta                ;deprecated
    \{ read-set
    \< (throwing-reader "Unreadable form")
    \! read-comment
    \_ read-discard
    \: read-namespaced-map
    nil))

(defn- read-tagged [rdr initch opts]
  (let [tag (read rdr true nil opts)
        object (read rdr true nil opts)]
    (if-not (symbol? tag)
      (reader-error rdr "Reader tag must be a symbol"))
    (if-let [f (or (get (:readers opts) tag)
                   (default-data-readers tag))]
      (f object)
      (if-let [d (:default opts)]
        (d tag object)
        (reader-error rdr "No reader function for tag " (name tag))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Public API
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn read
  "Reads the first object from an IPushbackReader or a java.io.PushbackReader.
   Returns the object read. If EOF, throws if eof-error? is true otherwise returns eof.
   If no reader is provided, *in* will be used.

   Reads data in the edn format (subset of Clojure data):
   http://edn-format.org

   clojure.tools.reader.edn/read doesn't depend on dynamic Vars, all configuration
   is done by passing an opt map.

   opts is a map that can include the following keys:
   :eof - value to return on end-of-file. When not supplied, eof throws an exception.
   :readers  - a map of tag symbols to data-reader functions to be considered before default-data-readers.
              When not supplied, only the default-data-readers will be used.
   :default - A function of two args, that will, if present and no reader is found for a tag,
              be called with the tag and the value."
  ([reader] (read {} reader))
  ([{:keys [eof] :as opts} reader]
     (let [eof-error? (not (contains? opts :eof))]
       (read reader eof-error? eof opts)))
  ([reader eof-error? eof opts]
     (try
       (loop []
         (let [ch (read-char reader)]
           (cond
            (whitespace? ch) (recur)
            (nil? ch) (if eof-error? (reader-error reader "EOF") eof)
            (number-literal? reader ch) (read-number reader ch opts)
            :else (let [f (macros ch)]
                    (if f
                      (let [res (f reader ch opts)]
                        (if (identical? res reader)
                          (recur)
                          res))
                      (read-symbol reader ch))))))
       (catch js/Error e
         (if (ex-info? e)
           (let [d (ex-data e)]
             (if (= :reader-exception (:type d))
               (throw e)
               (throw (ex-info (.-message e)
                               (merge {:type :reader-exception}
                                      d
                                      (if (indexing-reader? reader)
                                        {:line   (get-line-number reader)
                                         :column (get-column-number reader)
                                         :file   (get-file-name reader)}))
                               e))))
           (throw (ex-info (.-message e)
                           (merge {:type :reader-exception}
                                  (if (indexing-reader? reader)
                                    {:line   (get-line-number reader)
                                     :column (get-column-number reader)
                                     :file   (get-file-name reader)}))
                           e)))))))

(defn read-string
  "Reads one object from the string s.
   Returns nil when s is nil or empty.

   Reads data in the edn format (subset of Clojure data):
   http://edn-format.org

   opts is a map as per clojure.tools.reader.edn/read"
  ([s] (read-string {:eof nil} s))
  ([opts s]
     (when (and s (not= s ""))
       (read opts (string-push-back-reader s)))))
