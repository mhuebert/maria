(ns ^:figwheel-always maria.tests-runner
  (:require [lark.structure.bracket-tests]
            [lark.structure.edit-tests]
            [goog.events :as events]
            [cljs.test :refer-macros [run-all-tests]]))

(enable-console-print!)

(defn run-tests []
  (run-all-tests #".*tests.*"))

(defonce _
         (do (run-tests)
             (events/listen js/document.body "figwheel.before-js-reload" run-tests)))