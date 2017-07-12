(ns maria.show-comments
  (:require [re-view.core :as v :refer [defview]]
            [clojure.string :as string]))

(defn ranges-by-lines [s]
  (let [lines (vec (string/split-lines s))
        line-count (count lines)]
    (loop [i 0
           was-in-comment? false
           comment-start false
           comment-ranges []]
      (if (= i line-count)
        (cond-> comment-ranges
                was-in-comment? (conj [comment-start (dec i)]))
        (let [in-comment? (= \; (subs (lines i) 0 1))]
          (recur (inc i)
                 in-comment?
                 (when in-comment? (if was-in-comment? comment-start i))
                 (cond-> comment-ranges
                         (and was-in-comment? (not in-comment?))
                         (conj [comment-start (dec i)]))))))))

(defn wysiwyg-comments! [cm]
  (let [viewport (.getViewport cm)
        line-ranges (ranges-by-lines (.getValue cm))
        ]
    (.log js/console viewport)

    )
  ;; 1. find visible comment block ranges.
  ;;
  ;; comment blocks that extend to the top or bottom of the scrolled region
  ;; should be followed to find where they start/end.
  ;;
  ;; 2. compare current blocks to previous blocks. add/remove to sync.
  ;;
  ;;
  ;;
  )

(defn handle-ast-update [cm next-ast next-zip]
  (wysiwyg-comments! cm)
  #_(let [comment-blocks (time (ranges-by-lines (.getValue cm)))]
      (println (str "There are " (count comment-blocks) " comment blocks."))))