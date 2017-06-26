(ns maria.source-lookups
  (:require [cljs.tools.reader :as r]
            [cljs.tools.reader.reader-types :as rt]
            [clojure.string :as string]
            [maria.eval :as e]
            [cljs-live.eval :as cljs-eval]
            [cljs-live.compiler :as c])
  (:import goog.string.StringBuffer))

;; may the wrath of God feast upon those who introduce 1- and 0-indexes into the same universe

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
  (let [reader (rt/source-logging-push-back-reader (source-from source position))
        form (r/read reader)]
    (:source (meta form))))

(defn js-source->clj-source
  "Searches previously compiled ClojureScript<->JavaScript mappings to return the original ClojureScript
  corresponding to compiled JavaScript"
  [js-source]
  (when-let [{:keys [index source compiled-js source-map]} (first (for [{:keys [compiled-js] :as res} @e/eval-log
                                                                        :when compiled-js
                                                                        :let [i (.indexOf compiled-js js-source)]
                                                                        :when (> i -1)]
                                                                    (merge res {:index i})))]
    (let [pos (-> (cljs-eval/mapped-cljs-position (index-position index compiled-js) source-map)
                  (update :line inc)
                  (update :column inc))]
      (source-of-form-at-position source pos))))

(defn fn-var
  "Look up the var for a function using its `name` property"
  [f]
  (when-let [munged-sym (aget f "name")]
    (let [parts (string/split (demunge munged-sym) "/")
          sym (symbol (string/join "." (drop-last parts)) (last parts))]
      (cljs-eval/resolve-var sym))))

(defn var-source
  "Look up the source code corresponding to a var's metadata"
  [{:keys [meta] :as pos}]
  (when-let [source (get @c/cljs-cache (:file meta))]
    (source-of-form-at-position source pos)))

(defn fn-source
  "Look up the source for a function, preferring ClojureScript source over JavaScript"
  [f]
  (or (some-> (fn-var f) (var-source))
      (js-source->clj-source (.toString f))))