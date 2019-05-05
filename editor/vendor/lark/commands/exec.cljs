(ns lark.commands.exec
  (:require [lark.commands.registry :as registry]
            [clojure.set :as set]))

(def debug? false )
(defonce context (volatile! {}))
(defonce last-selections (volatile! (list)))
(def which-key-time 1000)

(defonce WHICH_KEY_STATE
         ;;Atom to hold currently held modifiers & which-key state.
         (atom {:modifiers-down #{}
                :which-key/active? false}))

(defn clear-which-key
      "Fn which toggles which-key off, clearing timeout if it exists."
      [{:keys [timeout] :as current-state}]
      (some-> timeout
              (js/clearTimeout))
      (dissoc current-state :timeout :which-key/active?))

(defonce _ (do
             (doseq [modifier ["shift"
                               "alt"
                               "meta"]]
                    (let [internal-modifier (registry/format-segment :internal modifier)]
                         (.register_combo registry/Keypress
                                          #js {:keys #js [modifier]
                                               :is_exclusive false
                                               :is_solitary false
                                               :on_keydown #(let [{:keys [timeout]} @WHICH_KEY_STATE]
                                                                 (reset! WHICH_KEY_STATE
                                                                         (-> @WHICH_KEY_STATE
                                                                             (update :modifiers-down conj internal-modifier)
                                                                             (cond-> (nil? timeout)
                                                                                     (assoc :timeout (js/setTimeout (fn []
                                                                                                                      (swap! WHICH_KEY_STATE assoc :which-key/active? true)) which-key-time)))))
                                                                 true)
                                               :on_keyup #(do
                                                            (reset! WHICH_KEY_STATE (-> @WHICH_KEY_STATE
                                                                                        (update :modifiers-down disj internal-modifier)
                                                                                        (clear-which-key)))
                                                            true)})))))

(defn set-context!
      "Mutates command-context by merging provided context map."
      [ctx]
      (vswap! context merge ctx))

(defonce -before-exec (volatile! {}))
(defn before-exec
      "Registers a function `f` which will be called before any command is executed."
      [key f]
      (vswap! -before-exec assoc key f))

(defonce -context-augmenters (volatile! {}))
(defn add-context-augmenter!
      "Registers a reducing function which will be applied to the context before it is returned from `get-context`."
      [key f]
      (vswap! -context-augmenters assoc key f))

(defn get-context
      "Returns the current command context, as determined by previous calls to `set-context!`,
      after context augmenting fns have been applied."
      ([] (get-context nil))
      ([event-attrs]
       (let [{:keys [block-view] :as context} @context]
            (let [augment-context (apply comp (vals @-context-augmenters))]
                 (-> context
                     (merge event-attrs)
                     (augment-context))))))

(defn apply-context
      "Add contextual data to command. Adds keys `:exec?`, whether the command will be evaluated
      given the current context, and `:intercept?`, whether the command should preventDefault
      even if it is not executed and returns true."
      [context {:keys [exec-pred intercept-pred] :as command-entry}]
      (let [exec? (boolean (or (nil? exec-pred) (exec-pred context)))
            intercept? (and intercept-pred (intercept-pred context))]
           (assoc command-entry
                  :exec? exec?
                  :intercept? intercept?)))

(defn get-command
      "Returns command associated with name, if it exists.
      Add contextual data, :exec? and :intercept?."
      [context name]
      (apply-context context (get @registry/commands name)))

(defn contextual?
      "Returns true if the command will have an effect (either by executing or
      preventingDefault). Expects a command which has already had `apply-context` applied."
      [command]
      (or (:exec? command) (:intercept? command)))

(defn exec-command
      "Execute a command (returned by `get-command`)"
      [context {:keys [command exec? intercept? name]}]
      (let [result (when exec? (command context))]
           (when (and exec? debug?) (prn "Executed command:" name))
           (and result (not= result :lark.commands/Pass))))

(defn exec-command-name
      "Execute a command by name."
      ([name] (exec-command-name name (get-context)))
      ([name context]
       (exec-command context (get-command context name))))

(defn keyset-commands
      "Returns commands which are at least partial matches of the currently pressed keys."
      ([] (keyset-commands #{} (get-context)))
      ([modifiers-down current-context]
       (->> @registry/mappings
            (keep (fn [[keyset {:keys [exec]}]]
                    (when (set/subset? modifiers-down keyset)
                      (some->> (seq exec)
                               (map (partial get-command current-context))
                               (filter :exec?)))))
            (apply concat)
            (distinct))))

(defn contextual-commands
      [context]
      (->> (vals @registry/commands)
           (mapv #(apply-context context %))))

(def reverse-compare
  "Comparator function with inverse order to regular `compare`"
  (fn [a b] (compare b a)))

(defn handler
      "Main handler function which is called whenever a bound keybinding is triggered.
      Finds context-relevant commands and executes them until one returns true."
      [binding binding-vec]
      (let [binding-set (set binding-vec)
            command-names (seq (registry/get-keyset-commands binding-set))
            context (when command-names
                      (get-context {:binding binding
                                    :binding-vec binding-vec}))
            the-commands (when command-names
                           (->> (map #(get-command context %) command-names)
                                (filter contextual?)
                                (sort-by :priority reverse-compare)))
            _ (when the-commands
                (doseq [f (vals @-before-exec)]
                       (f)))
            results (when the-commands
                      ;; `take` with `filter` means we execute commands until one returns true, then stop
                      (take 1 (filter identity (map #(exec-command context %) the-commands))))
            prevent-default? (or (seq (filter identity results))
                                 (seq (filter :intercept? the-commands)))]
           (reset! WHICH_KEY_STATE (cond-> @WHICH_KEY_STATE
                                           (and (or prevent-default?
                                                    (seq the-commands))
                                                (not (empty? (set/difference (set binding-vec)
                                                                             registry/modifiers-internal)))) (clear-which-key)
                                           the-commands (assoc :last-exec-keys binding-vec)))
           (if prevent-default?
             false
             true)))

(vreset! registry/handler handler)
