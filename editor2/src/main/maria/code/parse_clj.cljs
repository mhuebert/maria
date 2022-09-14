(ns maria.code.parse-clj
  (:require ["lezer-clojure" :as lezer-clj]
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

(defn source-blocks
  "Returns a vector "
  [source]
  (->> source
       parse-clj
       program-nodes
       (eduction
        (map (fn [^js node] (subs source (.-from node) (.-to node))))
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

(defn blocks-as-markdown [blocks]
  (->> blocks
       (map (fn [{:keys [type source]}]
              (case type
                :code  (str "```clj"
                            \newline
                            source
                            \newline
                            "```")
                :prose source)))
       (str/join (str \newline \newline))))

(def sample-source
  "(ns my.app)

;; # Hello, world.
;;
;; This is a paragraph.

(+ 1 2)

;; Another paragraph.")

(comment
 (= (vec (source-blocks sample-source))
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
        source-blocks
        blocks-as-markdown)
    "```clj
(ns my.app)
```

# Hello, world.

This is a paragraph.

```clj
(+ 1 2)
```

Another paragraph."))
