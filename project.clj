(defproject maria "0.1.0-SNAPSHOT"
  :description "A learning project."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0-alpha16"]
                 [org.clojure/clojurescript "1.9.542"]

                 [fast-zip "0.7.0"]
                 [org.clojure/core.match "0.3.0-alpha4"]

                 [cljsjs/codemirror "5.19.0-0"]
                 [cljsjs/marked "0.3.5-0"]


                 [re-view "0.3.16"]
                 [re-view-routing "0.1.3"]
                 [re-view-material "0.1.3"]

                 [cljs-live "0.1.17-SNAPSHOT"]
                 [magic-tree "0.0.2"]

                 [cljsjs/react "15.5.4-0"]
                 [cljsjs/react-dom "15.5.4-0"]]

  :plugins [[lein-figwheel "0.5.10"]
            [lein-cljsbuild "1.1.6" :exclusions [org.clojure/clojure]]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]

  :source-paths ["src"]

  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src"]
                        :figwheel     true
                        :compiler     {:main           "maria.core"
                                       :asset-path     "/js/compiled/out"
                                       :output-to      "resources/public/js/compiled/maria.js"
                                       :output-dir     "resources/public/js/compiled/out"
                                       ;:source-map-timestamp true
                                       :optimizations  :none
                                       :parallel-build true}}
                       {:id           "prod"
                        :source-paths ["src"]
                        :compiler     {:main           "maria.core"
                                       :asset-path     "/js/compiled/out"
                                       :output-to      "resources/public/js/compiled/maria.js"
                                       :output-dir     "resources/public/js/compiled/out-prod"
                                       :cache-analysis true
                                       :dump-core      false
                                       :optimizations  :simple}}]}

  :figwheel {:ring-handler figwheel-server.core/handler
             :css-dirs     ["resources/public/css"]}

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
