(ns maria.editor.command-bar
  (:require ["prosemirror-keymap" :refer [keydownHandler]]
            [applied-science.js-interop :as j]
            [maria.ui :as ui]
            [re-db.reactive :as r]
            [yawn.view :as v]))

(r/redef !state (r/atom {:visible? false}))

(defn toggle! []
  (swap! !state update :visible? not))

(v/defview view []
  (if (ui/use-> !state :visible?)
    "command-bar"
    "no command-bar"))