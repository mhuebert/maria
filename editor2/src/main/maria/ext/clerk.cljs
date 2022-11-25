(ns maria.ext.clerk
  (:require [nextjournal.clerk.sci-viewer :as clerk.sci-viewer]))

(def viewers [(fn [opts x] (clerk.sci-viewer/inspect x))])