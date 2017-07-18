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

(defn stop-event [e]
  (.stopPropagation e)
  (.preventDefault e))

(defn exec-command
  [command-name]
  (let [context (get-context)]
    (when-let [{:keys [command]} (some-command context command-name)]
      (command context))))

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
        clear-timeout! #(some-> (d/get :commands :timeout) (js/clearTimeout))
        clear-which-key! #(do (clear-timeout!)
                              (d/transact! [[:db/add :commands :which-key/active? false]]))
        which-key-delay 500
        handle-keydown (fn [e]
                         (let [keycode (registry/normalize-keycode (.-keyCode e))
                               keys-down (d/get :commands :modifiers-down)
                               modifier? (contains? registry/modifiers keycode)]
                           (if-let [commands (seq (registry/get-keyset-commands (conj keys-down keycode)))]
                             (let [results (mapv exec-command commands)]
                               (when-not (contains? (set results) (.-Pass js/CodeMirror))
                                 (stop-event e))
                               (clear-which-key!))
                             (when modifier?
                               (clear-timeout!)
                               (d/transact! [[:db/update-attr :commands :modifiers-down conj keycode]
                                             [:db/add :commands :timeout (-> #(let [keys-down (d/get :commands :modifiers-down)]
                                                                                (when (and (seq keys-down)
                                                                                           (not= keys-down #{(registry/endkey->keycode "shift")}))
                                                                                  (d/transact! [[:db/add :commands :which-key/active? true]])))
                                                                             (js/setTimeout which-key-delay))]])))))]
    (clear-keys)
    (events/listen js/window "keydown" handle-keydown true)

    (events/listen js/window "mousedown"
                   (fn [e]
                     (doseq [command (-> (conj (d/get :commands :modifiers-down) (.-button e))
                                         (registry/get-keyset-commands))]
                       (exec-command command))))

    (events/listen js/window "keyup"
                   (fn [e]
                     (let [keycode (registry/normalize-keycode (.-keyCode e))]
                       (when (registry/modifiers keycode)
                         (d/transact! [[:db/update-attr :commands :modifiers-down disj keycode]])
                         (when (empty? (d/get :commands :modifiers-down))
                           (d/transact! [[:db/add :commands :which-key/active? false]]))))))

    (events/listen js/window #js ["blur" "focus"] #(when (= (.-target %) (.-currentTarget %))
                                                     (clear-keys)))))

(defonce _ (init-listeners))