(defproject maria/editor "0.1.0-SNAPSHOT"
  :description "A ClojureScript editor for beginners"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies ~(into '[[thheller/shadow-cljs "2.0.137"]
                         [org.clojure/clojure "1.9.0-alpha17"]
                         [org.clojure/clojurescript "1.9.946"]]
                   (->
                     (slurp "shadow-cljs.edn")
                     (read-string)
                     (:dependencies)))

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]

  :source-paths ["src"
                 "checkouts/re_view/src"
                 "checkouts/re_view_hiccup/src"
                 "checkouts/re_view_routing/src"
                 "checkouts/re_view_prosemirror/src"
                 "checkouts/re_db/src"
                 "checkouts/shapes"
                 "checkouts/friendly"]

  :deploy-via :clojars)
