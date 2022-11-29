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
                  (j/lit {:create (constantly #{})
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


(defn cursor-range [^js state]
  (if (.. state -selection -main -empty)
    (node-at-cursor state)
    (.. state -selection -main)))

(defn deco-set
  ([] (.-none Decoration))
  ([spec range]
   (.set Decoration #js[(mark spec range)])))

(def eval-regions
  (let [bg (fn [color] (j/lit {:attributes {:style (str "background-color: " color ";")}}))
        selection-color "rgba(0, 243, 255, 0.14)"
        block:highlight (fn [state]
                          (deco-set (bg selection-color) #js{:from 0 :to (.. state -doc -length)}))
        block:no-highlight (fn [^js state]
                             (deco-set (bg "transparent") #js{:from 0 :to (.. state -doc -length)}))
        cursor-node:highlight (fn [state]
                                (when-let [range (or (u/guard (main-selection state) (complement (j/get :empty)))
                                                     (cursor-range state))]
                                  (deco-set (bg selection-color) range)))]
    {#{"Shift" "Enter"} block:highlight
     #{"Shift"} block:no-highlight
     #{"Meta"} cursor-node:highlight
     #{"Meta" "Enter"} cursor-node:highlight}))

(defonce region-field
         (let [bg (fn [color] (j/lit {:attributes {:style (str "background-color: " color ";")}}))
               mark:none (bg "transparent")
               mark:selected (bg "rgba(0, 243, 255, 0.14)")]
           (.define StateField
                    (j/lit
                     {:create (constantly (.-none Decoration))
                      :update (j/fn [_value ^:js {:keys [state]}]
                                (or (when (n/within-program? state)
                                      (when-let [f (eval-regions (get-modifier-field state))]
                                        (f state)))
                                    (deco-set)))}))))

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
  (let [handle-enter (j/fn handle-enter [binding ^:js {:as view :keys [state]} _]
                       ;(j/log :handle-enter binding)
                       (let [mods (conj (get-modifier-field state) "Enter")
                             handled? (contains? eval-regions mods)]
                         (set-modifier-field! view mods)
                         (when on-enter
                           (some-> (current-selection-str state)
                                   (u/guard (complement str/blank?))
                                   on-enter))
                         handled?))
        handle-key-event (j/fn [^:js {:as event :keys [altKey shiftKey metaKey controlKey type]}
                                ^:js {:as view :keys [state]}]
                           ;(prn :handle-key-event type (keyName event))
                           (let [prev (get-modifier-field state)
                                 next (cond-> #{}
                                              altKey (conj "Alt")
                                              shiftKey (conj "Shift")
                                              metaKey (conj "Meta")
                                              controlKey (conj "Control")
                                              (and (= "keydown" type)
                                                   (= "Enter" (keyName event)))
                                              (conj "Enter"))]
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
                               :keyup handle-key-event})]))

(defn cursor-node-string [^js state]
  (u/guard (some->> (node-at-cursor state)
                    (u/range-str state))
           (complement str/blank?)))

(defn top-level-string [^js state]
  (u/guard (some->> (top-level-node state)
                    (u/range-str state))
           (complement str/blank?)))
