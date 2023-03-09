(ns maria.editor.command-bar
  (:require ["prosemirror-keymap" :refer [keydownHandler]]
            [applied-science.js-interop :as j]
            [yawn.hooks :as h]
            [yawn.view :as v]))


(defn use-global-keymap [bindings]
  (h/use-effect
   (fn []
     (let [on-keydown (let [handler (keydownHandler bindings)]
                        (fn [event]
                          (handler #js{} event)))]
       (.addEventListener js/window "keydown" on-keydown)
       #(.removeEventListener js/window "keydown" on-keydown)))))

(v/defview view [{:keys [binding]}]
  (use-global-keymap
   (j/obj binding
          (fn [& _]
            ;; TODO
            ;; implement command palette
            (prn :show-command-palette))))
  nil)