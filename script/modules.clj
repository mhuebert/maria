(require '[cljs.build.api :as b])

(b/build "src"
         {:modules        {:live-frame    {:entries   #{maria.frames.live-frame}
                                           :output-to "resources/public/js/compiled/live.js"}
                           :trusted-frame {:entries   #{maria.frames.trusted-frame}
                                           :output-to "resources/public/js/compiled/trusted.js"}}
          :output-dir     "resources/public/js/compiled/out-modules-dev"
          :asset-path     "/js/compiled/out-modules-dev"
          :language-in    :ecmascript5
          :source-map     true
          :optimizations  :none
          :install-deps   true
          :parallel-build true})

(System/exit 0)