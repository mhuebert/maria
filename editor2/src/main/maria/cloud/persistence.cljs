(ns maria.cloud.persistence
  (:require [maria.cloud.github :as gh]
            [maria.editor.keymaps :as keymaps]))

(keymaps/register-commands!
  {:file/new       {:kind     :prose
                    :bindings [:Mod-n]
                    :f        #(prn :new)}
   :file/duplicate {:kind :prose
                    :f    #(prn :duplicate)}
   :file/revert    {:kind :prose
                    :f    #(prn :revert)}
   :file/save      {:kind     :prose
                    :bindings [:Mod-s]
                    :when     gh/token
                    :f        #(prn :save)}})