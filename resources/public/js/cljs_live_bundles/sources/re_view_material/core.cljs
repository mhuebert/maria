(ns re-view-material.core
  (:refer-clojure :exclude [List])
  (:require
    [goog.net.XhrIo :as xhr]
    [goog.events :as events]
    [goog.dom :as gdom]
    [goog.style :as gstyle]
    [goog.object :as gobj]
    [re-view.core :as v :refer [defview]]
    [re-view-material.icons :as icons]
    [re-view-material.util :as util]
    [clojure.string :as string]
    [re-view-routing.core :as routing]
    [re-view.view-spec :refer [defspecs]]
    [re-view.core :as v]
    [re-view.util :refer [update-attrs]]
    [re-view-material.mdc :as mdc]
    [re-view-material.ext :as ext])
  (:import [goog Promise]))



(defspecs {::color         {:spec #{:primary :accent}
                            :doc  "Specifies color variable from theme."}
           ::raised        {:spec :Boolean
                            :doc  "Raised buttons gain elevation, and color is applied to background instead of text."}
           ::ripple        {:spec    :Boolean
                            :doc     "Enables ripple effect on click/tap"
                            :default true}
           ::compact       {:spec :Boolean
                            :doc  "Reduces horizontal padding"}
           ::auto-focus    {:spec :Boolean
                            :doc  "If true, focuses element on mount"}


           ::id            :String
           ::dirty         {:spec :Boolean
                            :doc  "If true, field should display validation errors"}
           ::dense         {:spec :Boolean
                            :doc  "Reduces text size and vertical padding"}
           ::disabled      {:spec         :Boolean
                            :doc          "Disables input element or button"
                            :pass-through true}
           ::label         {:spec :Element
                            :doc  "Label for input element or button"}
           ::on-change     :Function
           ::rtl           {:spec :Boolean
                            :doc  "Show content in right to left."}
           ::value         {:spec :String
                            :doc  "Providing a value causes an input component to be 'controlled'"}
           ::default-value {:spec :String
                            :doc  "For an uncontrolled component, sets the initial value"}})

