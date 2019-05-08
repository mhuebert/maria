(ns maria.share
  (:require [chia.view :as v]
            [lark.tree.emit :as emit]
            [maria.commands.doc :as doc]
            [lark.tree.node :as node]
            [lark.tree.core :as tree]
            [maria.blocks.blocks :as Block]))

(defn get-share-url [doc block block-list]
  (when-not (doc/unsaved-changes? doc)
    (let [the-node (cond-> (:node block)
                           (= :base (:tag (:node block)))
                           (-> :children first))
          the-string (emit/string the-node)
          ast (->> (Block/emit-list (.getBlocks block-list))
                   (tree/ast)
                   (:children)
                   (remove #(or (node/whitespace? %)
                                (node/comment? %))))
          index (->> ast
                     (take-while (fn [{:keys [tag] :as node}]
                                   (or (not= tag (:tag the-node))
                                       (not= the-string (emit/string node)))))
                     (count))
          {:keys [id version]} (get-in doc [:project :persisted])]
      (str "http://share.maria.cloud/gist/" id "/" version "/" index))))

(v/defclass ShareLink
                 [{:keys [view/state doc block block-list]}]
                 (let [{:keys [hovered]} @state]
                   (when (= :gist (get-in doc [:project :persisted :persistence/provider]))
                     (let [unsaved-changes (and hovered (doc/unsaved-changes? doc))
                           url (when (and hovered (not unsaved-changes))
                                 (get-share-url doc block block-list))]
                       [(if url :a :div)
                        {:class (str "share absolute top-0 right-0 bg-darken pa1 hover-bg-darken-more f7 z-5 black no-underline "
                                     (if unsaved-changes "o-50" "pointer"))
                         :href url
                         :target (when url "_blank")
                         :on-mouse-enter #(swap! state assoc :hovered true)
                         :on-mouse-leave #(swap! state dissoc :hovered)}
                        (if unsaved-changes
                          "please save first!"
                          "share")]))))