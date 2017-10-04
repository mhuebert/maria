(defproject maria/editor "0.1.0-SNAPSHOT"
  :description "A ClojureScript editor for beginners"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/clojurescript "1.9.946"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [com.cognitect/transit-cljs "0.8.239"]
                 [com.cognitect/transit-clj "0.8.300"]
                 [com.lucasbradstreet/cljs-uuid-utils "1.0.2"]
                 [org.clojure/data.json "0.2.6"]

                 [fast-zip "0.7.0"]

                 [maria/friendly "0.1.0-SNAPSHOT"]
                 [maria/shapes "0.1.0-SNAPSHOT"]

                 [re-view "0.3.31"]
                 [re-view-routing "0.1.3"]
                 [re-view-prosemirror "0.1.10-SNAPSHOT"]
                 [cljs-live "0.2.8-SNAPSHOT"]


                 [lark/commands "0.1.2-SNAPSHOT"]
                 [lark/value-viewer "0.1.2-SNAPSHOT"]
                 [lark/cells "0.1.5-SNAPSHOT"]

                 [lark/structure "0.1.3-SNAPSHOT"]
                 [lark/editors "0.1.3-SNAPSHOT"]
                 [lark/tree "0.1.3-SNAPSHOT"]

                 [cljsjs/codemirror "5.19.0-0"]
                 [cljsjs/react "16.0.0-beta.2-0"]
                 [cljsjs/react-dom "16.0.0-beta.2-0"]
                 [cljsjs/tinycolor "1.3.0-0"]
                 [cljsjs/firebase "4.0.0-0"]

                 ;; just for bundles
                 ;[reagent "0.7.0" :exclusions [cljsjs/react]]
                 ;[leipzig "0.10.0"]
                 ;[cljs-bach "0.2.0"]
                 ]

  :plugins [[lein-figwheel "0.5.13"]
            [lein-cljsbuild "1.1.7"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]

  :source-paths ["src"
                 "test"
                 "checkouts/re_view/src"
                 "checkouts/re_view_hiccup/src"
                 "checkouts/re_view_routing/src"
                 "checkouts/re_view_prosemirror/src"
                 "checkouts/re_db/src"
                 "checkouts/cljs_live/src"
                 "checkouts/shapes"
                 "checkouts/friendly"
                 "../../lark/cells/src"
                 "../../lark/commands/src"
                 "../../lark/structure/src"
                 "../../lark/editors/src"
                 "../../lark/tree/src"]

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
                       #_{:id           "modules"
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

                       #_{:id           "modules-prod"
                          :source-paths ["src"]
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
                                       ;:pseudo-names true
                                       :infer-externs  true
                                       :parallel-build true}}
                       {:id           "tests"
                        :source-paths ["src"
                                       "test"]
                        :figwheel     {:on-jsload "maria.tests-runner/run-tests"}
                        :compiler     {:main          maria.tests-runner
                                       :output-to     "test-target/public/js/tests.js"
                                       :output-dir    "test-target/public/js/out"
                                       :asset-path    "js/out"
                                       :optimizations :none}}]}

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
