(ns maria.cloud.pages.landing
  (:require [clojure.string :as str]
            [maria.ui :as ui :refer [defview]]))

(defn link [title href]
  [:a (cond-> {:href href}
              (str/starts-with? title "http")
              (assoc :target "_blank")) title])

(defview page []
  [:div.font-serif.text-center.px-3.mx-auto.mt-3
   {:style {:max-width 500}}
   [:div.pt-5 {:class "text-[3rem]"} "Welcome to Maria,"]
   [:div.text-2xl "a coding environment for beginners."]

   [:div.flex.flex-row.items-center.bg-zinc-200.rounded.my-6.p-6.gap-4
    "Start Here: "
    [:a.rounded-md.p-3.block.flex-grow
     {:class ["text-base" ui/c:button-light]
      :href "/intro"} "Learn Clojure with Shapes"]]

   [:div.text-left.pl-6
    [:div.italic.text-lg.text-left.mb-2.mt-4 "Further reading:"]

    [:ul.text-base.text-left.leading-normal.list-disc.pl-5
     [:li "An " (link "Example Gallery" "/gallery?eval=true") " of user creations."]
     [:li "The " (link "Editor Quickstart" "/quickstart") " explains how to use Maria."]

     [:li "Understand the " (link "Pedagogy" "https://github.com/mhuebert/maria/wiki/Curriculum") " behind Maria's curriculum."]
     [:li "Discover " (link "Sources of Inspiration" "https://github.com/mhuebert/maria/wiki/Background-reading") " for the project."]]]])