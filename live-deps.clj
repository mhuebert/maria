{:cljsbuild-out "resources/public/js/compiled/out-cljs-live"
 :output-dir    "resources/public/js/cljs_bundles"
 :bundles       [{:name          cljs.core
                  :require-cache [cljs.core cljs.core$macros]}
                 {:name           maria.user
                  :source-paths   ["src"]
                  :require        [[maria.user :include-macros true]]
                  :require-macros [magic-tree.backtick]
                  :provided       [maria.frames.user]
                  :require-cache  [maria.eval
                                   maria.editor
                                   maria.repl-specials]}
                 {:name     cljs.spec.alpha
                  :require  [[cljs.spec.test.alpha]
                             [cljs.spec.alpha :include-macros true]
                             [cljs.spec.test.alpha :include-macros true]
                             ;; TODO - see why the cljs.analyzer.api dependency is not found automatically
                             [cljs.analyzer.api]]
                  :dependencies [[org.clojure/test.check "0.10.0-alpha2"]]
                  :provided [maria.frames.user]
                  }]}