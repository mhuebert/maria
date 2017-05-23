{:cljsbuild-out "resources/public/js/compiled/out-prod"
 :output-dir    "resources/public/js/cljs_bundles"
 :bundles       [{:name          cljs.core
                  :require-cache [cljs.core cljs.core$macros]}
                 {:name           maria.user
                  :require        [[maria.user :include-macros true]]
                  :require-macros [magic-tree.backtick]
                  :provided       [maria.core]
                  :require-cache  [maria.eval]}]}