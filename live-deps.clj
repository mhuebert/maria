{:require        [[cljs.spec :include-macros true]
                  [cljs.spec.impl.gen :include-macros true]]
 :require-macros [cljs.spec
                  cljs.spec.impl.gen]
 :precompiled    [cljs.spec
                  cljs.js
                  clojure.set
                  clojure.string
                  clojure.walk
                  cljs.pprint
                  cljs.tools.reader.reader-types
                  cljs.tools.reader
                  cljs.analyzer]
 :output-to      "resources/public/js/cljs_live_cache_core.js"
 :cljsbuild-out  "resources/public/js/compiled/out"}