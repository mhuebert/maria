(ns maria.repl-actions.loaders
  (:require [re-view.core :as v :refer [defview]]
            [re-view.hoc :as hoc]
            [clojure.string :as string]
            [maria.eval :as eval]
            [goog.net.XhrIo :as xhr]
            [maria.views.repl-ui :as repl-ui]
            [re-view-material.icons :as icons]))

(defview gist-loader-status
  [{:keys [status
           url
           error
           source
           view/state]}]
  [:.ph3
   {:class repl-ui/card-classes}

   [:.b.mv2.flex.items-center
    (when source {:class    "pointer"
                  :on-click #(swap! state not)})
    status
    [:.flex-auto]
    (when source (if @state icons/ArrowDropUp icons/ArrowDropDown))]

   (when (and @state source)
     [:.code.overflow-scroll.ph3.nl3.nr3.bt.bb.b--darken.pv2
      {:style {:max-height 200}} source])

   [:.gray.pv2.overflow-auto.w-100.pv2 {:style {:font-size 13}} url]

   (when error [:.bg-near-white.pa2.mv2 error])])

(defn gist-source [gist-json]
  (->> (js->clj (aget gist-json "files"))
       (vals)
       (keep (fn [{:strs [content language]}]
               (when (= language "Clojure")
                 content)))
       (string/join "\n")))

(defn get-gist [id cb]
  (xhr/send (str "https://api.github.com/gists/" id)
            (fn [e]
              (let [target (.-target e)]
                (if (.isSuccess target)
                  (cb {:value (.getResponseJson target)})
                  (cb {:error (.getLastError target)}))))))

(defn load-gist [id]
  (let [status (atom {:url    id
                      :status "Loading gist..."})]
    (get-gist id (fn [{:keys [value error]}]
                   (if error
                     (swap! status assoc
                            :status [:.dark-red "Error:"]
                            :error error)
                     (let [source (gist-source value)]
                       (eval/eval-str source)
                       (swap! status assoc
                              :status "Gist loaded."
                              :source source)))))
    (-> gist-loader-status
        (hoc/bind-atom status))))
