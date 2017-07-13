(ns maria.commands.exec
  (:require [re-db.d :as d]
            [goog.events :as events]
            [maria.commands.registry :as registry]
            [clojure.set :as set]))

(def current-doc-toolbar nil)
(def current-editor nil)

(defn get-context []
  {:editor      current-editor
   :doc-toolbar current-doc-toolbar
   :signed-in?  (d/get :auth-public :signed-in?)})

(defn some-command
  "Returns command associated with name, if it exists and is valid given the provided context."
  ([context name]
   (let [{:keys [pred] :as command-entry} (get @registry/commands name)]
     (when (or (nil? pred) (pred context))
       command-entry))))

(defn exec-command
  ([command-name] (exec-command command-name nil))
  ([command-name e]
   (let [context (get-context)]
     (when-let [{:keys [command]} (some-command context command-name)]
       (let [result (command context)]
         (when (and e (not= result (.-Pass js/CodeMirror)))
           (.stopPropagation e)
           (.preventDefault e)))))))

(defn contextual-hints [modifiers-down]
  (let [current-context (get-context)]
    (->> @registry/mappings
         (keep (fn [[keyset {:keys [exec]}]]
                 (when (set/subset? modifiers-down keyset)
                   ;; change this later for multi-step keysets
                   (some->> (seq exec)
                            (keep (partial some-command current-context))))))
         (apply concat)
         (distinct))))

(defn init-listeners []
  (let [clear-keys #(d/transact! [[:db/add :commands :modifiers-down #{}]
                                  [:db/add :commands :which-key/active? false]])
        clear-which-key! #(do (doseq [timeout (d/get :commands :timeouts)]
                                (js/clearTimeout timeout))
                              (d/transact! [[:db/add :commands :which-key/active? false]]))
        which-key-delay 500
        handle-keydown (fn [e]
                         (let [keycode (registry/normalize-keycode (.-keyCode e))
                               keys-down (d/get :commands :modifiers-down)
                               modifier? (contains? registry/modifiers keycode)]
                           (if-let [commands (seq (registry/get-keyset-commands (conj keys-down keycode)))]
                             (do (doseq [command commands]
                                   (exec-command command e))
                                 (clear-which-key!))
                             (when modifier?
                               (d/transact! [[:db/update-attr :commands :modifiers-down conj keycode]
                                             [:db/update-attr :commands :timeouts conj (js/setTimeout #(let [keys-down (d/get :commands :modifiers-down)]
                                                                                                         (when (and (seq keys-down)
                                                                                                                    (not= keys-down #{(registry/keystring->code "shift")}))
                                                                                                           (d/transact! [[:db/add :commands :which-key/active? true]]))) which-key-delay)]])))))]
    (clear-keys)
    (events/listen js/window "keydown" handle-keydown true)

    (events/listen js/window "mousedown"
                   (fn [e]
                     (let [keycode (registry/button->code (.-button e))]
                       (when-let [commands (registry/get-keyset-commands (conj (d/get :commands :modifiers-down) keycode))]
                         (doseq [command commands]
                           (exec-command command e))))))

    (events/listen js/window "keyup"
                   (fn [e]
                     (let [keycode (registry/normalize-keycode (.-keyCode e))
                           modifier? (registry/modifiers keycode)]
                       (when modifier?
                         (clear-which-key!)
                         (d/transact! [[:db/update-attr :commands :modifiers-down disj keycode]])
                         (when (empty? (d/get :commands :modifiers-down))
                           (d/transact! [[:db/add :commands :which-key/active? false]]))))))

    (events/listen js/window #js ["blur" "focus"] #(when (= (.-target %) (.-currentTarget %))
                                                     (clear-keys)))))

(defonce _ (init-listeners))