{:cljsbuild-out "resources/public/js/compiled/out-cljs-live"
 :output-dir    "resources/public/js/cljs_bundles"
 :bundles       [#_{:name          cljs.core
                  :require-cache [cljs.core cljs.core$macros]}
                 {:name           maria.user
                  :source-paths   ["src"]
                  :require        [[maria.user :include-macros true]]
                  :require-macros [magic-tree.backtick]
                  :provided       [maria.frames.user]
                  :require-cache  [maria.eval
                                   cljs.js                  ;; for `doc` on cljs.js namespace
                                   maria.editor
                                   maria.repl-specials
                                   cljs.core.match
                                   maria.views.repl-specials]}
                 #_{:name         cljs.spec.alpha
                  :source-paths ["src"]
                  :require      [[cljs.spec.test.alpha]
                                 [cljs.spec.alpha :include-macros true]
                                 [cljs.spec.test.alpha :include-macros true]]
                  :dependencies [[org.clojure/test.check "0.10.0-alpha2"]]
                  :provided     [maria.frames.user]
                  }]}