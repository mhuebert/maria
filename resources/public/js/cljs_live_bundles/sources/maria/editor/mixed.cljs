(ns maria.editor.mixed
  (:require [re-view.core :as v :refer [defview]]
            [re-view-prosemirror.markdown :as prose]
            [maria.editor.codemirror :as codemirror]
            [magic-tree.core :as tree]
            [clojure.core.match :refer-macros [match]]
            [fast-zip.core :as z]
            [clojure.string :as string]
            [maria.views.repl-utils :as repl-ui]))

(defview comment-block [_ text]
  (prose/Editor {:value text}))

(defn kind [node]
  (case (:tag node)
    :newline :newline
    (:comma :space) :space
    :else :code))

(def loc-tag (comp :tag z/node))

(defn comment-block-loc [loc]
  (when loc
    (let [{:keys [tag]} (z/node loc)]
      (or (keyword-identical? :newline tag)
          (keyword-identical? :comment tag)))))

(defn comment-loc? [loc]
  (when loc
    (keyword-identical? :comment (get (z/node loc) :tag))))

(defn right [loc]
  (when loc (z/right loc)))

(defn locs->string [locs]
  (->> (mapv (comp tree/string z/node) locs)
       (string/join)))

(defn comment-locs->string [locs]
  (->> locs
       (keep #(let [{:keys [tag value] :as node} (z/node %)]
                (when (keyword-identical? tag :comment)
                  (.replace value #"^;+" ""))))
       (interpose "\n")
       (apply str)))

(defn group-children [loc]
  (loop [loc loc
         out []]
    (if-not loc
      out
      (let [tag (loc-tag loc)]
        (if (or (= :comment tag)
                (and (= :newline tag) (= :comment (some-> (z/right loc) loc-tag))))
          (let [comment-locs (take-while comment-block-loc (iterate right loc))]
            (recur (right (last comment-locs))
                   (conj out [:comment comment-locs])))
          (let [code-locs (take-while (complement comment-loc?) (iterate right loc))]
            (assert (= :newline (loc-tag (last code-locs))))
            (recur (last code-locs)
                   (conj out [:code (butlast code-locs)]))))))))

(defview editor
  {:view/initial-state      (fn [{:keys [value]}]
                              {:zipper (tree/string-zip value)})
   :view/will-receive-props (fn [{:keys [value view/prev-props view/state]}]
                              (when-not (= value (:value prev-props))
                                (swap! state assoc :zipper (tree/string-zip value))))}
  [{:keys [value
           auto-focus
           on-update
           read-only?
           on-mount
           cm-opts
           view/state]}]
  (let [{:keys [zipper]} @state]

    [:flex.flex-column.overflow-y-auto
     [:.w-50
      (->> (group-children (z/down zipper))
           (map-indexed
             (fn [i [kind locs]]
               (case kind :comment [:.serif.f4.pa3
                                    (prose/Editor {:key   i
                                                   :value (comment-locs->string locs)})]
                          :code [:.bg-white
                                 (codemirror/editor {:key   i
                                                     :class "ph4 pv3"
                                                     :value (locs->string locs)})]))))]]))