(ns maria.editor.code.parse-clj
  (:require ["@nextjournal/lezer-clojure" :as lezer-clj]
            [applied-science.js-interop :as j]
            [clojure.string :as str]))

;; This namespace splits Clojure files into prose and code blocks using nextjournal/clojure-mode lezer parser.

(defn parse-clj [source] (.parse lezer-clj/parser source))

(defn program-nodes
  "Returns a vector of Tree/TreeBuffer nodes"
  [^js tree]
  (let [^js cursor (.cursor tree)
        !found (volatile! [])]
    (when (.firstChild cursor)
      (vswap! !found conj (.-node cursor))
      (while (.nextSibling cursor)
        (vswap! !found conj (.-node cursor))))
    @!found))

(defn comment? [s] (str/starts-with? s ";"))

(defn clj->blocks
  "Returns a vector "
  [source]
  (->> source
       parse-clj
       program-nodes

       ;; lezer does not represent the space between nodes, eg. empty lines,
       ;; so add a ::line-gap marker in these cases
       (partition 2 1 nil)
       (mapcat (fn [[^js a ^js b]]
                 (if (and b (pos? (- (.-from b) (.-to a) 1)))
                   [a ::line-gap]
                   [a])))

       (eduction
        (map (fn [node]
               (if (= node ::line-gap)
                 ";"
                 (subs source (j/get node :from) (j/get node :to)))))
        (partition-by comment?)
        (mapcat
         (fn [sources]
           (if (comment? (first sources))
             [{:type :prose
               :source (->> sources
                            (map #(str/replace % #"^;+\s*" ""))
                            (str/join \newline))}]
             (map (fn [source]
                    {:type :code
                     :source source}) sources)))))))

(defn blocks->md [blocks]
  (->> blocks
       (map (fn [{:keys [type source]}]
              (case type
                :code (str \newline
                           "```clj"
                           \newline
                           source
                           \newline
                           "```"
                           \newline)
                :prose source)))
       (str/join \newline)))

(def clj->md (comp blocks->md clj->blocks))

(def sample-source
  "(ns my.app)

;; # Hello, world.
;;
;; This is a paragraph.

(+ 1 2)

;; Another paragraph.")

(comment
 (= (vec (clj->blocks sample-source))
    [{:type :code
      :source "(ns my.app)"}
     {:type :prose
      :source "# Hello, world.\n\nThis is a paragraph."}
     {:type :code
      :source "(+ 1 2)"}
     {:type :prose
      :source "Another paragraph."}]))

(comment
 (= (-> sample-source
        clj->blocks
        blocks->md)
    "```clj
(ns my.app)
```

# Hello, world.

This is a paragraph.

```clj
(+ 1 2)
```

Another paragraph."))
