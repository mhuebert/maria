(use 'figwheel-sidecar.repl-api)
(start-figwheel! "eval-dev" "index-dev") ;; <-- fetches configuration
(cljs-repl)