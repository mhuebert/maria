{:cljsbuild-out "resources/public/js/compiled/out"
 :output-dir    "resources/public/js/cljs_bundles"
 :bundles       [{:name          maria-user
                  :require       [[cljs.spec :include-macros true]
                                  [maria.user :include-macros true]]
                  :provided      [maria.core]
                  :require-cache [maria.eval]
                  :cljsbuild-out "resources/public/js/compiled/out"}]}