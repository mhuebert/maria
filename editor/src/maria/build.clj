(ns maria.build
  (:require [shadow.cljs.devtools.api :as api]))

;; to run from command line: npx shadow-cljs clj-run maria.build/release

(defn release
  []
  (api/release :live)
  (api/release :bootstrap)
  (api/release :trusted))
