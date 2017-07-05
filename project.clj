(defproject maria "0.1.0-SNAPSHOT"
  :description "A learning project."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0-alpha16"]
                 [org.clojure/clojurescript "1.9.562"]

                 [org.clojure/core.match "0.3.0-alpha4"]
                 [com.cognitect/transit-cljs "0.8.239"]

                 [fast-zip "0.7.0"]
                 [cljsjs/codemirror "5.19.0-0"]
                 [cljsjs/marked "0.3.5-0"]


                 [re-view "0.3.16"]
                 [re-view-routing "0.1.3"]
                 [re-view-material "0.1.3"]

                 [cljs-live "0.1.20-SNAPSHOT"]
                 [magic-tree "0.0.6-SNAPSHOT"]

                 [cljsjs/react "15.5.4-0"]
                 [cljsjs/react-dom "15.5.4-0"]]

  :plugins [[lein-figwheel "0.5.10"]
            [lein-cljsbuild "1.1.6" :exclusions [org.clojure/clojure]]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]

  :source-paths ["src"]

  :cljsbuild {:builds [{:id           "eval"
                        :source-paths ["src"]
                        :figwheel     true
                        :compiler     {:main           "maria.core"
                                       :asset-path     "/js/compiled/out-eval"
                                       :output-to      "resources/public/js/compiled/eval.js"
                                       :output-dir     "resources/public/js/compiled/out-eval"
                                       ;:source-map-timestamp true
                                       :source-map     true
                                       :optimizations  :none
                                       :parallel-build true}}
                       {:id           "eval-prod"
                        :source-paths ["src"]
                        :compiler     {:main           "maria.core"
                                       :asset-path     "/js/compiled/out-eval-prod"
                                       :output-to      "resources/public/js/compiled/eval.js"
                                       :output-dir     "resources/public/js/compiled/out-eval-prod"
                                       :cache-analysis true
                                       :dump-core      false
                                       :optimizations  :simple}}
                       {:id           "index"
                        :source-paths ["src"]
                        :compiler     {:main           "maria.index"
                                       :output-to      "resources/public/js/compiled/index.js"
                                       :output-dir     "resources/public/js/compiled/out-index"
                                       :source-map     true
                                       :asset-path     "/js/compiled/out-index"
                                       :optimizations  :none
                                       :parallel-build true}}
                       {:id           "index-prod"
                        :source-paths ["src"]
                        :compiler     {:main           "maria.index"
                                       :output-to      "resources/public/js/compiled/index.js"
                                       :output-dir     "resources/public/js/compiled/out-index-prod"
                                       :asset-path     "/js/compiled/out-index-prod"
                                       :optimizations  :advanced
                                       :parallel-build true}}]}

  :figwheel {:ring-handler figwheel-server.core/handler
             :css-dirs     ["resources/public/css"]
             :nrepl-port   7888}

  :deploy-via :clojars

  :profiles {:dev {:dependencies [[figwheel-pushstate-server "0.1.0"]
                                  [binaryage/devtools "0.8.2"]
                                  [figwheel-sidecar "0.5.10"]
                                  [com.cemerick/piggieback "0.2.1"]]
                   ;; need to add dev source path here to get user.clj loaded
                   :source-paths ["src" "dev"]
                   ;; for CIDER
                   :plugins      [[cider/cider-nrepl "0.14.0"]]
                   :repl-options {; for nREPL dev you really need to limit output
                                  :init             (set! *print-length* 50)
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}})
