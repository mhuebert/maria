(ns maria.user.loaders
  (:require [re-view.core :as v :refer [defview]]
            [re-view.hoc :as hoc]
            [re-view-material.icons :as icons]

            [re-view-hiccup.core :as hiccup]

            [maria.eval :as eval]
            [maria.views.repl-utils :as repl-ui]
            [maria.editor :as editor]

            [goog.net.XhrIo :as xhr]
            [goog.net.jsloader :as jsl]

            [clojure.string :as string]
            [clojure.set :as set]))

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
     [:.overflow-scroll.ph3.nl3.nr3.bt.bb.b--darken.pv2
      {:style {:max-height 200}} (editor/viewer source)])

   [:.gray.pv2.overflow-auto.w-100.pv2 {:style {:font-size 13}} url]

   (when error [:.bg-near-white.pa2.mv2 error])])

(defn gist-source [gist-data]
  (prn :gist gist-data)
  (if-let [clojure-files (->> gist-data
                              :files
                              (vals)
                              (keep (fn [{:keys [content language]}]
                                      (when (= language "Clojure")
                                        content)))
                              (seq))]
    (string/join "\n" clojure-files)
    ";; No Clojure files found in gist."))

(defn get-gist [id cb]
  (xhr/send (str "https://api.github.com/gists/" id)
            (fn [e]
              (let [target (.-target e)]
                (if (.isSuccess target)
                  (cb {:value (js->clj (.getResponseJson target) :keywordize-keys true)})
                  (cb {:error (.getLastError target)}))))))

(defn load-gist
  "Loads gist content, evaluating as ClojureScript code."
  ;; TODO - distinguish between .clj(s|c) files and .md files, handle appropriately.
  [id]
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

(defn load-js
  "Load javascript file from url. Return message indicating added global variables."
  [url]
  (let [start-globals (set (js->clj (.keys js/Object js/window)))]
    (-> (jsl/load url)
        (.addCallback (fn []
                        (when-let [new-globals (seq (set/difference (set (js->clj (.keys js/Object js/window)))
                                                                    start-globals))]
                          (hiccup/element [:div "Added: " (->> new-globals
                                                               (map #(do (list "window." [:span.b %])))
                                                               (interpose ", "))])))))))

(defn load-npm
  "Load package from NPM, packaged with browserify (see https://wzrd.in/).
   Usage: `(load-npm \"my-package@0.2.0\")` or `(load-npm \"my-package@latest\")`"
  [package]
  (load-js (str "https://wzrd.in/standalone/" package)))