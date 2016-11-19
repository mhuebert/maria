(ns maria.tree.paredit
  (:require [maria.tree.core :as tree]
            [maria.codemirror :as cm]
            [re-view.subscriptions :as subs]
            [fast-zip.core :as z]
            [re-db.d :as d]
            [cljs.pprint :refer [pprint]]
            [re-view.core :as v :refer-macros [defcomponent]]))


(defn pretty-str [x]
  (with-out-str (pprint x)))

(defcomponent track-cursor
  :render
  (fn [this]
    (let [{:keys [ast zipper cursor-state] :as state} (some-> this (v/react-ref "editor") v/state)
          {:keys [pos node]} cursor-state
          node (some-> zipper (tree/node-at pos) z/node)]
      [:div.ma4.ph3
       [:.f4.mt2 "Track Cursor"]
       [:.black-70.i.mt2 "Helper view to inspect cursor & node positions"]
       [:.h3.bg-solarized-light.mt2.pa3
        (cm/editor {:ref                  "editor"
                    :local-storage        ["track-cursor" "(conj [1 2 3] 4)"]
                    :event/cursorActivity #(v/force-update this)})]
       [:.mt2 (cm/editor {:ref   "viewer"
                          :value (str "cursor: " pos "\n"
                                      "node tag: " (:tag node) \newline
                                      "node pos:" (pretty-str (select-keys node [:row :col :end-row :end-col]))
                                      "\n" (tree/string node) "\n"
                                      )})]])))


(defcomponent examples
  [:.serif
   (track-cursor)])


