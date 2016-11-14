(defproject maria "0.1.0-SNAPSHOT"
  :description "A learning project."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.293"]

                 [cljsjs/codemirror "5.19.0-0"]
                 [cljsjs/marked "0.3.5-0"]

                 [org.clojars.mhuebert/cljs-live "0.1.3-SNAPSHOT"]
                 [org.clojars.mhuebert/re-view "0.1.5-SNAPSHOT"]]

  :plugins [[lein-figwheel "0.5.8"]
            [lein-npm "0.6.2"]
            [lein-cljsbuild "1.1.4" :exclusions [org.clojure/clojure]]]

  :npm {:dependencies [[stylus "0.54.5"]
                       [firebase-tools "3.1.0"]]}

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]

  :source-paths ["src"]

  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src"]
                        :figwheel     true
                        :compiler     {:main                 "maria.core"
                                       :asset-path           "js/compiled/out"
                                       :output-to            "resources/public/js/compiled/maria.js"
                                       :output-dir           "resources/public/js/compiled/out"
                                       :source-map-timestamp true
                                       :source-map           true
                                       :parallel-build       true}}
                       {:id           "prod"
                        :source-paths ["src"]
                        :compiler     {:main          "maria.core"
                                       :asset-path    "js/compiled/out"
                                       :output-to     "resources/public/js/compiled/maria.js"
                                       :dump-core     false
                                       :optimizations :simple}}]}

  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles {:dev {:dependencies [[binaryage/devtools "0.8.2"]
                                  [figwheel-sidecar "0.5.8"]
                                  [com.cemerick/piggieback "0.2.1"]]
                   ;; need to add dev source path here to get user.clj loaded
                   :source-paths ["src" "dev"]
                   ;; for CIDER
                   :plugins      [[cider/cider-nrepl "0.14.0"]]
                   :repl-options {; for nREPL dev you really need to limit output
                                  :init             (set! *print-length* 50)
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}})
