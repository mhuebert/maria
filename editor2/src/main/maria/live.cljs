(ns maria.live
  (:require ["prosemirror-keymap" :refer [keydownHandler]]
            [applied-science.js-interop :as j]
            [clojure.string :as str]
            [maria.code-blocks.eldoc :as eldoc]
            [maria.examples :as ex]
            [maria.prosemirror.editor :as prose]
            [maria.scratch]
            [yawn.hooks :as h]
            [yawn.root :as root]
            [yawn.view :as v]))

(defn link [title href]
  [:a (cond-> {:href href}
              (str/starts-with? title "http")
              (assoc :target "_blank")) title])

(def requires
  (str '(ns maria.examples
          (:require [shapes.core :refer :all]
                    [cells.api :refer :all]))))

(defn use-global-keymap [bindings]
  (h/use-effect
   (fn []
     (let [on-keydown (let [handler (keydownHandler bindings)]
                            (fn [event]
                              (handler #js{} event)))]
       (.addEventListener js/window "keydown" on-keydown)
       #(.removeEventListener js/window "keydown" on-keydown)))))

(v/defview landing []
  (use-global-keymap (j/js
                       {:mod-k
                        (fn [& _]
                          ;; TODO
                          ;; implement command palette
                          (prn :show-command-palette))}))
  [:div
   [prose/editor {:source ex/examples}]
   [eldoc/view]])

(defn ^:export init []
  (root/create :maria-live (v/x [landing])))