(ns maria.cloud.pages.landing
  (:require [clojure.string :as str]
            [maria.ui :as ui :refer [defview]]))

(defn link [title href]
  [:a (cond-> {:href href}
              (str/starts-with? title "http")
              (assoc :target "_blank")) title])

(defview page []
  [:div.font-serif.text-center.px-3.mx-auto.mt-3
   {:style {:max-width 600}}
   [:div.mb-3.pt-5 {:class "text-[3rem]"} "Welcome to Maria,"]
   [:div.text-2xl.my-3 "a coding environment for beginners."]

   [:div.flex.items-center.text-lg.my-4.rounded-md.bg-zinc-300.p-3
    [:div.flex-auto]
    "Your journey begins here: "
    [:a.rounded-md.bg-white.shadow-lg.py-3.px-3.ml-3.font-sans.text-black.no-underline.text-base.font-bold.cursor-pointer.hover:underline.hover:shadow-2xl
     {:href "/intro"} "Learn Clojure with Shapes"]
    [:div.flex-grow]]

   [:div.text-center.italic.text-lg.my-3 "Further reading:"]

   [:ul.text-base.text-left.leading-normal.list-disc.pl-6
    [:li "An " (link "Example Gallery" "/gallery?eval=true") " of user creations."]
    [:li "The " (link "Editor Quickstart" "/quickstart") " explains how to use Maria."]

    [:li "Understand the " (link "Pedagogy" "https://github.com/mhuebert/maria/wiki/Curriculum") " behind Maria's curriculum."]
    [:li "Discover " (link "Sources of Inspiration" "https://github.com/mhuebert/maria/wiki/Background-reading") " for the project."]]])