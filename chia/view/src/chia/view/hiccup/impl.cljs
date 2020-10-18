(ns chia.view.hiccup.impl
  (:require [clojure.string :as str]
            ["react" :as react]
            [applied-science.js-interop :as j]
            [chia.util.perf :as perf]
            [chia.util :as u]
            [goog.object :as gobj]))

(defn parse-key
  "Parses a hiccup key like :div#id.class1.class2 to return the tag name, id, and classes.
   If tag-name is ommitted, defaults to 'div'. Class names are padded with spaces."
  [x]
  (let [match (.exec #"([^#.]+)?(?:#([^.]+))?(?:\.(.*))?" x)
        classes (aget match 3)]
    (j/obj .-tag (or (aget match 1) "div")
           .-id (aget match 2)
           .-classes (if (undefined? classes)
                       classes
                       (str/replace (aget match 3) "." " ")))))

(def parse-key-memo (u/memoize-str parse-key))

(defn name->react-attr
  "Return js (react) key for keyword/string.

  - Namespaced keywords are ignored
  - area- and data- prefixed keys are not camelCased
  - other keywords are camelCased"
  [s]
  (cond (identical? s "for") "htmlFor"
        (identical? s "class") "className"
        (or (str/starts-with? s "data-")
            (str/starts-with? s "aria-")) s
        :else (u/camel-case s)))

(def name->react-attr-memo (u/memoize-str name->react-attr))

(defn map->js
  "Return javascript object with camelCase keys (shallow)"
  [style]
  (->> style
       (reduce-kv (fn [obj k v]
                    (j/assoc! obj (u/camel-case (name k)) v)) #js{})))

(def ^:dynamic *wrap-props* nil)

(defn- map-prop? [js-key]
  (or (identical? js-key "style")
      (identical? js-key "dangerouslySetInnerHTML")))

(defn- defined? [x] (not (undefined? x)))

(defn class-str [s]
  (cond (vector? s) (str/replace (str/join " " (mapv class-str s))
                                 "." " ")
        (keyword? s) (name s)
        :else s))

(defn props->js
  "Returns a React-conformant javascript object. An alternative to clj->js,
  allowing for key renaming without an extra loop through every prop map."
  ([props] (props->js #js{} props))
  ([parsed-key props]
   (->> (cond-> (if (some? props) props {})
                (some? *wrap-props*) (*wrap-props* (.-tag parsed-key))

                (defined? (.-id parsed-key))
                (assoc :id (.-id parsed-key))



                (or (defined? (.-classes parsed-key)) (contains? props :class))
                (update :class (fn [x]
                                 (if (some? x)
                                   (str (.-classes parsed-key) " " (class-str x))
                                   (.-classes parsed-key)))))
        (reduce-kv
         (fn [js-props k v]
           (if-some [js-key (when-not (qualified-keyword? k)
                              (name->react-attr-memo (name k)))]
             (j/unchecked-set js-props js-key
                              (cond-> v (map-prop? js-key) (map->js)))
             js-props)) (js-obj)))))
