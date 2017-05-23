(ns maria.repl-actions.loaders
  (:require [re-view.core :as v :refer [defview]]
            [re-view.hoc :as hoc]
            [clojure.string :as string]
            [maria.eval :as eval]
            [goog.net.XhrIo :as xhr]
            [maria.views.repl-ui :as repl-ui]))

(defview gist-loader-status
  [{:keys [status
           url
           error]}]
  [:.ph3
   {:class repl-ui/card-classes}
   [:.gray.pv2.overflow-auto.w-100.pv2 {:style {:font-size 13}} url]
   [:.b.mv2 status]
   (when error [:.bg-near-white.pa2.mv2 error])])

(defn normalize-gist-path [gist-path]
  (as-> gist-path path
        (string/replace path #"https://gist\.github.*?\.com/" "")
        (str "https://gist.githubusercontent.com/" path)
        (cond-> path
                (not (re-find #"\.(?:clj|cljs|cljc)$" path)) (str "/raw"))))

(defn load-gist
  ([gist-path] (load-gist gist-path {}))
  ([gist-path opts]
   (let [url (normalize-gist-path gist-path)
         status (atom {:url    url
                       :status "Loading gist..."})]
     (xhr/send url
               (fn [e]
                 (let [target (.-target e)]
                   (if (.isSuccess target)
                     (do
                       (eval/eval-str (.getResponseText target))
                       (swap! status assoc :status "Gist loaded."))
                     (swap! status assoc
                            :status [:.dark-red "Error:"]
                            :error (.getLastError target))))))
     (-> gist-loader-status
         (hoc/bind-atom status)))))