(ns lark.tree.ext
  (:require [clojure.string :as string]
            [lark.tree.emit :as emit]
            [lark.tree.reader :as rd]
            [lark.tree.node :as n]))

(defn normalize-comment-line [s]
  (string/replace s #"^;+\s?" ""))

(comment

 ;; IN PROGRESS
 ;; thinking about a better way to group comment and code blocks
 ;; ...contemplating a transducer, or similar thing?

 (defn conj-while [[out in] xform]
   (loop [out out
          in in]
     (if-let [form (xform (peek in))]
       (recur (update-in out [(dec (count out)) :value] conj form)
              (subvec in 1))
       [out in])))

 (groups {:comment-block {:init {:tag :comment-block
                                 :value ""}
                          :pred comment-block-child?
                          :conj (fn [oldval node]
                                  (str oldval (-> (emit/string node)
                                                  (normalize-comment-line))))}
          :code-block {:init {:tag :base
                              :value []}
                       :pred (complement comment-block-child?)
                       :conj (fn [oldval node]
                               (conj oldval node))}} nodes))

(defn group-comment-blocks
  "Put consecutive top-level whitespace and comment nodes into :comment-blocks"
  [ast]
  (update ast :children
          (fn [nodes]
            (->> nodes
                 (reduce
                  (fn [out node]
                    (let [prev-tag (get (peek out) :tag)
                          current-tag (get node :tag)
                          in-comment-block? (= prev-tag :comment-block)
                          target (case current-tag
                                   (:newline :comment) :comment-block
                                   :space (if in-comment-block? :comment-block :code-block)
                                   :code-block)]
                      (case target
                        :comment-block
                        (if (= :comment-block prev-tag)
                          (update-in out [(dec (count out)) :value] str (-> (emit/string node)
                                                                            (normalize-comment-line)))
                          (conj out (assoc node
                                      :tag :comment-block
                                      :value (normalize-comment-line (emit/string node)))))
                        :code-block
                        (if (= :base prev-tag)
                          (update out (dec (count out)) (fn [base]
                                                          (-> base
                                                              (update :children conj node)
                                                              (update :source str (get node :source)))))
                          (conj out (-> (rd/EmptyNode :base)
                                        (assoc :children [node]
                                               :source (get node :source))))))))
                  [])))))

(defn shape [{:keys [tag children] :as node}]
  (if (= tag :base)
    (mapv shape children)
    (if (n/may-contain-children? node)
      (into [tag] (mapv shape children))
      tag)))