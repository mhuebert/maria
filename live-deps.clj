{:require        [[cljs.spec :include-macros true]
                  [cljs.spec.impl.gen :include-macros true]]
 :precompiled    [cljs.spec
                  cljs.js
                  clojure.set
                  clojure.string
                  clojure.walk
                  cljs.pprint
                  cljs.tools.reader.reader-types
                  cljs.tools.reader
                  cljs.analyzer
                  maria.eval]
 :require-caches [maria.user]
 :require-macros [maria.user]
 :output-to      "resources/public/js/cljs_live_cache.js"
 :cljsbuild-out  "resources/public/js/compiled/out"}