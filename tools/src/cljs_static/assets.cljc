(ns cljs-static.assets
  (:require [clojure.string :as str]
            [clojure.java.io :as io])
  #?(:clj (:import (java.security MessageDigest))))

(def join-paths #?(:clj  io/file
                   :cljs js-path/join))

;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Asset handling

;; Dynamic vars are used for asset-path options

(def ^:dynamic *public-path* "public")

(def ^:dynamic *asset-host*
  "Host where assets are to be accessed by client"
  nil)

(defn strip-slash [path]
  (cond-> path
          (str/starts-with? path "/") (subs 1)))

(def md5
  "Returns md5 hash for string"
  ;; https://gist.github.com/jizhang/4325757#gistcomment-2196746
  #?(:clj  (fn [s] (let [algorithm (MessageDigest/getInstance "MD5")
                         raw (.digest algorithm (.getBytes s))]
                     (format "%032x" (BigInteger. 1 raw))))
     :cljs md5-fn))

(defn try-slurp [file]
  (try #?(:clj  (slurp file)
          :cljs (some-> (fs/readFileSync file) (str)))
       (catch #?(:clj  Exception
                 :cljs js/Error) e nil)))

(def make-parents #?(:clj  io/make-parents
                     :cljs (fn [s]
                             (mkdirp (str/replace s #"/[^/]+$" "")))))

(defn asset-file [path]
  (assert *public-path* "*public-path* must be set")
  (join-paths *public-path* (strip-slash path)))

(defn read-asset
  "Returns the contents for an asset"
  [path]
  (-> (asset-file path)
      (try-slurp)))

(defn path*
  "Asset-path function, for use in generating HTML"
  ([path] (path* {} path))
  ([{:keys [invalidation]
     :or   {invalidation :md5}} path]
   (if-not (str/starts-with? path "/")
     path
     (let [prefix *asset-host*
           postfix (case invalidation
                     :md5 (some->> (read-asset path)
                                   (md5)
                                   (str "?v="))
                     :always (str "?v=" #?(:cljs (.now js/Date)
                                           :clj  (System/currentTimeMillis))))]
       (str prefix path postfix)))))

(defmacro path [& args]
  (apply path* args))

(defn write-asset!
  "Write `content` string to an asset file"
  [path content]
  (doto (asset-file path)
    (make-parents)
    (spit content))
  (println (str " + " path)))
