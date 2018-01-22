(ns maria.build
  (:require [shadow.cljs.devtools.api :as api]))

;; to run from command line: npx shadow-cljs clj-run maria.build/release

(defn release
  [build]
  (api/release :live)
  (api/release :trusted)
  (api/release :bootstrap))
