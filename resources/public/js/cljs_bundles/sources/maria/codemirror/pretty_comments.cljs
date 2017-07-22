(ns maria.codemirror.pretty-comments
  (:require [re-view.core :as v :refer [defview]]
            [clojure.string :as string]

            [goog.dom :as gdom]))

(def enabled false)

(defn ranges-by-lines [s]
  (let [lines (vec (string/split-lines s))
        line-count (count lines)]
    (loop [i 0
           was-in-comment? false
           comment-start-line false
           comment-ranges []]
      (if (= i line-count)
        (cond-> comment-ranges
                was-in-comment? (conj [comment-start-line (dec i) (subvec lines comment-start-line i)]))
        (let [current-line-is-comment (string/starts-with? (lines i) ";")
              start-comment? (and (not was-in-comment?) current-line-is-comment #_(string/starts-with? (lines i) ";;;;"))
              continue-comment? (and was-in-comment? current-line-is-comment)
              next-comment-start-line (cond continue-comment? comment-start-line
                                            start-comment? i
                                            :else false)
              comment-ended? (and was-in-comment? (not (number? next-comment-start-line)))]
          (recur (inc i)
                 (or continue-comment? start-comment?)
                 next-comment-start-line
                 (cond-> comment-ranges
                         comment-ended?
                         (conj [comment-start-line (dec i) (subvec lines comment-start-line i)]))))))))

(defn clear-comment-blocks! [cm]
  (doseq [handle (get-in cm [:maria/comment-blocks :handles])]
    (.clear handle))
  (swap! cm update :maria/comment-blocks dissoc :handles))

(defn comment-node [line-height lines]
  (gdom/createDom "div" #js {:className "bg-blue pink pre-wrap pa2 mv2"
                             :style     (str "padding-right: 15px; width: 100%; ")}
                  (string/join "\n" lines)))

(defn comment-blocks! [cm line-ranges]
  (let [line-height (.defaultTextHeight cm)]
    (swap! cm assoc-in [:maria/comment-blocks :handles]
           (->> (mapv (fn [[l1 l2 lines]]
                        [(.addLineWidget cm (dec l1) (comment-node line-height lines) #js {:showIfHidden true})
                         (.markText cm
                                    #js {:line (dec l1)}
                                    #js {:line l2}
                                    #js {:inclusiveLeft  (= 0 l1)
                                         :inclusiveRight true
                                         :collapsed      true})]
                        #_(.markText cm
                                     #js {:line l1 :ch 0}
                                     #js {:line (inc l2) :ch 0}
                                     #js {:inclusiveLeft  true
                                          :inclusiveRight false
                                          :replacedWith   (comment-node line-height lines)})) line-ranges)
                (reduce into [])))))

(defn wysiwyg-comments! [cm]
  (when enabled
    (let [viewport (.getViewport cm)
          comment-ranges (ranges-by-lines (.getValue cm))]
      (clear-comment-blocks! cm)
      (time (comment-blocks! cm comment-ranges))))
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
  (wysiwyg-comments! cm))