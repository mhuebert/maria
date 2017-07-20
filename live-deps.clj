{:cljsbuild-out "resources/public/js/compiled/out-cljs-live"
 :output-dir    "resources/public/js/cljs_bundles"
 :source-paths  ["src"]
 :bundles       [#_{:name          cljs.core
                  :require-cache [cljs.core cljs.core$macros]}
                 {:name           maria.user
                  :require        [[maria.user :include-macros true]]
                  :require-macros [magic-tree.backtick]
                  :provided       [maria.frames.user]
                  ;; :require-cache both prevents cljs from trying to load the lib from source,
                  ;; and also ensures that metadata becomes available in the environment.
                  :require-cache  [maria.eval
                                   cljs-live.eval
                                   cljs.js                  ;; for `doc` on cljs.js namespace
                                   cljs.compiler
                                   maria.codemirror.editor
                                   maria.repl-specials
                                   cljs.core.match
                                   maria.views.repl-specials
                                   maria.live.analyzer]}
                 #_{:name         cljs.spec.alpha
                  :require      [[cljs.spec.test.alpha]
                                 [cljs.spec.alpha :include-macros true]
                                 [cljs.spec.test.alpha :include-macros true]]
                  :dependencies [[org.clojure/test.check "0.10.0-alpha2"]]
                  :provided     [maria.frames.user]
                  }]}