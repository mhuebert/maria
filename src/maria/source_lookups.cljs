(ns maria.source-lookups
  (:require [cljs.tools.reader :as r]
            [cljs.tools.reader.reader-types :as rt]
            [clojure.string :as string]
            [maria.eval :as e]
            [cljs-live.eval :as cljs-eval]
            [cljs-live.compiler :as c]
            [goog.net.XhrIo :as xhr])
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

(defn demunge-symbol-str [s]
  (when s
    (let [parts (string/split (demunge s) "/")]
      (if (> (count parts) 1)
        (str (string/join "." (drop-last parts)) "/" (last parts))
        (first parts)))))

(defn fn-var
  "Look up the var for a function using its `name` property"
  [f]
  (when-let [munged-sym (aget f "name")]
    (cljs-eval/resolve-var (symbol (demunge-symbol-str munged-sym)))))

(def source-path "/js/cljs_bundles/sources")

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
      (if-let [source (get @c/cljs-cache file)]
        (cb {:value (source-of-form-at-position source the-var)})
        (xhr/send (str source-path "/" file)
                  (fn [e]
                    (let [target (.-target e)]
                      (if (.isSuccess target)
                        (let [source (.getResponseText target)]
                          ;; strip source to line/col from meta
                          (cb {:value (source-of-form-at-position source meta)}))
                        (cb {:error (.getLastError target)}))))
                  "GET")))
    (cb {:error "No file associated with this name."})))

(defn fn-source-sync [f]
  (or (js-source->clj-source (.toString f))
      (.toString f)))

(defn ensure-str [s]
  (when (and (string? s) (not (identical? s "")))
    s))

(defn fn-name
  [f]
  (or (some-> (aget f "name") (demunge-symbol-str))
      (:name (fn-var f))))