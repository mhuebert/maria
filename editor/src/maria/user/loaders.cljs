(ns maria.user.loaders
  (:require [chia.view :as v]
            [maria.views.hoc :as hoc]
            [maria.views.icons :as icons]

            [chia.view.hiccup :as hiccup]

            [maria.eval :as eval]
            [maria.views.cards :as repl-ui]
            [maria.editors.code :as code]

            [goog.net.jsloader :as jsl]
            [goog.html.legacyconversions :refer [trustedResourceUrlFromString]]

            [clojure.set :as set]
            [maria.persistence.github :as github]))

(v/defclass gist-loader-status
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
    (when source (if @state icons/ArrowPointingUp icons/ArrowPointingDown))]

   (when (and @state source)
     [:.overflow-scroll.ph3.nl3.nr3.bt.bb.b--darken.pv2
      {:style {:max-height 200}} (code/viewer source)])

   [:.gray.pv2.overflow-auto.w-100.pv2 {:style {:font-size 13}} url]

   (when error [:.bg-near-white.pa2.mv2 error])])


(defn load-gist
  "Loads gist content, evaluating as ClojureScript code."
  [id]
  (let [status (atom {:url    id
                      :status "Loading gist..."})]
    (github/get-gist id (fn [{:keys [value error]}]
                          (if error
                            (swap! status assoc
                                   :status [:.dark-red "Error:"]
                                   :error error)
                            (let [project (:persisted value)
                                  [_ {source :content}] (first (:files project))
                                  c-env @eval/c-env
                                  {:keys [value
                                          error]} (eval/eval-str* source)]
                              ;; TOOD
                              ;; handle error
                              (reset! eval/c-env c-env)
                              (swap! status assoc
                                     :status "Gist loaded."
                                     :source source)))))
    (-> gist-loader-status
        (hoc/bind-atom status))))

(defn load-js
  "Load javascript file from url. Return message indicating added global variables."
  [url]
  (let [start-globals (set (js->clj (.keys js/Object js/window)))
        url (trustedResourceUrlFromString url)]
    (-> (jsl/safeLoad url)
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

