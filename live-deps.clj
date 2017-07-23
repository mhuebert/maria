{:cljsbuild-out "resources/public/js/compiled/out-cljs-live"
 :bundle-out    "resources/public/js/cljs_live_bundles"
 :source-paths  ["src"]
 :bundles       [{:name     cljs.spec.alpha
                  :entry    [cljs.spec.alpha
                             cljs.spec.alpha$macros
                             cljs.analyzer.api]
                  :provided [maria.frames.user]}
                 {:name            maria.user
                  :entry           #{maria.user}
                  :provided        #{maria.frames.user}
                  #_:entry/exclude #_#{cljs.core.match
                                       cljs.core.match$macros
                                       cljs.analyzer$macros}
                  :entry/no-follow #{maria.eval
                                     cljs-live.eval
                                     cljs.js
                                     cljs.compiler
                                     maria.codemirror.editor
                                     maria.repl-specials
                                     cljs.core.match
                                     maria.views.repl-specials
                                     maria.live.analyzer
                                     }}]}