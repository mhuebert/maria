(ns maria.clerkify
  (:require ["jszip" :as jszip]
            ["file-saver" :as file-saver]
            [applied-science.js-interop :as j]
            [maria.editor.doc :as editor.doc]
            [maria.editor.keymaps :as keymaps]
            [shadow.resource :as rc]))

(defn download-clerkified-zip
  "Creates & downloads ZIP file of current editor view, packaged up to run locally with NextJournal's Clerk.
  Approach adapted from https://stackoverflow.com/a/49836948/706499"
  [source]
  (-> (new jszip)
      (.file "deps.edn" (rc/inline "clerkify/deps.edn"))
      (.file "dev/user.clj" (rc/inline "clerkify/user.clj"))
      ;; TODO fn taking ns & file name from doc info?
      (.file "notebooks/clerkified_maria.clj"
             (str (rc/inline "clerkify/clerkified_maria.clj")
                  "\n\n"
                  source))
      (.generateAsync #js{:type "blob"})
      (.then (fn [content]
               (file-saver/saveAs content "clerkified-maria.zip")))))

(keymaps/register-commands!
  {:file/save-as-clerk-project {:kind :prose
                                :f    (fn [state dispatch view]
                                        (download-clerkified-zip
                                          (-> (j/get-in view [:state :title])
                                              editor.doc/doc->clj)))}})