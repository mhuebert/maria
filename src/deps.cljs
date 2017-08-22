{:foreign-libs [{:file           "js/provided.js"
                 :provides       ["cljsjs.react"
                                  "cljsjs.react.dom"
                                  "react"
                                  "react-dom"
                                  "cljsjs.markdown-it"
                                  "cljsjs.codemirror"]
                 :global-exports {react       React
                                  react-dom   ReactDOM
                                  markdown-it markdownit}}
                {:file     "js/codemirror.mode.clojure.js"
                 :provides ["codemirror.mode.clojure"]}
                {:file     "js/codemirror.addon.edit.closebrackets.js"
                 :provides ["codemirror.addon.edit.closebrackets"]}]}