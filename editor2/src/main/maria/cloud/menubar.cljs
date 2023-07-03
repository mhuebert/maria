(ns maria.cloud.menubar
  (:require ["jszip" :as jszip]
            ["file-saver" :as file-saver]
            ["@radix-ui/react-menubar" :as menu :refer [Item Separator Root Menu Trigger Portal Content]]
            ["@radix-ui/react-avatar" :as ava]
            [applied-science.js-interop :as j]
            [clojure.string :as str]
            [maria.cloud.sidebar :as sidebar]
            [maria.editor.core]
            [maria.editor.icons :as icons]
            [maria.cloud.github :as gh]
            [maria.ui :as ui]
            [yawn.view :as v]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Components

(def item
  (v/from-element menu/Item
    {:class ["flex items-center px-2 py-1 my-1 text-sm cursor-pointer rounded"
             "data-[disabled]:cursor-default data-[disabled]:text-zinc-400"
             "data-[highlighted]:outline-none data-[highlighted]:bg-sky-500 data-[highlighted]:text-white"]}))

(def icon-btn
  (v/from-element :div.cursor-pointer.p-1.flex.items-center.m-1))

(def shortcut
  (v/from-element :div.RightSlot.text-zinc-500.tracking-widest))

(def trigger
  (v/from-element menu/Trigger
    {:class ["px-1 py-1 my-[2px] bg-transparent hover:bg-zinc-200 rounded"
             "data-[highlighted]:bg-zinc-200"
             "data-[state=open]:bg-zinc-200"]}))

(def content
  (v/from-element menu/Content
    {:class "MenubarContent mt-[4px]"}))

(def separator (v/x [:el menu/Separator {:class [ui/c:divider "mx-2"]}]))

(defn menu [title & children]
  (v/x
   [:el menu/Menu
    [trigger title]
    [:el menu/Portal (into [content] children)]]))

(v/defview avatar [photo-url display-name]
  (let [initials (->> (str/split display-name #"\s+")
                      (map first)
                      (take 2)
                      str/join)]
    [:el ava/Root {:class ["inline-flex items-center justify-center align-middle"
                           "overflow-hidden select-none w-6 h-6 rounded-full bg-zinc-300"]}
     [:el ava/Image {:src photo-url}]
     [:el ava/Fallback {:delayMs 600
                        :class "text-xs font-bold text-zinc-700"} initials]]))

(def button-small-med
  (v/from-element :a
    {:class ["rounded flex items-center px-2 py-1 shadow cursor-pointer text-sm"
             ui/c:button-med]}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Menubar

(def clerk-deps-edn
  "deps.edn String for Clerkified Maria doc"
  "{:paths [\"dev\" ;; <-- automatically loads user.clj on start-up, launching Clerk
         \"src\"]
 :deps {org.clojure/clojure {:mvn/version \"1.11.1\"}
        io.github.nextjournal/clerk {:mvn/version \"0.13.842\"}
        io.github.applied-science/shapes {:git/sha \"da44031cf79a649932cb502f17388db23f2b8ace\"}
        io.github.applied-science/clerk-helpers {:git/sha \"3e42e4d73cc557170c8dbf3f4e5a39cec644a2e2\"}}}")

(def clerk-user-clj
  "user.clj String, per Clerk instructions"
  "(require '[nextjournal.clerk :as clerk])

;; start Clerk's built-in webserver on the default port 7777, opening the browser when done
(clerk/serve! {:browse? true
               ;;:watch-paths [\"notebooks\"]
               })

;; call `clerk/show!` explicitly to show a given notebook
(clerk/show! \"notebooks/clerkified_maria.clj\")")

(def clerk-ns-form
  "Namespace form String for Clerkified Maria doc"
  "^{:nextjournal.clerk/visibility {:code :hide, :result :hide}}
(ns clerkified-maria
  (:require
   ;; It's often bad form to bring in all vars from multiple lib
   ;; namespaces. Here we do it so that code from Maria works as-is. See
   ;; https://github.com/bbatsov/clojure-style-guide#prefer-require-over-use
   [applied-science.shapes :refer :all]
   [applied-science.clerk-helpers :refer :all]
   [nextjournal.clerk :as clerk]))

^{::clerk/visibility {:code :hide, :result :hide}}
(clerk/add-viewers! [{:pred shape?
                      ;; Make Clerk \"present\" Shapes as they are shown with Maria:
                      :transform-fn (clerk/update-val (comp clerk/html
                                                            to-hiccup))}])")

(defn download-clerkified-zip
  "Creates & downloads ZIP file of current editor view, packaged up to run locally with NextJournal's Clerk.
  Approach adapted from https://stackoverflow.com/a/49836948/706499"
  [_e]
  (-> (new jszip)
      (.file "deps.edn" clerk-deps-edn)
      (.file "dev/user.clj" clerk-user-clj)
      ;; TODO fn taking ns & file name from doc info?
      (.file "notebooks/clerkified_maria.clj"
             (str clerk-ns-form
                  "\n\n"
                  (-> @maria.editor.core/!mounted-view
                      (j/get-in [:state :doc])
                      maria.editor.core/doc->clj)))
      (.generateAsync #js{:type "blob"})
      (.then (fn [content]
               (file-saver/saveAs content "clerkified-maria.zip")))))

(ui/defview menubar []
  [:<>
   [:div {:style {:height 40}}]
   [:div.w-100.fixed.top-0.right-0.flex.items-center.shadow.px-2.text-sm.z-50.bg-neutral-50
    {:style {:height 40
             :left (sidebar/sidebar-width)}}
    (when-not (:sidebar/visible? @ui/!state)
      [icon-btn {:on-click #(swap! ui/!state update :sidebar/visible? not)}
       [icons/bars3 "w-4 h-4"]])
    [:el Root {:class "flex flex-row w-full items-center"}
     [menu "File"
      [item "New" [shortcut "⌘N"]]
      [item "Duplicate"]
      separator
      [item "Revert"]
      separator
      [item {:disabled (not (gh/token))} "Save" [shortcut "⌘S"]]
      [item {:on-click download-clerkified-zip}
       "Download for Clerk"]]
     [menu "View"
      [item {:on-click #(swap! ui/!state update :sidebar/visible? not)} "Sidebar" [shortcut "⇧⌘K"]]
      [item "Command Bar" [shortcut "⌘K"]]]
     [:div.flex-grow]
     [:div#menubar-title]
     [:div.flex-grow]
     (if-some [{:as user :keys [photo-url display-name]} @gh/!user]
       [:el Menu
        [:el Trigger {:class "cursor-pointer px-2"}
         [avatar photo-url display-name]]
        [:el Portal
         [:el Content {:class "MenubarContent mt-[4px]"}
          [item {:on-click #(gh/sign-out)} "Sign Out"]]]]
       [button-small-med
        {:on-click #(gh/sign-in-with-popup!)}
        "Sign In " [:span.hidden.md:inline.pl-1 " with GitHub"]])]]])

(defn title! [content]
  (v/portal :menubar-title (v/x content)))
