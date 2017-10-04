{:cljsbuild-out "resources/public/js/compiled/out-cljs-live"
 :bundle-out    "resources/public/js/cljs_live_bundles"
 :source-paths  ["src"]
 :bundles       [{:name     cljs.spec.alpha
                  :entry    [cljs.spec.alpha
                             cljs.spec.alpha$macros]
                  :provided [maria.frames.live-frame]}
                 {:name            maria.user
                  :entry           #{maria.user}
                  :provided        #{maria.frames.live-frame}
                  :entry/no-follow #{maria.eval
                                     cljs-live.eval
                                     cljs.js
                                     cljs.compiler
                                     maria.editors.code
                                     maria.repl-specials
                                     cljs.core.match
                                     maria.views.repl-specials
                                     maria.live.analyze
                                     maria.live.source-lookups}
                  :entry/exclude   #{cljs.pprint}}
                 #_{:name          reagent.core
                  :entry         reagent.core
                  :entry/exclude cljsjs.react
                  :provided      maria.frames.live-frame}
                 #_{:name     bach-leipzig
                  :entry    #{leipzig.canon
                              leipzig.chord
                              leipzig.melody
                              leipzig.scale
                              leipzig.temperament
                              cljs-bach.synthesis}
                  :provided maria.frames.live-frame}]}
