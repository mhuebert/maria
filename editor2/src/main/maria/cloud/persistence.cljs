(ns maria.cloud.persistence
  (:require [maria.cloud.github :as gh]
            [maria.cloud.local :as local]
            [maria.editor.doc :as doc]
            [maria.editor.keymaps :as keymaps]
            [yawn.hooks :as h]
            [goog.functions :as gf]))

(defn autosave-fn
  "Returns a callback that will save the current doc to local storage after a 1s debounce."
  []
  (-> (fn [id ^js prev-state ^js next-state]
        (when-not (.eq (.-doc prev-state) (.-doc next-state))
          (local/put! id (doc/doc->clj (.-doc next-state)))))
      (gf/debounce 1000)))

(keymaps/register-commands!
  {:file/new {:kind :prose
              :bindings [:Mod-n]
              ;; generate a new UUID,
              ;; go to that (local) route,
              ;; sync local storage for that UUID,
              ;; if empty create a blank doc.
              :f #(prn :new)}
   :file/duplicate {:kind :prose
                    ;; create a new gist with contents of current doc.
                    :f #(prn :duplicate)}
   :file/revert {:kind :prose
                 ;; :when local state diverges from gist state.
                 ;; reset local state to gist state.
                 :f #(prn :revert)}
   :file/save {:kind :prose
               :bindings [:Mod-s]
               :when gh/token
               ;; if local, create a new gist and then navigate there.
               ;; if gist, save a new revision of that gist.
               :f #(prn :save)}})