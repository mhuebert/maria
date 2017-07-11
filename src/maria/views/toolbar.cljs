(ns maria.views.toolbar
  (:require [re-view.core :as v :refer [defview]]
            [maria.views.text :as text]
            [maria.frames.communication :as frame]
            [re-view-material.icons :as icons]
            [maria.persistence.github :as github]
            [re-db.d :as d]
            [re-view-material.core :as ui]
            [clojure.string :as string]
            [cljs.core.match :refer-macros [match]]))

(def send (partial frame/send frame/trusted-frame))

(defn add-clj-ext [s]
  (when s
    (cond-> s
            (not (re-find #"\.clj[cs]?$" s)) (str ".cljs"))))

(defn strip-clj-ext [s]
  (some-> s
          (string/replace #"\.clj[cs]?$" "")))

(defn user-menu []
  (ui/SimpleMenuWithTrigger
    {:open-from         :top-left
     :container-classes ["mdc-menu-anchor" "flex" "items-stretch"]}
    [:.flex.items-center.b.pointer.ph2.hover-bg-near-white (d/get :auth-public :username)]
    (ui/SimpleMenuItem {:text-primary "My gists"
                        :href         (str "/gists/" (d/get :auth-public :username))
                        :dense        true})
    (ui/SimpleMenuItem {:text-primary "Sign out"
                        :on-click     #(send [:auth/sign-out])
                        :dense        true})))

(def toolbar-button :.pa2.flex.items-center)
(v/defn toolbar-item [props [action icon]]
  [toolbar-button (merge {:on-click action}
                         (cond-> props
                                 action (update :classes conj "pointer hover-bg-near-white"))) (-> icon
                                                                                                   (icons/size 20)
                                                                                                   (icons/class "gold"))])

(def toolbar-text :.f7.gray.no-underline.pa2.pointer.hover-underline.flex.items-center)

(defview toolbar
  {:life/did-mount          #(.updateWindowTitle %)
   :update-window-title     (fn [{:keys [filename]}]
                              (frame/send frame/trusted-frame [:window/set-title filename]))
   :life/will-receive-props (fn [{filename                  :filename
                                  {prev-filename :filename} :view/prev-props
                                  :as                       this}]
                              (when-not (= filename prev-filename)
                                (.updateWindowTitle this)))}
  [{{:keys [persisted local]} :project
    :keys                     [filename get-editor id owner view/state] :as this}]

  (let [signed-in? (d/get :auth-public :signed-in?)

        {:keys [maria-url] :as owner} (cond owner owner
                                            persisted (:owner persisted)
                                            :else (d/entity :auth-public))
        owned-by-current-user? (= (str (:id owner)) (d/get :auth-public :id))
        current-filename (or (get-in local [:files filename :filename]) filename)

        {local-content  :content
         local-filename :filename} (get-in local [:files filename])

        persisted-id (:id persisted)
        persisted-content (get-in persisted [:files filename :content])
        persist-mode (match [(boolean persisted) owned-by-current-user?]
                            [true true] :publish
                            [true false] :fork
                            [false _] :create)
        persist-icon (case persist-mode (:publish :create) icons/Backup
                                        :fork icons/ContentDuplicate)
        persist! #(send (case persist-mode
                          :publish [:project/publish persisted-id {:files {filename {:filename (or local-filename filename)
                                                                                     :content  (or local-content persisted-content)}}}]
                          :create [:project/create (d/get "new" :local)]
                          :fork [:project/fork persisted-id]))
        update-filename #(d/transact! [[:db/update-attr id :local (fn [local]
                                                                    (assoc-in local [:files filename] {:filename %
                                                                                                       :content  (or local-content persisted-content)}))]])
        new-file #(let [{:keys [title-input]} @state]
                    (github/clear-new!)
                    (.focus title-input)
                    (when get-editor
                      (some-> (get-editor)
                              (.setValue "")))
                    (frame/send frame/trusted-frame [:window/navigate "/new" {}]))
        sufficient-content-to-save? (and (not (empty? (or local-content persisted-content)))
                                         (not (empty? (some-> (or local-filename filename)
                                                              (strip-clj-ext)))))
        unsaved-changes? (and filename
                              (contains? (:files local) filename)
                              (or (and local-content (not= local-content persisted-content))
                                  (and local-filename (not= local-filename filename)))
                              (not (re-find #"^\s*$" local-content)))]
    [:.bb.b--light-gray.flex.sans-serif.f6.items-stretch.flex-none.br.b--light-gray.f7.flex-none.relative
     [:.ph2.flex-auto.flex.items-center
      [:.pl2.flex.items-center
       [:a.hover-underline.gray.no-underline {:href maria-url} (:username owner)]
       [:.ph1.gray "/"]
       (when filename
         (-> (text/autosize-text {:auto-focus  true
                                  :ref         #(when % (swap! state assoc :title-input %))
                                  :value       (strip-clj-ext current-filename)
                                  :on-key-down #(when (= 13 (.-which %))
                                                  (persist!))
                                  :placeholder "Enter a title..."
                                  :on-change   #(update-filename (add-clj-ext (.-value (.-target %))))})
             ))]

      (when (and filename signed-in?)
        (if (= :fork persist-mode)
          (toolbar-item [persist! persist-icon])
          (list
            (if (and sufficient-content-to-save? unsaved-changes?)
              (toolbar-item [persist! persist-icon])
              (toolbar-item {:class "o-50"} [nil persist-icon ""]))
            (when (and persisted unsaved-changes?)
              (toolbar-item [#(do (d/transact! [[:db/add persisted-id :local persisted]])
                                  (.setValue (get-editor) persisted-content)) icons/Undo])))))]

     (toolbar-item [new-file icons/Add "New namespace"])
     (if signed-in? (user-menu)
                    [toolbar-text {:on-click #(frame/send frame/trusted-frame [:auth/sign-in])} "Sign in with GitHub"])]))