(defproject maria "0.1.0-SNAPSHOT"
  :description "A learning project."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/clojurescript "1.9.854"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [org.clojure/tools.reader "1.0.5"]
                 [com.cognitect/transit-cljs "0.8.239"]
                 [com.cognitect/transit-clj "0.8.300"]
                 [com.stuartsierra/dependency "0.2.0"]
                 [com.lucasbradstreet/cljs-uuid-utils "1.0.2"]

                 [fast-zip "0.7.0"]

                 [re-view "0.3.28-SNAPSHOT"]
                 [re-view-routing "0.1.3"]
                 [re-view-prosemirror "0.1.9-SNAPSHOT"]
                 [cljs-live "0.2.6-SNAPSHOT"]
                 [magic-tree "0.0.13-SNAPSHOT"]
                 [org.clojure/data.json "0.2.6"]

                 [cljsjs/codemirror "5.19.0-0"]
                 [cljsjs/react "16.0.0-beta.2-0"]
                 [cljsjs/react-dom "16.0.0-beta.2-0"]
                 ;[cljsjs/firebase "4.0.0-0"]

                 ;; just for bundles
                 [reagent "0.7.0" :exclusions [cljsjs/react]]
                 [leipzig "0.10.0"]
                 [cljs-bach "0.2.0"]
                 [thheller/shadow-cljs "1.0.20170802"]
                 ]

  :plugins [[lein-figwheel "0.5.12"]
            [lein-cljsbuild "1.1.7"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]

  :source-paths ["src"
                 "test"
                 "checkouts/re_view/src"
                 "checkouts/re_view_hiccup/src"
                 "checkouts/re_view_routing/src"
                 "checkouts/re_view_prosemirror/src"
                 "checkouts/magic_tree/src"
                 "checkouts/re_db/src"
                 "checkouts/cljs_live/src"]

  :cljsbuild {:builds [{:id           "live-dev"
                        :source-paths ["src"]
                        :figwheel     true
                        :compiler     {:main           "maria.frames.live-frame"
                                       :output-to      "resources/public/js/compiled/live.js"
                                       :output-dir     "resources/public/js/compiled/out-live-dev"
                                       :asset-path     "/js/compiled/out-live-dev"
                                       :language-in    :ecmascript5
                                       :source-map     true
                                       :optimizations  :none
                                       :parallel-build true}}
                       {:id           "modules"
                        :source-paths ["src"]
                        :figwheel     true
                        :compiler     {:modules        {:live-frame    {:entries   #{maria.frames.live-frame}
                                                                        :output-to "resources/public/js/compiled/live.js"}
                                                        :trusted-frame {:entries   #{maria.frames.trusted-frame}
                                                                        :output-to "resources/public/js/compiled/trusted.js"}}
                                       :output-dir     "resources/public/js/compiled/out-modules-dev"
                                       :asset-path     "/js/compiled/out-modules-dev"
                                       :language-in    :ecmascript5
                                       :source-map     true
                                       :optimizations  :none
                                       :install-deps   true
                                       :parallel-build true}}

                       {:id           "modules-prod"
                        :source-paths ["src"]
                        :figwheel     true
                        :compiler     {:modules        {:live-frame    {:entries   #{maria.frames.live-frame}
                                                                        :output-to "resources/public/js/compiled/live.js"}
                                                        :trusted-frame {:entries   #{maria.frames.trusted-frame}
                                                                        :output-to "resources/public/js/compiled/trusted.js"}}
                                       :npm-deps       {:react     "next"
                                                        :react-dom "next"}
                                       :output-dir     "resources/public/js/compiled/out-modules-prod"
                                       :asset-path     "/js/compiled/out-modules-prod"
                                       :language-in    :ecmascript5
                                       :source-map     true
                                       :optimizations  :simple
                                       :install-deps   true
                                       :parallel-build true}}

                       {:id           "live-prod"
                        :source-paths ["src"]
                        :compiler     {:main           "maria.frames.live-frame"
                                       :output-to      "resources/public/js/compiled/live.js"
                                       :output-dir     "resources/public/js/compiled/out-live-prod"
                                       :asset-path     "/js/compiled/out-live-prod"
                                       :language-in    :ecmascript5

                                       ;:source-map     "resources/public/js/compiled/live.js.map"
                                       :cache-analysis false
                                       :dump-core      false
                                       :parallel-build true
                                       :optimizations  :simple}}
                       {:id           "trusted-dev"
                        :source-paths ["src"]
                        :figwheel     true
                        :compiler     {:main           "maria.frames.trusted-frame"
                                       :output-to      "resources/public/js/compiled/trusted.js"
                                       :output-dir     "resources/public/js/compiled/out-trusted-dev"
                                       :asset-path     "/js/compiled/out-trusted-dev"
                                       :language-in    :ecmascript5
                                       :source-map     true
                                       :optimizations  :none
                                       :parallel-build true}}
                       {:id           "trusted-prod"
                        :source-paths ["src"]
                        :compiler     {:main           "maria.frames.trusted-frame"
                                       :output-to      "resources/public/js/compiled/trusted.js"
                                       :output-dir     "resources/public/js/compiled/out-trusted-prod"
                                       :asset-path     "/js/compiled/out-trusted-prod"
                                       :language-in    :ecmascript5
                                       :optimizations  :advanced
                                       :dump-core      false
                                       :infer-externs  true
                                       :parallel-build true}}]}

  :figwheel {:ring-handler figwheel-server.core/handler
             :css-dirs     ["resources/public/css"]
             :nrepl-port   7888}

  :aliases {"dev"     ["figwheel" "live-dev" "trusted-dev"]
            "build"   ["cljsbuild" "once" "live-prod" "trusted-prod"]
            "bundles" ["run" "-m" "cljs-live.bundle/main" "live-deps.clj"]}

  :deploy-via :clojars

  :profiles {:dev {:dependencies [[figwheel-pushstate-server "0.1.1-SNAPSHOT"]
                                  [binaryage/devtools "0.8.2"]
                                  [figwheel-sidecar "0.5.10"]
                                  [com.cemerick/piggieback "0.2.1"]]
                   ;; need to add dev source path here to get user.clj loaded
                   :source-paths ["src"
                                  "dev"]
                   ;; for CIDER
                   ;:plugins      [[cider/cider-nrepl "0.14.0"]]
                   :repl-options {; for nREPL dev you really need to limit output
                                  :init             (set! *print-length* 50)
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}})
