(ns maria.persistence.remote
  (:require [re-view.core :as v :refer [defview]]
            [goog.net.XhrIo :as xhr]
            [clojure.string :as string]))

(defn parse-gist [gist-data]
  (if-let [{:keys [content filename]} (->> gist-data
                                                                 :files
                                                                 (vals)
                                                                 (filter #(= (:language %) "Clojure"))
                                                                 (first))]
    {:title           filename
     :persisted-value content}
    {:persisted-value ";; No Clojure files found in gist."}))

(defn get-gist [id cb]
  (xhr/send (str "https://api.github.com/gists/" id)
            (fn [e]
              (let [target (.-target e)]
                (if (.isSuccess target)
                  (cb {:value (js->clj (.getResponseJson target) :keywordize-keys true)})
                  (cb {:error (.getLastError target)}))))))

(defview load
  {:life/initial-state {:loading? true}
   :life/did-mount     (fn [{:keys [load-fn view/state]}]
                         (load-fn (partial reset! state)))}
  [{:keys [on-loading on-success on-error view/state]}]
  (let [{:keys [loading? value error]} @state]
    (cond loading? (on-loading)
          value (on-success value)
          error (on-error error))))