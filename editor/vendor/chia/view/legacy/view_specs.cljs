(ns chia.view.legacy.view-specs
  (:require [clojure.string :as string]
            ["react" :as react]
            [clojure.spec.alpha :as s]))

(defonce spec-meta
  ;; clojure spec doesn't support metadata, but we want our docstrings
  (atom {}))

(def ReactElement? react/isValidElement)

(def Hiccup? #(and (vector? %)
                   (keyword? (first %))))

(def SVG? #(and (Hiccup? %)
                (string/starts-with? (name (first %)) "svg")))

(defn any-pred
  "Evaluate fns sequentially, stopping if any return true."
  [& fns]
  (fn [this]
    (loop [fns fns]
      (if (empty? fns)
        false
        (or ((first fns) this)
            (recur (rest fns)))))))

(def Element? (any-pred
                nil?
                ReactElement?
                Hiccup?
                string?))

(s/def :view/element Element?)
(s/def :view/svg SVG?)
(s/def :view/hiccup Hiccup?)
(s/def :view/react-element ReactElement?)

(s/def :dom/on-click fn?)
(s/def :dom/on-blur fn?)
(s/def :dom/on-focus fn?)