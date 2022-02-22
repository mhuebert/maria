(ns chia.view.util
  (:require [applied-science.js-interop :as j]))

(def browser? #?(:cljs (exists? js/window)
                 :clj  false))

#?(:cljs
   (defn find-or-append-element
     ([id] (find-or-append-element id :div))
     ([id tag]
      (or (.getElementById js/document id)
          (let [element (-> (.createElement js/document (name tag))
                            (j/assoc! :id (name id)))]
            (.. js/document -body (appendChild element)))))))