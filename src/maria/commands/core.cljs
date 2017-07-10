(ns maria.commands.core
  (:require [clojure.set :as set])
  (:require-macros [maria.commands.core :refer [defcommand]]))

(def commands (atom {}))
(def mappings (atom {}))

(defn get-commands [keys-pressed]
  {:commands (->> (get-in @mappings [keys-pressed :commands])
                  (map @commands))
   :hints    (->> (keep (fn [[keyset results]]
                          (when (not= keyset keys-pressed)
                            (when-let [matches (seq (set/intersection keyset keys-pressed))]
                              {:keyset      keyset
                               :results     results
                               :num-matches (count matches)}))) @mappings)
                  (sort-by :num-matches))})

;; icons for shift, command, option

;; ctrl/command
;; alt/option
;; shift

;; navigator.platform
;; isMac == navigator.platform.indexOf('Mac') > -1
;; https://stackoverflow.com/questions/10527983/best-way-to-detect-mac-os-x-or-windows-computers-with-javascript-or-jquery
;; Mac*  MacIntel
;; iOs - iP* iPad, iPhone, iPod