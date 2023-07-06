;; user.clj per Clerk instructions

(require '[nextjournal.clerk :as clerk])

;; start Clerk's built-in webserver on the default port 7777, opening the browser when done
(clerk/serve! {:browse? true
               ;;:watch-paths ["notebooks"]
               })

;; call `clerk/show!` explicitly to show a given notebook
(clerk/show! "notebooks/clerkified_maria.clj")