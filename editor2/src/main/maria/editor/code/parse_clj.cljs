(ns maria.editor.code.parse-clj
  (:require ["@nextjournal/lezer-clojure" :as lezer-clj]
            [maria.editor.prosemirror.schema :as schema]
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
       (map (fn [^js x] (j/!set x :source (subs source (.-from x) (.-to x)))))

       (partition 2 1 nil)

       (eduction
         ;; preserve newlines between prose blocks
         (mapcat (fn [[^js a ^js b]]
                   (cond-> [(.-source a)]
                           (and b
                                (comment? (.-source a))
                                (comment? (.-source b)))
                           (into
                             (take (->> (subs source (.-to a) (.-from b))
                                        (re-seq #"\n")
                                        count
                                        dec)
                                   (repeat ";"))))))
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

(def clojure->markdown (comp blocks->md clj->blocks))


;; - code blocks maintain a 1-line gap above & below
;; - consecutive prose blocks are separated by ;;-prefixed lines

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
     "
 ```clj
 (ns my.app)
 ```

 # Hello, world.

 This is a paragraph.

 ```clj
 (+ 1 2)
 ```

 Another paragraph."))

(comment
  (assert
    (= (vec (clj->blocks ";; hello\n\n;; world"))
       (vec (clj->blocks ";; hello\n;;\n;; world")))
    "prose blocks are joined even if separated by non-;; lines"))
