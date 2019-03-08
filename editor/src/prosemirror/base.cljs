(ns prosemirror.base
  (:require ["prosemirror-inputrules" :as input-rules]
            ["prosemirror-history" :refer [history]]
            ["prosemirror-keymap" :as keymap]
            ["prosemirror-commands" :as commands]
            ["prosemirror-state" :as state :refer [EditorState]]
            [chia.view :as v]
            [chia.view.legacy :as vlegacy]
            [applied-science.js-interop :as j]
            [prosemirror.core :as pm]
            [clojure.spec.alpha :as s]))

;; todo
;; Editor accepts :default-value and :value but is not an ordinary controlled component.
;; Behaving as a controlled component could be problematic, because serializing on every
;; change would slow down editing on large documents (but be consistent w/ React inputs).
;; *INTERIM MEASURE* - a CHANGED :value prop will replace the current editor state, but
;; passing the same value does not prevent edits to the local editor state.

(s/def ::input-rules vector?)

(vlegacy/defspec ::doc
                 "A prosemirror doc"
                 object?)

(vlegacy/defspec ::serialize
                 "Should convert a ProseMirror doc to Markdown."
                 fn?)

(vlegacy/defspec ::parse
                 "Should convert a Markdown string ot a ProseMirror doc."
                 fn?)

(vlegacy/defspec ::schema
                 "a ProseMirror schema"
                 #(and (j/contains? % :nodes)
                       (j/contains? % :marks)))

(vlegacy/defspec ::on-dispatch
                 "(this, EditorView) - called after every update."
                 fn?)

(vlegacy/defspec ::editor-view-props
                 "Passed to the EditorView constructor."
                 map?)

(vlegacy/defspec ::keymap
                 "Merged as the highest-priority keymap (http://prosemirror.net/docs/ref/#keymap)."
                 map?)

(vlegacy/defspec ::default-value
                 "the initial editor state."
                 string?)

(vlegacy/defspec ::value
                 "Behaves differently from ordinary React controlled inputs. When a *new/different* :value is passed, it replaces the current doc, but continuing to pass the same :value does not freeze local state."
                 string?)

(vlegacy/defview Editor
  "A ProseMirror editor view."
  {#_#_:spec/props (s/keys :opt-un
                           [::input-rules
                            ::doc
                            ::serialize
                            ::parse
                            ::schema
                            ::on-dispatch
                            ::editor-view-props
                            ::keymap
                            ::default-value
                            ::value])
   :view/did-mount (fn [{:keys [value
                                default-value
                                on-dispatch
                                view/state
                                editor-view-props
                                input-rules
                                plugins
                                parse
                                doc
                                schema]
                         user-keymap :keymap
                         :as this}]
                     (let [editor-state (.create EditorState
                                                 #js {"doc" (or doc (parse (or value default-value "")))
                                                      "schema" schema
                                                      "plugins" (cond-> [(history)
                                                                         (input-rules/inputRules
                                                                          #js {:rules (to-array (into input-rules
                                                                                                      input-rules/allInputRules))})]
                                                                        user-keymap (conj (keymap/keymap (clj->js user-keymap)))
                                                                        plugins (into plugins)
                                                                        false (conj (keymap/keymap commands/baseKeymap))
                                                                        true (to-array))})
                           editor-view (-> (vlegacy/dom-node this)
                                           (pm/EditorView. (clj->js (merge editor-view-props
                                                                           {:state editor-state
                                                                            :spellcheck false
                                                                            :attributes {:class "outline-0"}
                                                                            :dispatchTransaction
                                                                            (fn [tr]
                                                                              (let [^js/pm.EditorView pm-view (get @state :pm-view)
                                                                                    prev-state (.-state pm-view)]
                                                                                (pm/transact! pm-view tr)
                                                                                (when-not (nil? on-dispatch)
                                                                                  (on-dispatch this pm-view prev-state))))}))))]
                       (set! (.-reView editor-view) this)
                       (reset! state {:pm-view editor-view})))

   :view/did-update (fn [{value :value
                          doc :doc
                          {prev-value :value
                           prev-doc :doc} :view/prev-props
                          :as this}]
                      (when (or (and value
                                     (not= value prev-value))
                                (and doc
                                     (not= doc prev-doc)))
                        (.resetDoc this (or doc value))))
   :view/will-unmount (fn [{:keys [view/state]}]
                        (pm/destroy! (:pm-view @state)))}
  [this]
  [:.prosemirror-content
   (-> (:view/props this)
       (select-keys [:class :style])
       (assoc :dangerouslySetInnerHTML {:__html ""}))])

(vlegacy/extend-view Editor
                     Object
                     (resetDoc [{:keys [view/state parse schema]} new-value]
                               (let [view (:pm-view @state)]
                                 (.updateState view
                                               (.create EditorState #js {"doc" (cond-> new-value
                                                                                       (string? new-value) (parse))
                                                                         "schema" schema
                                                                         "plugins" (aget view "state" "plugins")}))))
                     (pmView [{:keys [view/state]}]
                             (:pm-view @state))
                     (serialize [{:keys [view/state serialize]}]
                                (some-> (:pm-view @state)
                                        (j/get-in [:state :doc])
                                        (serialize))))

