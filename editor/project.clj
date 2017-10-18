(defproject maria/editor "0.1.0-SNAPSHOT"
  :description "A ClojureScript editor for beginners"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[thheller/shadow-cljs "2.0.22"]
                 [org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/clojurescript "1.9.946"]

                 [org.clojure/core.match "0.3.0-alpha4"]
                 [com.cognitect/transit-cljs "0.8.239"]
                 [com.cognitect/transit-clj "0.8.300"]
                 [com.lucasbradstreet/cljs-uuid-utils "1.0.2"]
                 [org.clojure/data.json "0.2.6"]

                 [fast-zip "0.7.0"]

                 [maria/friendly "0.1.0"]
                 [maria/shapes "0.1.0"]

                 [re-view "0.4.2"]
                 [re-view/prosemirror "0.2.2"]

                 [lark/tools "0.1.10"]
                 [lark/cells "0.1.5"]]

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
