(use 'figwheel-sidecar.repl-api)
(start-figwheel! "eval" "index") ;; <-- fetches configuration
(cljs-repl)