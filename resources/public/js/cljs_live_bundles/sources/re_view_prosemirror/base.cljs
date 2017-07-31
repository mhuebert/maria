(ns re-view-prosemirror.base
  (:require [re-view.core :as v :refer [defview]]
            [re-view-prosemirror.core :as pm]))

;; todo
;; Editor accepts :default-value and :value but is not an ordinary controlled component.
;; Behaving as a controlled component could be problematic, because serializing on every
;; change would slow down editing on large documents (but be consistent w/ React inputs).
;; *INTERIM MEASURE* - a CHANGED :value prop will replace the current editor state, but
;; passing the same value does not prevent edits to the local editor state.


(def EditorProps [:serialize :parse :schema
                  :on-mount :on-dispatch
                  :value :default-value])
(defview Editor
         "A ProseMirror editor view."
         {:spec/props        {:serialize         {:spec :Function
                                                  :doc  "Should convert a ProseMirror doc to Markdown."}
                              :parse             {:spec :Function
                                                  :doc  "Should convert a Markdown string ot a ProseMirror doc."}
                              :on-mount          {:spec :Function
                                                  :doc  "(this, EditorView) - called after mount."}
                              :on-dispatch       {:spec :Function
                                                  :doc  "(this, EditorView) - called after every update."}
                              :editor-view-props {:spec :Map
                                                  :doc  "Passed to the EditorView constructor."}
                              :keymap            {:spec :Object
                                                  :doc  "Merged as the highest-priority keymap (http://prosemirror.net/docs/ref/#keymap)."}
                              :default-value     {:spec :String
                                                  :doc  "Parsed as the initial editor state."
                                                  :pass true}
                              :value             {:spec :String
                                                  :doc  "Behaves differently from ordinary React controlled inputs. When a *new/different* :value is passed, it replaces the current doc, but continuing to pass the same :value does not freeze local state."
                                                  :pass true}}
          :view/did-mount    (fn [{:keys [value
                                          default-value
                                          on-dispatch
                                          on-mount
                                          view/state
                                          editor-view-props
                                          keymap
                                          serialize
                                          parse
                                          schema] :as this}]
                               (let [editor-state (.create pm/EditorState
                                                           #js {"doc"     (parse (or value default-value ""))
                                                                "schema"  schema
                                                                "plugins" (-> [(.history pm/history)
                                                                               (pm/keymap-markdown schema)
                                                                               (pm/input-rules schema)
                                                                               (pm/keymap pm/keymap-base)]
                                                                              (cond-> keymap (conj (pm/user-keymap keymap)))
                                                                              (to-array))})
                                     editor-view (-> (v/dom-node this)
                                                     (pm/EditorView. (clj->js (merge editor-view-props
                                                                                     {:state      editor-state
                                                                                      :attributes {:class "outline-0"}
                                                                                      :dispatchTransaction
                                                                                                  (fn [tr]
                                                                                                    (let [^js/pm.EditorView pm-view (get @state :pm-view)]
                                                                                                      (pm/transact! pm-view tr)
                                                                                                      (when-not (nil? on-dispatch)
                                                                                                        (on-dispatch this pm-view))))}))))]
                                 (set! (.-reView editor-view) this)
                                 (reset! state {:pm-view editor-view})
                                 (when-not (nil? on-mount)
                                   (on-mount this editor-view))))
          :view/did-update   (fn [{value               :value
                                   {prev-value :value} :view/prev-props
                                   :keys               [view/state parse schema]}]
                               (when (and value (not= value prev-value))
                                 (let [view (:pm-view @state)]
                                   (.updateState view (.create pm/EditorState #js {"doc"     (parse value)
                                                                                   "schema"  schema
                                                                                   "plugins" (aget view "state" "plugins")})))))
          :view/will-unmount (fn [{:keys [view/state]}]
                               (pm/destroy! (:pm-view @state)))
          :serialize         (fn [{:keys [view/state serialize]}]
                               (when-let [doc (some-> (:pm-view @state)
                                                      (aget "state" "doc"))]
                                 (serialize doc)))}
         [{:keys [view/props] :as this}]
         [:.prosemirror-content.ph3.cf
          (-> (apply dissoc props EditorProps)
              (assoc :dangerouslySetInnerHTML {:__html ""}))])

