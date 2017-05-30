{:cljsbuild-out "resources/public/js/compiled/out"
 :output-dir    "resources/public/js/cljs_bundles"
 :bundles       [#_{:name          cljs.core
                  :require-cache [cljs.core cljs.core$macros]}
                 {:name           maria.user
                  :require        [[maria.user :include-macros true]]
                  :require-macros [magic-tree.backtick]
                  :provided       [maria.core]
                  :require-cache  [maria.eval]}
                 #_{:name           cljs.spec.alpha
                  :require        [cljs.spec.alpha]
                  :require-macros [cljs.spec.alpha]
                  :provided       [maria.core
                                   maria.user
                                   cljs.core
                                   cljs.core$macros]}]}