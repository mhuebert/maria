(ns maria-commands.exec
  (:require [re-db.d :as d]
            [goog.events :as events]
            [maria-commands.registry :as registry]
            [clojure.set :as set]))

(def context (volatile! {}))

(defn set-context! [values]
  (vswap! context merge values))

(defn get-context []
  (let [{:keys [block-view] :as context} @context]
    (-> context
        (assoc :signed-in? (d/get :auth-public :signed-in?))
        (cond-> block-view
                (merge {:editor (.getEditor block-view)}
                       (select-keys block-view [:block :blocks]))))))

(defn get-command
  "Returns command associated with name, if it exists.
  Add contextual data, :exec? and :intercept?."
  [context name]
  (when-let [{:keys [exec-pred intercept-pred] :as command-entry} (get @registry/commands name)]
    (let [exec? (boolean (or (nil? exec-pred) (exec-pred context)))
          intercept? (if intercept-pred (intercept-pred context)
                                        exec?)]
      (assoc command-entry
        :exec? exec?
        :intercept? intercept?))))

(defn stop-event [e]
  (.stopPropagation e)
  (.preventDefault e))

(defn exec-command
  "Execute a command (returned by `get-command`)"
  [context {:keys [command exec? intercept?]}]
  (let [result (when exec? (command context))]
    {:intercept! (and intercept? (not (or (false? result)
                                          (= result (.-Pass js/CodeMirror)))))}))

(defn exec-command-name
  [name]
  (let [context (get-context)]
    (exec-command context (get-command context name))))

(defn contextual-hints [modifiers-down]
  (let [current-context (get-context)]
    (->> @registry/mappings
         (keep (fn [[keyset {:keys [exec]}]]
                 (when (set/subset? modifiers-down keyset)
                   ;; change this later for multi-step keysets
                   (some->> (seq exec)
                            (map (partial get-command current-context))
                            (filter :exec?)))))
         (apply concat)
         (distinct))))

(defn init-listeners []
  (let [clear-keys #(d/transact! [[:db/add :commands :modifiers-down #{}]
                                  [:db/add :commands :which-key/active? false]])
        clear-timeout! #(some-> (d/get :commands :which-key/timeout) (js/clearTimeout))
        clear-which-key! #(do (clear-timeout!)
                              (d/transact! [[:db/add :commands :which-key/active? false]]))

        which-key-delay 1500
        handle-keydown (fn [e]
                         (let [keycode (registry/normalize-keycode (.-keyCode e))
                               {keys-down :modifiers-down
                                which-key-active? :which-key/active?} (d/entity :commands)
                               modifier? (contains? registry/modifiers keycode)
                               command-names (seq (registry/get-keyset-commands (conj keys-down keycode)))
                               context (when command-names
                                         (get-context))
                               commands (when command-names
                                          (mapv #(get-command context %) command-names))
                               results (when command-names
                                         (mapv #(exec-command context %) commands))]
                           (when (and which-key-active? (= keycode 27))
                             (d/transact! [[:db/add :commands :which-key/active? false]]))
                           ;; if we use Tab as a modifier,
                           ;; we'll stop its default behaviour
                           #_(when (= 9 keycode)
                             (.preventDefault e)
                             (.stopPropagation e))

                           (when (seq (filter :intercept! results))
                             (stop-event e)
                             (clear-which-key!))

                           (clear-timeout!)

                           (when modifier?
                             (d/transact! [[:db/update-attr :commands :modifiers-down conj keycode]
                                           [:db/add :commands :which-key/timeout
                                            (js/setTimeout
                                              #(let [keys-down (d/get :commands :modifiers-down)]
                                                 (when (and (seq keys-down)
                                                            (not= keys-down #{(registry/endkey->keycode "shift")}))
                                                   (d/transact! [[:db/add :commands :which-key/active? true]])))
                                              which-key-delay)]]))))]
    (clear-keys)
    (events/listen js/window "keydown" handle-keydown true)

    (events/listen js/window "mousedown"
                   (fn [e]
                     (doseq [command (-> (conj (d/get :commands :modifiers-down) (.-button e))
                                         (registry/get-keyset-commands))]
                       (exec-command-name command))))

    (events/listen js/window "keyup"
                   (fn [e]
                     (let [keycode (registry/normalize-keycode (.-keyCode e))]
                       (when (registry/modifiers keycode)
                         (d/transact! [[:db/update-attr :commands :modifiers-down disj keycode]])
                         (when (empty? (d/get :commands :modifiers-down))
                           (d/transact! [[:db/add :commands :which-key/active? false]])))))
                   true)

    (events/listen js/window #js ["blur" "focus"] #(when (= (.-target %) (.-currentTarget %))
                                                     (clear-keys)))))

(defonce _ (init-listeners))