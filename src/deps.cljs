{:foreign-libs [{:file           "js/provided.js"
                 :provides       ["cljsjs.react"
                                  "cljsjs.react.dom"
                                  "react"
                                  "react-dom"
                                  "cljsjs.markdown-it"]
                 :global-exports {react       React
                                  react-dom   ReactDOM
                                  markdown-it markdownit}}
                {:file     "js/codemirror.js"
                 :global-exports {cljsjs.codemirror CodeMirror}
                 :provides ["cljsjs.codemirror"]}
                {:file     "js/codemirror.mode.clojure.js"
                 :provides ["codemirror.mode.clojure"]
                 :requires ["cljsjs.codemirror"]}
                {:file     "js/codemirror.addon.edit.closebrackets.js"
                 :provides ["codemirror.addon.edit.closebrackets"]
                 :requires ["cljsjs.codemirror"]}
                {:file     "js/codemirror.addon.mark-selection.js"
                 :provides ["codemirror.addon.markselection"]
                 :requires ["cljsjs.codemirror"]}
                {:file "js/codemirror.addon.search.searchcursor.js"
                 :provides ["codemirror.addon.search.searchcursor"]
                 :requires ["cljsjs.codemirror"]}]}