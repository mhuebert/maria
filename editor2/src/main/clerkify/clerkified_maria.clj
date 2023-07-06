^{:nextjournal.clerk/visibility {:code :hide, :result :hide}}
(ns clerkified-maria
  "A clerkified Maria doc"
  (:require
    ;; It's often bad form to bring in all vars from multiple lib
    ;; namespaces. Here we do it so that code from Maria works as-is. See
    ;; https://github.com/bbatsov/clojure-style-guide#prefer-require-over-use
    [applied-science.shapes :refer :all]
    [applied-science.clerk-helpers :refer :all]
    [nextjournal.clerk :as clerk]))

^{::clerk/visibility {:code :hide, :result :hide}}
(clerk/add-viewers! [{:pred shape?
                      ;; Make Clerk "present" Shapes as they are shown with Maria:
                      :transform-fn (clerk/update-val (comp clerk/html
                                                            to-hiccup))}])