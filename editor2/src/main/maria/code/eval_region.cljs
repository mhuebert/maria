(ns maria.code.eval-region
  (:require
   ["@codemirror/state" :as state :refer [StateEffect StateField]]
   ["@codemirror/view" :as view :refer [EditorView Decoration keymap]]
   ["w3c-keyname" :refer [keyName]]
   [applied-science.js-interop :as j]
   [nextjournal.clojure-mode.util :as u]
   [nextjournal.clojure-mode.node :as n]
   [clojure.string :as str]))

(defn uppermost-edge-here
  "Returns node or its highest ancestor that starts or ends at the cursor position."
  [pos node]
  (or (->> (iterate n/up node)
           (take-while (every-pred (complement n/top?)
                                   #(or (= pos (n/end %) (n/end node))
                                        (= pos (n/start %) (n/start node)))))
           (last))
      node))

(defn main-selection [state]
  (-> (j/call-in state [:selection :asSingle])
      (j/get-in [:ranges 0])))

(defn node-at-cursor
  ([state] (node-at-cursor state (j/get (main-selection state) :from)))
  ([^js state from]
   (some->> (n/nearest-touching state from -1)
            (#(when (or (n/terminal-type? (n/type %))
                        (<= (n/start %) from)
                        (<= (n/end %) from))
                (cond-> %
                        (or (n/top? %)
                            (and (not (n/terminal-type? (n/type %)))
                                 (< (n/start %) from (n/end %))))
                        (-> (n/children from -1) first))))
            (uppermost-edge-here from)
            (n/balanced-range state))))

(defn top-level-node [state]
  (->> (n/nearest-touching state (j/get (main-selection state) :from) -1)
       (iterate n/up)
       (take-while (every-pred identity (complement n/top?)))
       last))

;; Modifier field
(defonce modifier-effect (.define StateEffect))
(defonce modifier-field
         (.define StateField
                  (j/lit {:create (constantly {})
                          :update (fn [value ^js tr]
                                    (or (some-> (first (filter #(.is ^js % modifier-effect) (.-effects tr)))
                                                (j/get :value))
                                        value))})))

(defn get-modifier-field [^js state] (.field state modifier-field))

(j/defn set-modifier-field! [^:js {:as view :keys [dispatch state]} value]
  (dispatch #js{:effects (.of modifier-effect value)
                :userEvent "evalregion"}))

(j/defn mark [spec ^:js {:keys [from to]}]
  (-> (.mark Decoration spec)
      (.range from to)))

(defn single-mark [spec range]
  (.set Decoration #js[(mark spec range)]))


(defn cursor-range [^js state]
  (if (.. state -selection -main -empty)
    (node-at-cursor state)
    (.. state -selection -main)))

(defonce region-field
         (let [bg (fn [color] (j/lit {:attributes {:style (str "background-color: " color ";")}}))
               mark:none (bg "transparent")
               mark:selected (bg "rgba(0, 243, 255, 0.14)")]
           (.define StateField
                    (j/lit
                     {:create (constantly (.-none Decoration))
                      :update (j/fn [_value ^:js {:keys [state]}]
                                (let [mods (set (keys (get-modifier-field state)))]
                                  (if-some [[spec range] (when (n/within-program? state)
                                                           (case mods
                                                             (#{"Shift" "Enter"})
                                                             [mark:selected (top-level-node state)]

                                                             #{"Shift"}
                                                             [mark:none (j/lit {:from 0 :to (.. state -doc -length)})]

                                                             (#{"Meta"}
                                                              #{"Meta" "Enter"})
                                                             (when-let [range (or (u/guard (main-selection state) (complement (j/get :empty)))
                                                                                  (cursor-range state))]
                                                               [mark:selected range])
                                                             nil))]
                                    (single-mark spec range)
                                    (.-none Decoration))))}))))


(defn get-region-field [^js state] (.field state region-field))

(defn current-range
  "Range of eval-region"
  [^js state]
  (some-> (get-region-field state)
          (j/call :iter)
          (u/guard #(j/get % :value))))

(defn current-selection-str
  "String of eval-region or selection"
  [^js state]
  (u/range-str state (or (current-range state)
                         (.. state -selection -main))))

(defn extension
  "Maintains modifier-state-field, containing a map of {<modifier> true}, including Enter."
  [{:as opts :keys [on-enter]}]
  (prn :opts opts)
  (let [handle-enter (j/fn handle-enter [binding ^:js {:as view :keys [state]} _]
                       ;(j/log :handle-enter binding)
                       (let [mods (get-modifier-field state)]
                         (set-modifier-field! view (assoc mods "Enter" true))
                         (when on-enter
                           (some-> (current-selection-str state)
                                   (u/guard (complement str/blank?))
                                   on-enter)))
                       true)
        handle-key-event (j/fn [^:js {:as event :keys [altKey shiftKey metaKey controlKey type]}
                                ^:js {:as view :keys [state]}]
                           ;(prn :handle-key-event type (keyName event))
                           (let [prev (get-modifier-field state)
                                 next (cond-> {}
                                              altKey (assoc "Alt" true)
                                              shiftKey (assoc "Shift" true)
                                              metaKey (assoc "Meta" true)
                                              controlKey (assoc "Control" true)
                                              (and (= "keydown" type)
                                                   (= "Enter" (keyName event)))
                                              (assoc "Enter" true))]
                             (when (not= prev next)
                               (set-modifier-field! view next))
                             false))
        handle-backspace (j/fn [^:js {:as view :keys [state dispatch]}]
                           (j/let [^:js {:keys [from to]} (current-range state)]
                             (when (not= from to)
                               (dispatch (j/lit {:changes {:from from :to to :insert ""}
                                                 :annotations (u/user-event-annotation "delete")})))
                             true))]
    #js[region-field
        (.. EditorView -decorations (from region-field))
        modifier-field
        (.of keymap
             (j/lit [{:key "Meta-Backspace"
                      :run handle-backspace
                      :shift handle-backspace}
                     {:key "Enter"
                      :run (partial handle-enter "!Enter")
                      :shift (partial handle-enter "!Shift-Enter")}
                     ~@(for [mod ["Alt" "Meta" "Control"]]
                         (j/lit {:key (str mod "-Enter")
                                 :run (partial handle-enter (str mod "-Enter"))
                                 :shift (partial handle-enter (str "Shift-" mod "-Enter"))}))]))
        (.domEventHandlers view/EditorView
                           #js{:keydown handle-key-event
                               :keypress handle-key-event
                               :keyup   handle-key-event})]))

(defn cursor-node-string [^js state]
  (u/guard (some->> (node-at-cursor state)
                    (u/range-str state))
           (complement str/blank?)))

(defn top-level-string [^js state]
  (u/guard (some->> (top-level-node state)
                    (u/range-str state))
           (complement str/blank?)))
