{:require       [[cljs.spec :include-macros true]
                 [maria.user :include-macros true]]
 :provided      [maria.core]
 :require-cache [maria.eval]
 :output-to     "resources/public/js/cljs_live_cache.js"
 :cljsbuild-out "resources/public/js/compiled/out"}