(ns maria.live.source-lookups
  (:require [cljs.tools.reader.reader-types :as rt]
            [lark.tree.parse :as parse]
            [lark.tree.core :as tree]
            [clojure.string :as string]
            [maria.eval :as e]
            [lark.eval :as live-eval]
            [goog.net.XhrIo :as xhr]
            [maria.util :as util])
  (:import goog.string.StringBuffer))

;; may the wrath of God feast upon those who introduce 1- and 0-indexes into the same universe

(defn read-node [reader]
  (parse/parse-next reader))

(defn index-position
  "Given an index into a string, returns the 1-indexed line+column position"
  [idx source]
  (let [lines (as-> source source
                    (subs source 0 idx)
                    (string/split source "\n"))]
    {:line   (count lines)
     :column (count (last lines))}))

(defn source-from
  "Trim string, from beginning until the provided 1-indexed line+column position."
  [source {:keys [line column]}]
  (as-> source source
        (string/split source "\n")
        (drop (dec line) source)
        (vec source)
        (update source (dec (count source)) #(subs % (dec column) (count %)))
        (string/join "\n" source)))

(defn source-of-form-at-position
  "Given a 1-indexed position in a source string, return the first form."
  [source position]
  (let [reader (parse/indexing-reader (source-from source position))
        node (read-node reader)]
    (tree/string node)))

(defn source-of-top-level-form
  "Given a 1-indexed position in a source string, return the first form."
  [source {:keys [line] :as the-var}]
  (let [line (dec line)
        reader (parse/indexing-reader source #_(source-from source (assoc position :column 1)))]
    (loop []
      (let [the-node (read-node reader)]
        (cond (nil? the-node)
              nil
              (and (>= (:end-line the-node) line)
                   (not (tree/whitespace? the-node)))
              (tree/string the-node)
              :else (recur))))))

(defn js-match [js-source {:keys [compiled-js intermediate-values] :as result}]
  (or (some->> intermediate-values
               (keep (partial js-match js-source))
               (first))
      (when compiled-js
        (let [i (.indexOf compiled-js js-source)]
          (when (> i -1)
            (assoc result :index i))))))

(defn js-source->clj-source
  "Searches previously compiled ClojureScript<->JavaScript mappings to return the original ClojureScript
  corresponding to compiled JavaScript"
  [js-source]
  (when-let [{:keys [index source compiled-js source-map]} (->> @e/eval-log
                                                                (keep (partial js-match js-source))
                                                                (first))]
    (let [pos (-> (live-eval/mapped-cljs-position (index-position index compiled-js) source-map)
                  (update :line inc)
                  (update :column inc))]
      (source-of-form-at-position source pos))))

(defn demunge-symbol-str [s]
  (when s
    (let [parts (string/split (demunge s) "/")]
      (if (> (count parts) 1)
        (str (string/join "." (drop-last parts)) "/" (last parts))
        (first parts)))))

(def fn-var
  "Look up the var for a function using its `name` property"
  (memoize
    (fn [f]
      (when (fn? f)
        (or (when-let [munged-sym (util/some-str (aget f "name"))]
              (e/resolve-var (symbol (demunge-symbol-str munged-sym))))
            (first (for [[_ ns-data] (get-in @e/c-state [:cljs.analyzer/namespaces])
                         [_ the-var] (ns-data :defs)
                         :when (= f (live-eval/var-value the-var))]
                     the-var)))))))

(def source-path "/js/cljs_live_bundles/sources")

(defn var-source
  "Look up the source code corresponding to a var's metadata"
  [{{meta-file :file :as meta} :meta file :file name :name :as the-var} cb]
  (if-let [file (or file meta-file)]
    (let [namespace-path (as-> (namespace name) path
                               (string/split path ".")
                               (map munge path)
                               (string/join "/" path)
                               (string/replace path "$macros" ""))
          file (re-find (re-pattern (str namespace-path ".*")) file)]
      (if-let [source (get @live-eval/cljs-cache file)]
        (cb {:value (source-of-top-level-form source the-var)})
        (xhr/send (str source-path "/" file)
                  (fn [e]
                    (let [target (.-target e)]
                      (if (.isSuccess target)
                        (let [source (.getResponseText target)]
                          ;; strip source to line/col from meta
                          (cb {:value (source-of-top-level-form source meta)}))
                        (cb {:error (str "File not found: `" file "`\n" (.getLastError target))}))))
                  "GET")))
    (cb {:error (str "File not specified for `" name "`")})))

(defn fn-source-sync [f]
  (when (fn? f)
    (or (js-source->clj-source (.toString f))
        (.toString f))))

(defn fn-name
  [f]
  (or (some-> (aget f "name") (demunge-symbol-str))
      (:name (fn-var f))))