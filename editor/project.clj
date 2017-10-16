(defproject maria/editor "0.1.0-SNAPSHOT"
  :description "A ClojureScript editor for beginners"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[thheller/shadow-cljs "2.0.18"]
                 [org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/clojurescript "1.9.946"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [com.cognitect/transit-cljs "0.8.239"]
                 [com.cognitect/transit-clj "0.8.300"]
                 [com.lucasbradstreet/cljs-uuid-utils "1.0.2"]
                 [org.clojure/data.json "0.2.6"]

                 [fast-zip "0.7.0"]

                 [maria/friendly "0.1.0-SNAPSHOT"]
                 [maria/shapes "0.1.0-SNAPSHOT"]

                 [re-view "0.3.33"]
                 [re-view-routing "0.1.3"]
                 [re-view-prosemirror "0.1.10-SNAPSHOT"]
                 [cljs-live "0.2.9-SNAPSHOT"]


                 [lark/commands "0.2.0-SNAPSHOT"]
                 [lark/value-viewer "0.1.2-SNAPSHOT"]
                 [lark/cells "0.1.5-SNAPSHOT"]

                 [lark/structure "0.1.3-SNAPSHOT"]
                 [lark/editors "0.1.3-SNAPSHOT"]
                 [lark/tree "0.1.3-SNAPSHOT"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]

  :source-paths ["src"
                 "checkouts/re_view/src"
                 "checkouts/re_view_hiccup/src"
                 "checkouts/re_view_routing/src"
                 "checkouts/re_view_prosemirror/src"
                 "checkouts/re_db/src"
                 "checkouts/cljs_live/src"
                 "checkouts/shapes"
                 "checkouts/friendly"]

  :deploy-via :clojars)