(defview Ripple
  "Applies ripple effect to a single child view."
  {:spec/children      [:Hiccup]
   :key                (fn [_ element]
                         (or (get-in element [1 :key])
                             (get-in element [1 :id])))
   :life/did-mount     #(mdc/init % mdc/Ripple)
   :life/should-update #(do true)
   :life/did-update    (mdc/mdc-style-update :Ripple "rippleTarget")
   :life/will-unmount  #(mdc/destroy % mdc/Ripple)}
  [{:keys [view/state]} hiccup-element]
  (update-attrs hiccup-element update :classes into (:mdc/Ripple-classes @state)))

(defview Button
  "Communicates the action that will occur when the user touches it. [More](https://material.io/guidelines/components/buttons.html)"
  {:key        :label
   :spec/props {:props/keys [::color ::compact ::dense ::disabled ::label ::raised ::ripple]
                :icon       :SVG
                :icon-end   :SVG}}
  [{:keys [href
           on-click
           label
           icon
           icon-end
           disabled
           dense
           raised
           compact
           ripple
           color
           classes]
    :as   this}]
  (when (some #{:secondary :type} (set (keys (:props this))))
    (throw "Depracated :secondary, :type"))
  (let [only-icon? (and icon (contains? #{nil ""} label))]
    ((if ripple Ripple identity)
      [(if (and (not disabled)
                href) :a :button)
       (-> (v/pass-props this)
           (cond-> disabled (dissoc :href :on-click)
                   only-icon? (update :style assoc :min-width "auto"))
           (update :style merge (when (or icon icon-end)
                                  {:display     "inline-flex"
                                   :align-items "center"}))
           (update :classes into (-> ["mdc-button"
                                      (when ripple "mdc-ripple-target")
                                      (when dense "mdc-button--dense")
                                      (when raised "mdc-button--raised")
                                      (when compact "mdc-button--compact")
                                      (when color (str "mdc-button--" (name color)))])))
       (when icon
         (cond-> (icons/style icon {:flex-shrink 0})
                 (not only-icon?) (icons/style {:margin-right "0.5rem"})
                 dense (icons/size 20)))
       [:span {:style {:flex-shrink 0}}
        (when-let [label (util/ensure-str label)]
          label)]
       (when icon-end
         (cond-> (icons/style icon-end {:margin-left "0.5rem"})
                 dense (icons/size 20)))])))

(defview Dialog
  {:spec/props         {:label-accept   :String
                        :label-cancel   :String
                        :scrollable?    :Boolean
                        :content-header :Element}
   :spec/children      [:& :Element]
   :life/initial-state {:mdc/styles {}}
   :life/did-mount     [#(mdc/init % mdc/Dialog)
                        (mdc/mdc-style-update :Dialog)]
   :life/did-update    (mdc/mdc-style-update :Dialog)
   :life/will-unmount  #(mdc/destroy % mdc/Dialog)
   :open               (fn [this]
                         (.open (gobj/get this "mdcDialog")))
   :close              (fn [this]
                         (.close (gobj/get this "mdcDialog")))
   }
  [{:keys [label-accept
           label-cancel
           view/state
           scrollable?
           content-header]
    :or   {label-accept "OK"
           label-cancel "Cancel"}} & body]
  [:aside#mdc-dialog.mdc-dialog
   {:classes          (:mdc/Dialog-classes @state)
    :role             "alertdialog"
    :aria-hidden      "true"
    :aria-labelledby  "mdc-dialog-label"
    :aria-describedby "mdc-dialog-body"}
   [:.mdc-dialog__surface
    (some->> content-header (conj [:header.mdc-dialog__header]))
    (into [:section#mdc-dialog-body.mdc-dialog__body
           {:class (when scrollable? "mdc-dialog__body--scrollable")}]
          body)
    [:footer.mdc-dialog__footer
     (Button {:classes ["mdc-dialog__footer__button"
                        "mdc-dialog__footer__button--cancel"]
              :label   label-cancel})
     (Button {:classes ["mdc-dialog__footer__button"
                        "mdc-dialog__footer__button--accept"]
              :color   :primary
              :label   label-accept})]]
   [:.mdc-dialog__backdrop]])

(def DialogWithTrigger (ext/with-trigger Dialog))

(defview Input
  {:spec/props              {:element {:doc     "Base element type"
                                       :spec    #{:input :textarea}
                                       :default :input}
                             :mask    {:spec :Function
                                       :doc  "Function to restrict input"}}
   :life/initial-state      #(get % :value)
   :life/will-receive-props (fn [{{prev-value :value} :view/prev-props
                                  {value :value}      :view/props
                                  state               :view/state :as this}]
                              (when-not (= prev-value value)
                                (reset! state value)))
   :life/did-mount          (fn [{:keys [auto-focus] :as this}]
                              (when (true? auto-focus) (-> (v/dom-node this)
                                                           (util/closest (fn [^js/Element el]
                                                                           (#{"INPUT" "TEXTAREA"} (aget el "tagName"))))
                                                           (.focus))))}
  [{:keys [view/props
           view/state
           element
           on-change
           mask
           on-key-press
           auto-focus
           class] :as this}]
  [element (cond-> (-> (v/pass-props this)
                       (update :classes conj "outline-0"))
                   (contains? props :value)
                   (merge
                     {:value        (or @state "")
                      :on-key-press (fn [e]
                                      (when (and mask (= (mask (util/keypress-value e)) @state))
                                        (.preventDefault e))
                                      (when on-key-press (on-key-press e)))
                      :on-change    (fn [e]
                                      (binding [re-view.render-loop/*immediate-state-update* true]
                                        (let [target (.. e -target)
                                              value (.. target -value)
                                              new-value (cond-> value
                                                                mask (mask))
                                              cursor (.. target -selectionEnd)]
                                          (when mask (aset target "value" new-value))
                                          (when on-change (on-change e))
                                          (doto target
                                            (aset "selectionStart" cursor)
                                            (aset "selectionEnd" cursor)))))}))])

(defview Select
  "Native select element"
  {:spec/children [:& :Element]}
  [{:keys [view/props]} & items]
  (into [:select.mdc-select props] items))

(defview Text
  "Allow users to input, edit, and select text. [More](https://material.io/guidelines/components/text-fields.html)"
  {:key                :name
   :spec/props         {:props/keys           [::label ::dense ::auto-focus ::dirty]
                        :floating-label       {:spec    :Boolean
                                               :default true}
                        :help-text-persistent :Boolean
                        :multi-line           :Boolean
                        :full-width           :Boolean
                        :expandable           :Boolean

                        :hint                 :String
                        :error                :String
                        :info                 :String
                        :placeholder          {:spec         :String
                                               :pass-through true}

                        :on-save              :Function
                        :in-progress          :Boolean
                        :input-styles         :Map
                        :container-props      :Map
                        :field-props          :Map

                        :value                {:spec         ::value
                                               :pass-through true}
                        :default-value        {:spec         ::default-value
                                               :pass-through true}}
   :life/initial-state {:dirty                 false
                        :mdc/Textfield-classes #{"mdc-textfield--upgraded"}
                        :mdc/label-classes     #{"mdc-textfield__label"}
                        :mdc/help-classes      #{"mdc-textfield-helptext"}}
   :life/did-mount     (fn [this]
                         (mdc/init this mdc/Textfield))
   :life/will-unmount  (fn [this]
                         (mdc/destroy this mdc/Textfield))
   :reset              #(swap! (:view/state %) assoc :dirty false)}
  [{:keys [id
           label
           floating-label
           help-text-persistent
           dense
           multi-line
           full-width
           expandable
           focused
           dirty
           hint
           error
           info
           on-save
           in-progress
           input-styles
           container-props
           field-props

           value
           default-value

           view/props
           view/state
           ] :as this}]
  (let [{:keys [focused
                mdc/Textfield-classes
                mdc/label-classes
                mdc/help-classes
                mdc/help-attrs]} @state
        dirty (or (:dirty @state) dirty)

        field-id (or id name)]
    [:div container-props
     [:.mdc-textfield
      (update field-props :classes into (into Textfield-classes
                                              [(when multi-line "mdc-textfield--multiline")
                                               (when (:disabled props) "mdc-textfield--disabled")
                                               (when full-width "mdc-textfield--fullwidth")
                                               (when (:dense props) "mdc-textfield--dense")]))
      (Input
        (-> (v/pass-props this)
            (cond-> full-width (assoc :aria-label label))
            (merge {:element       (if multi-line :textarea :input)
                    :class         "w-100 mdc-textfield__input"
                    :aria-controls (str field-id "-help")
                    :id            field-id
                    :style         input-styles}
                   (util/collect-handlers props {:on-key-down (util/handle-on-save on-save)}))))
      (when (and (not full-width) (util/ensure-str label))
        (let [floatingLabel (boolean (or (:floating-label props)
                                         (util/ensure-str value)
                                         (util/ensure-str default-value)
                                         (util/ensure-str (:placeholder props))))]
          [:label {:class    (cond-> (string/join " " label-classes)
                                     floatingLabel (str " mdc-textfield__label--float-above"))
                   :html-for field-id}
           label]))
      #_(when in-progress (ProgressIndeterminate {:class "absolute w-100"}))]

     (let [error (seq (util/collect-text (:error props)))
           info (seq (util/collect-text (:info props)))
           hint (seq (util/collect-text (:hint props)))]
       [:div (merge {:class (cond-> (string/join " " help-classes)
                                    (or help-text-persistent
                                        error
                                        info) (str " mdc-textfield-helptext--persistent"))
                     :id    (str field-id "-help")}
                    help-attrs)
        (some->> error (conj [:.dark-red.pb1]))
        (some->> info (conj [:.black.pb1]))
        hint])]))


(defn Submit [label]
  (Button
    {:type  :raised
     :color :primary
     :class "w-100 f4 mv3 pv2"
     :label label
     :style {:height "auto"}}))


(defview List
  "Presents multiple line items vertically as a single continuous element. [More](https://material.io/guidelines/components/lists.html)"
  {:spec/props    {:props/keys  [::dense]
                   :avatar-list {:spec :Boolean
                                 :doc  "Adds modifier class to style start-detail elements as large, circular 'avatars'"}}
   :spec/children [:& :Element]}
  [{:keys [dense avatar-list] :as this} & items]
  [:div (-> (v/pass-props this)
            (update :classes into ["mdc-list"
                                   (when dense "mdc-list--dense")
                                   (when avatar-list "mdc-list--avatar-list")]))
   items])

(defview ListItem
  "Collections of list items present related content in a consistent format. [More](https://material.io/guidelines/components/lists.html#lists-behavior)"
  {:key        (fn [{:keys [href text-primary]}] (or href text-primary))
   :spec/props {:props/keys     [::ripple]
                :text-primary   :Element
                :text-secondary :Element
                :detail-start   :Element
                :detail-end     :Element
                :dense          :Boolean}}
  [{:keys [text-primary
           text-secondary
           detail-start
           detail-end
           href
           ripple
           dense
           view/props] :as this}]
  (when-let [dep (some #{:title :body :avatar} (set (keys props)))]
    (throw (js/Error. "Deprecated " dep "... title, body - primary/text-secondary, avatar - detail-start or detail-end")))
  (let [el (if href :a :div)]
    (cond-> [el (-> (v/pass-props this)
                    (update :classes into ["mdc-list-item"
                                           (when ripple "mdc-ripple-target")])
                    (cond-> dense (update :style merge {:font-size   "1.1rem"
                                                        :height      40
                                                        :line-height "40px"
                                                        :padding     "0 8px"})))
             (some->> detail-start (conj [:.mdc-list-item__start-detail]))
             [:.mdc-list-item__text
              (when dense {:style {:font-size "80%"}})
              text-primary
              [:.mdc-list-item__text__secondary text-secondary]]
             (some->> detail-end (conj [:.mdc-list-item__end-detail]))]
            ripple (Ripple))))

(defn ListDivider []
  [:.mdc-list-divider {:role "presentation"}])

(defn ListGroup [& content]
  (into [:.mdc-list-group] content))

(defn ListGroupSubheader [content]
  [:.mdc-list-group__subheader content])

(defview SimpleMenu
  "Menus appear above all other in-app UI elements, and appear on top of the triggering element. [More](https://material.io/guidelines/components/menus.html#menus-behavior)"
  {:key               :id
   :spec/props        {:on-cancel   :Function
                       :on-selected :Function
                       :open-from   #{:bottom-right :bottom-left :top-right :top-left}}
   :spec/children     [:& :Element]
   :life/did-mount    (fn [this] (mdc/init this mdc/SimpleMenu))
   :life/will-unmount (fn [this] (mdc/destroy this mdc/SimpleMenu))
   :open              (fn [this] (.open (gobj/get this "mdcSimpleMenu")))
   :life/did-update   [(mdc/mdc-style-update :SimpleMenu "menuItemContainer")
                       (mdc/mdc-style-update :SimpleMenu)]}
  [{:keys [view/state view/props classes open-from] :as this} & items]
  [:.mdc-simple-menu (merge (v/pass-props this)
                            {:tab-index -1
                             :class  (when open-from (str "mdc-simple-menu--open-from-" (name open-from)))
                             :classes   (into classes (:mdc/SimpleMenu-classes @state))})
   (into [:.mdc-simple-menu__items.mdc-list
          {:role        "menu"
           :aria-hidden true}]
         items)])

(def SimpleMenuWithTrigger (ext/with-trigger SimpleMenu {:container-classes ["mdc-menu-anchor"]}))

(def SimpleMenuItem (v/partial ListItem
                               {:role      "menuitem"
                                :tab-index 0}))


(defn- formField-attrs [{:keys [mdc/FormField-classes align-end rtl field-classes]}]
  {:classes (cond-> (into field-classes FormField-classes)
                    align-end (conj "mdc-form-field--align-end"))
   :dir     (when rtl "rtl")})

(defview Switch
  "Allow a selection to be turned on or off. [More](https://material.io/guidelines/components/selection-controls.html#selection-controls-radio-button)"
  {:key        :id
   :spec/props {:props/keys [::rtl ::color]
                :align-end  :Keyword}}
  [{:keys [disabled rtl label color] :as this}]
  (let [color-class (case color :primary "mdc-theme--primary-bg"
                                :accent "mdc-theme--accent-bg"
                                nil)]
    [:.mdc-switch
     (cond-> {}
             disabled (assoc :class "mdc-switch--disabled")
             rtl (assoc :dir "rtl"))
     [:input.mdc-switch__native-control (assoc (v/pass-props this) :type "checkbox")]
     [:.mdc-switch__background
      {:class color-class}
      [:.mdc-switch__knob {:class color-class}]]]))

(defview SwitchField
  "Allow a selection to be turned on or off. [More](https://material.io/guidelines/components/selection-controls.html#selection-controls-radio-button)"
  {:key        :id
   :spec/props {:props/keys    [::label ::rtl]
                :id            {:spec         ::id
                                :pass-through true
                                :required     true}
                :field-classes :Vector
                :align-end     :Boolean}}
  [{:keys [id label view/props field-classes rtl align-end] :as this}]
  [:.mdc-form-field
   (cond-> {:classes (cond-> []
                             field-classes (into field-classes)
                             align-end (conj "mdc-form-field--align-end"))}
           rtl (assoc :dir "rtl"))
   (Switch (v/pass-props this))
   [:label {:for   id
            :style {:margin "0 0.25rem"}} label]])



(defview Checkbox
  "Allow the selection of multiple options from a set. [More](https://material.io/guidelines/components/selection-controls.html#)"
  {:key               :id
   :spec/props        {:props/keys [::disabled
                                    ::dense
                                    ::value
                                    ::rtl
                                    ::label
                                    ::id]
                       :checked    :Boolean
                       :align-end  :Boolean}
   :life/did-mount    #(mdc/init % mdc/Ripple mdc/Checkbox mdc/FormField)
   :life/will-unmount #(mdc/destroy % mdc/Ripple mdc/Checkbox mdc/FormField)
   :life/did-update   (mdc/mdc-style-update :Ripple)}
  [{:keys [id name label view/props view/state
           dense
           label-class
           input-class] :as this}]
  (when (contains? props :label-class) (throw "label-class in Checkbox not supported"))
  (let [{:keys [mdc/Checkbox-classes
                mdc/Ripple-classes]} @state]
    [:.mdc-form-field
     (formField-attrs props)
     [:.mdc-checkbox.mdc-ripple-target
      {:classes                      (into Checkbox-classes Ripple-classes)
       :data-mdc-ripple-is-unbounded true
       :style                        (when dense {:margin "-0.5rem 0"})}
      [:input.mdc-checkbox__native-control (-> (v/pass-props this)
                                               (merge {:type  "checkbox"
                                                       :class input-class
                                                       :id    (or id name)}))]
      [:div.mdc-checkbox__background
       [:svg.mdc-checkbox__checkmark
        {:view-box "0 0 24 24"}
        [:path.mdc-checkbox__checkmark__path
         {:fill   "none"
          :stroke "white"
          :d      "M1.73,12.91 8.1,19.28 22.79,4.59"}]]
       [:.mdc-checkbox__mixedmark]]]
     (when label
       (-> label
           (cond->> (string? label) (conj [:label]))
           (update-attrs #(-> %
                              (assoc :html-for (or id name))
                              (update :classes conj label-class)))))]))

#_(defview FormField
    {:key               (fn [_ _ {:keys [id]} _] id)
     :life/did-mount    #(mdc/init % mdc/FormField)
     :life/will-unmount #(mdc/destroy % mdc/FormField)}
    [{:keys [rtl align-end]} field label]
    [:.mdc-form-field
     {:class (when align-end "mdc-form-field--align-end")
      :dir   (when rtl "rtl")}
     field
     (update-attrs label assoc :html-for (v/element-get field :id))])

#_(defn CheckboxField [{:keys [id rtl label align-end] :as props}]
    (FormField (select-keys props [:rtl :align-end])
               (Checkbox (dissoc props :rtl :align-end :label))
               [:label label])
    #_[:.mdc-form-field
       {:class (when align-end "mdc-form-field--align-end")
        :dir   (when rtl "rtl")}
       (Checkbox (dissoc props :label :rtl :align-end))
       (when label [:label {:html-for id} label])])
(def PermanentDrawerToolbarSpacer [:.mdc-permanent-drawer__toolbar-spacer])
(defview PermanentDrawer
  "Permanent navigation drawers are always visible and pinned to the left edge, at the same elevation as the content or background. They cannot be closed. The recommended default for desktop. [More](https://material.io/guidelines/patterns/navigation-drawer.html#navigation-drawer-behavior)"
  {:spec/children [:& :Element]}
  [this & content]
  [:.mdc-permanent-drawer
   (v/pass-props this)
   (into [:.mdc-permanent-drawer__content] content)])

(defn TemporaryDrawerHeader [& content]
  [:.mdc-temporary-drawer__header
   (into [:.mdc-temporary-drawer__header-content] content)])

(defview TemporaryDrawer
  "Slides in from the left and contains the navigation destinations for your app. [More](https://material.io/guidelines/patterns/navigation-drawer.html)"
  {:life/did-mount          (fn [{:keys [open? view/state] :as this}]
                              (mdc/init this mdc/TemporaryDrawer)
                              (swap! state assoc :route-listener (routing/listen (aget this "close") {:fire-now? false}))
                              (when open? (.open this)))
   :life/will-receive-props (fn [{open? :open? {prev-open? :open?} :view/prev-props :as this}]
                              (cond (and open? (not prev-open?)) (.open this)
                                    (and prev-open? (not open?)) (.close this)))
   :life/will-unmount       #(do (mdc/destroy % mdc/TemporaryDrawer)
                                 (routing/unlisten (:route-listener @(:view/state %))))
   :life/did-update         (mdc/mdc-style-update :TemporaryDrawer "drawer")
   :foundation              (fn [this]
                              (let [^js/mdc.Foundation foundation (gobj/get this "mdcTemporaryDrawer")]
                                foundation))
   :open                    (fn [this] (.open (.foundation this)))
   :close                   (fn [this] (.close (.foundation this)))
   :notifyOpen              (fn [{:keys [on-open]}] (when on-open (on-open)))
   :notifyClose             (fn [{:keys [on-close]}] (when on-close (on-close)))
   :spec/props              {:toolbar-spacer? :Boolean
                             :header-content  :Element
                             :open?           :Boolean
                             :on-open         :Function
                             :on-close        :Function}
   :spec/children           [:& :Element]
   }
  [{:keys      [toolbar-spacer? header-content view/state]
    list-props :view/props
    :as        this} & list-items]
  [:.mdc-temporary-drawer
   {:class (string/join " " (:mdc/TemporaryDrawer-classes @state))}
   [:.mdc-temporary-drawer__drawer
    (when header-content
      [:.mdc-temporary-drawer__header
       [:.mdc-temporary-drawer__header-content header-content]])
    (apply List (-> (v/pass-props this)
                    (update :classes conj "mdc-temporary-drawer__content")) list-items)]])

(def TemporaryDrawerWithTrigger (ext/with-trigger TemporaryDrawer))

(def update-toolbar-styles
  (v/compseq (mdc/mdc-style-update :Toolbar "titleElement")
             (mdc/mdc-style-update :Toolbar "flexibleRowElement")
             (mdc/mdc-style-update :Toolbar "fixedAdjustElement")
             (mdc/mdc-style-update :Toolbar)))

(defn reset-toolbar
  "Toolbar is very stateful and must be totally reset if props change."
  [{:keys [view/state] :as this}]
  (v/swap-silently! state #(do {}))
  (update-toolbar-styles this)
  (mdc/destroy this mdc/Toolbar)
  (mdc/init this mdc/Toolbar))

(defview Toolbar
  "Toolbar."
  {:spec/props        {:fixed        {:doc  "Fixes the toolbar to top of screen."
                                      :spec #{true false :lastrow-only}}
                       :waterfall    {:spec :Boolean
                                      :doc  "On scroll, toolbar will gain elevation."}
                       :flexible     {:spec #{true false :default-behavior}
                                      :doc  "Toolbar starts out large, then shrinks gradually as user scrolls down."}
                       :rtl          ::rtl
                       :with-content {:spec :Boolean
                                      :doc  "If true, last child element will be rendered as sibling of Toolbar, with margin applied to adjust for fixed toolbar size."}}
   :spec/children     [:& :Element]
   :life/did-mount    #(mdc/init % mdc/Toolbar)
   :life/did-update   [update-toolbar-styles
                       (fn [{:keys [view/props
                                    view/prev-props
                                    view/children
                                    view/prev-children] :as this}]
                         (when (and (not= props prev-props))
                           (reset-toolbar this))
                         (when (not= children prev-children)
                           (let [foundation (aget this "mdcToolbar")
                                 set-key-heights (aget foundation "setKeyHeights_")]
                             (.call set-key-heights foundation))))]
   :life/will-unmount #(mdc/destroy % mdc/Toolbar)}
  [{:keys [view/state
           view/props
           fixed
           waterfall
           flexible
           rtl
           with-content] :as this} & body]
  (let [[toolbar-content sibling-content] (if with-content [(drop-last body) (last body)] [body nil])
        toolbar-props (-> (v/pass-props this)
                          (cond-> rtl (assoc :dir "rtl"))
                          (update :classes into (cond-> (:mdc/Toolbar-classes @state)
                                                        fixed (conj "mdc-toolbar--fixed")
                                                        (= fixed :lastrow-only) (conj "mdc-toolbar--fixed-lastrow-only")
                                                        waterfall (conj "mdc-toolbar--waterfall")
                                                        flexible (conj "mdc-toolbar--flexible")
                                                        (= flexible :default-behavior) (conj "mdc-toolbar--flexible-default-behavior"))))
        toolbar [:header.mdc-toolbar toolbar-props toolbar-content]]
    (if with-content
      [:span toolbar
       [:div {:class (when fixed "mdc-toolbar-fixed-adjust")} sibling-content]]
      toolbar)))

(def ToolbarWithContent
  (v/partial Toolbar {:react-keys {:display-name "material/ToolbarWithContent"}} {:with-content true}))


;(v/defelement [])
;; defines element - parses hiccup immediately. not a function, so no arguments. (returns plain element.)



(v/defview ToolbarSection
  "Toolbar section.

  :align (middle): :start or :end
  :shrink? (false): use for very long titles"
  ;; TODO
  ;; enable specs for functional components
  {:spec/props {:align          {:spec    #{:start :center :end}
                                 :default :center}
                :shrink-to-fit? :Boolean}}
  [{:keys [align shrink-to-fit?] :as this} & content]
  [:section.mdc-toolbar__section
   (-> (v/pass-props this)
       (assoc :role "toolbar")
       (update :classes into (cond-> []
                                     align (conj (case align :center ""
                                                             :end "mdc-toolbar__section--align-end"
                                                             :start "mdc-toolbar__section--align-start"))
                                     shrink-to-fit? (conj "mdc-toolbar__section--shrink-to-fit"))))
   content])

(v/defn ToolbarTitle [{:keys [href] :as props} title]

  [(if href :a :span)
   (update props :classes conj "mdc-toolbar__title") title])

(defn ToolbarRow [& content]
  (into [:.mdc-toolbar__row] content))

;; MDC select element needs work --  arrow does not line up.
;; wait until it has improved to implement.


;(def SelectItem (v/partial ListItem {:role      "option"
;                                     :tab-index 0}))
;(def ^:private SelectProps [:on-change])
;(defview Select
;  {:did-mount        #(mdc/init % mdc/Select)
;   :will-unmount     #(mdc/destroy % mdc/Select)
;   :getValue         (fn [this] (.getValue (gobj/get this "mdcSelect")))
;   :getSelectedIndex (fn [this] (.getSelectedIndex (gobj/get this "mdcSelect")))
;   :setSelectedIndex (fn [this] (.setSelectedIndex (gobj/get this "mdcSelect")))
;   :isDisabled       (fn [this] (.isDisabled (gobj/get this "mdcSelect")))
;   :setDisabled      (fn [this] (.setDisabled (gobj/get this "mdcSelect")))
;
;   :did-update       (fn [this]
;                       (.resize (gobj/get this "mdcSelect")))}
;  [{:keys [mdc/Select-classes view/props]}]
;  [:.mdc-select
;   (-> {:role      "listbox"
;        :tab-index 0}
;       (merge (apply dissoc props SelectProps))
;       (update :classes into Select-classes))
;   [:span.mdc-select__selected-text "Pick..."]
;   (SimpleMenu {:class "mdc-select__menu"}
;               )])



;; TODO
;; MDC Dialog
;; MDC Snackbar



;(defn Chip [{:keys [label
;                    class
;                    label-class
;                    icon-class
;                    on-click
;                    onDelete] :as props}]
;  [:div (merge {:class (str "br4 pv2 ph2 mt2 mr2 f65 dib inner-shadow bg-light-gray "
;                                 (when (or on-click on-click)
;                                  " pointer")
;                                 " "
;                                 class)}
;               (select-keys props [:style :on-click :on-click :key]))
;   [:span {:class (str "ma1 " label-class)} label]
;   (when onDelete
;     (update icons/Cancel 1 merge
;             {:class (str " o-60 hover-o-100 pointer mtn2 mbn2 mrn1 " icon-class)
;              :on-click   onDelete}))])
;
;(defview Avatar
;         {:key :src}
;         [{:keys [size style view/props] :as this
;           :or   {size 40}}]
;         [:img (-> props
;                   (assoc :style (merge {:width         size
;                                         :height        size
;                                         :border-radius "50%"}
;                                        style)))])
;
;(defn Divider [] [:hr.bt.mv2.b--light-gray {:style {:border-bottom "none"}}])
;
;(def Subheader :.gray.f6.w-100.ph3.pt3.pb1)
;
;(defview Paper
;         [_ & children]
;         (into [:.mdl-shadow--2dp.bg-white.pa3.br1] children))
;
;(defn Header [& content]
;  (into [:.flex.bg-blue.flex-none.white.items-center] content))
;
;(defn HeaderButton [{:keys [href] :as props} icon]
;  [(if href :a :div)
;   (update props :class #(str " pointer pa3 hover-bg-darken-1 " %))
;   icon])
;
;(defview ProgressIndeterminate
;         "ProgressIndeterminate..."
;         {:did-mount upgrade-component}
;         [{:keys [view/props]}]
;         [:.mdl-progress.mdl-js-progress.mdl-progress__indeterminate (assoc props :dangerouslySetInnerHTML {:__html ""})])
;
;(defview ProgressSpinner
;         "ProgressSpinner..."
;         {:did-mount upgrade-component}
;         [{:keys [view/props]}]
;         [:.mdl-spinner.mdl-js-spinner.is-active.mdl-spinner--single-color (assoc props :dangerouslySetInnerHTML {:__html ""})])
;
;(defview RadioButton
;         "RadioButton..."
;         {:key        :id
;          :did-mount  upgrade-component
;          :did-update (fn [this]
;                        (let [m (.. (v/dom-node this) -MaterialRadio)]
;                          (if (:checked this) (.check m) (.uncheck m))))}
;         [{:keys [label
;                  class
;                  label-class
;                  input-class
;                  checked
;                  id
;                  view/props]}]
;         [:label {:class (str "mdl-radio mdl-js-radio mdl-js-ripple-effect pl4 "
;                              class)
;                  :html-for   id}
;          [:input (merge {:type       "radio"
;                          :class (str "mdl-radio__button absolute left-0 "
;                                      input-class)}
;                         (cond-> (select-keys props [:name :id :value :default-checked])
;                                 checked (assoc :default-checked "checked")))]
;          [:span {:class (str "mdl-radio__label "
;                              label-class)} label]])
;
;


#_(defview Select
    "Select..."
    {:key               (fn [{:keys [id name]}] (or id name))
     :life/did-mount    (fn [this] (mdc/init this mdc/Select))
     :life/will-unmount (fn [this] (mdc/destroy this mdc/Select))
     :spec/props        {:label {:spec :String}}}
    [{:keys [label]}]
    [:.mdc-select
     {:role      "listbox"
      :tab-index 0}
     [:span.mdc-select__selected-text label]
     [:.mdc-simple-menu.mdc-select__menu]])