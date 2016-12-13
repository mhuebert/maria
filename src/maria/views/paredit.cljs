(ns maria.views.paredit
  (:require [magic-tree.core :as tree]
            [maria.editor :as cm]
            [fast-zip.core :as z]
            [re-db.d :as d]
            [cljs.pprint :refer [pprint]]
            [re-view.core :as v :refer [defview]]))

(defn pretty-str [x]
  (with-out-str (pprint x)))

(defview track-cursor
  (fn [this]
    (let [{:keys [ast zipper cursor] :as state} (some-> this (v/ref "editor") :state)
          {:keys [pos node]} cursor
          node (some-> zipper (tree/node-at pos) z/node)]
      [:div.ma4.ph3
       [:.f4.mt2 "Track Cursor"]
       [:.black-70.i.mt2 "Helper view to inspect cursor & node positions"]
       [:.bg-solarized-light.mt2.pa3
        (cm/editor {:ref                  "editor"
                    :local-storage        ["track-cursor" "(conj [1 2 3] 4)"]
                    :event/cursorActivity #(v/force-update this)})]
       [:.mt2 (cm/editor {:ref   "viewer"
                          :value (str "cursor: " pos "\n"
                                      "node tag: " (:tag node) \newline
                                      "node pos:" (pretty-str (select-keys node [:line :column :end-line :end-column]))
                                      "\n" (tree/string node) "\n")})]])))

(defview examples
  [:.serif
   (track-cursor)])



