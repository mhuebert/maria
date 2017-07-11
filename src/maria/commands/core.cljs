(ns maria.commands.core
  (:require [clojure.set :as set]
            [magic-tree-codemirror.edit :as edit]
            [maria.editor :as editor]
            [goog.events :as events]
            [goog.object :as gobj]
            [cljs.pprint :refer [pprint]]
            [re-db.d :as d]
            [clojure.string :as string]
            [maria.eval :as eval]
            [magic-tree.core :as tree]
            [magic-tree-codemirror.util :as cm]
            [re-view-material.icons :as icons])
  (:require-macros [maria.commands.core :refer [defcommand]])
  (:import [goog.events KeyCodes KeyEvent KeyHandler]))

(def commands (atom {}))
(def mappings (atom {}))

(def mac? (let [platform (.. js/navigator -platform)]
            (or (string/starts-with? platform "Mac")
                (string/starts-with? platform "iP"))))

(def mouse-icon [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
                 [:path {:d "M0 0h24v24H0z", :fill "none"}]
                 [:path {:d "M13 1.07V9h7c0-4.08-3.05-7.44-7-7.93zM4 15c0 4.42 3.58 8 8 8s8-3.58 8-8v-4H4v4zm7-13.93C7.05 1.56 4 4.92 4 9h7V1.07z"}]])

(def symbols {"CONTROL"              (if mac? "⌘" "^")
              "COMMAND"              (if mac? "⌘" "^")
              "SHIFT"                "⇧"
              "OPTION"               "⎇"
              "ENTER"                "↩"
              "BACKSPACE"            "⌫"
              "FORWARD_DELETE"       "⌦"
              "LEFT"                 "←"
              "RIGHT"                "→"
              "UP"                   "↑"
              "DOWN"                 "↓"
              "HOME"                 "↖"
              "END"                  "↘"
              "CLICK"                (with-meta (icons/size mouse-icon 14)
                                                {:name "z"})
              "OPEN_SQUARE_BRACKET"  "["
              "CLOSE_SQUARE_BRACKET" "]"
              "BACKSLASH"            "/"
              "SLASH"                \\
              "APOSTROPHE"           "'"
              "COMMA"                ","
              "PERIOD"               "."
              "EQUALS"               "="
              "PLUS"                 "+"
              "DASH"                 "-"
              "SEMICOLON"            ";"
              "TILDE"                "`"})

(defn keystring->code [k]
  (let [k (string/upper-case k)
        k (get {"CMD"     "CTRL"
                "COMMAND" "CTRL"
                "CONTROL" "CTRL" #_"CTRL"
                "CTRL"    "CTRL"
                "OPT"     "ALT"
                "OPTION"  "ALT"
                "1"       "ONE"
                "2"       "TWO"
                "3"       "THREE"
                "4"       "FOUR"
                "5"       "FIVE"
                "6"       "SIX"
                "7"       "SEVEN"
                "8"       "EIGHT"
                "9"       "NINE"
                "["       "OPEN_SQUARE_BRACKET"
                "]"       "CLOSE_SQUARE_BRACKET"
                "/"       "BACKSLASH"
                \\        "SLASH"
                "'"       "APOSTROPHE"
                ","       "COMMA"
                "."       "PERIOD"
                "="       "EQUALS"
                "+"       "PLUS"
                "-"       "DASH"
                ";"       "SEMICOLON"
                "`"       "TILDE"} k k)]
    (or (gobj/get KeyCodes k)
        k)))

(defn button->code [n]
  (case n 0 "CLICK"
          2 "CLICK_RIGHT"
          1 "CLICK_MIDDLE"))

(defn normalize-keyset-string [patterns]
  (->> (string/split patterns #"\s")
       (map (fn [s] (->> (string/split s #"[-+]")
                         (map keystring->code)
                         (set))))
       (interpose :keys)))

(def code->symbol
  (reduce-kv (fn [m k sym]
               (assoc m (gobj/get KeyCodes k k) sym)) {} symbols))

(defn show-key [key-code]
  (or (get code->symbol key-code)
      (if (string? key-code)
        key-code
        (.fromCharCode js/String key-code))))

(def modifiers #{(.-META KeyCodes)
                 (.-CTRL KeyCodes)
                 (.-ALT KeyCodes)
                 (.-SHIFT KeyCodes)})

(defn get-keyset-command [keys-pressed]
  (get-in @mappings [keys-pressed :exec]))

(defn event-keycode [e]
  (let [key-code (.-keyCode e)]
    (get {91 17} key-code key-code)))

;; google closure KeyHandler does not support holding down the meta key & pressing multiple keys.
;; so we keep track of when the meta key is 'stuck', and track this manually.
;; probably leads to inconsistencies in some browsers.
(def meta-key-down? false)
(def meta-key-stuck? false)
(defn init-meta-key-management []
  (let [clear! #(do (set! meta-key-down? false)
                    (set! meta-key-stuck? false))
        meta? #(= (.-keyCode %) (.-META KeyCodes))
        meta-is-down! (fn []
                        (set! meta-key-down? true)
                        (events/listenOnce js/document "keydown" #(when meta-key-down? (set! meta-key-stuck? true)) true))]

    (events/listen js/document "keydown" #(when (meta? %) (meta-is-down!)) true)
    (events/listen js/document "keyup" #(when (meta? %) (clear!)) true)
    (events/listen js/document "blur" clear!)))


(defn init-listeners []
  (let [clear-keys #(do (set! meta-key-down? false)
                        (set! meta-key-stuck? false)
                        (d/transact! [[:db/add :commands :modifiers-down #{}]
                                      [:db/add :commands :which-key/active? false]]))
        which-key-delay 500
        key-handling-element (new KeyHandler js/document true)
        handle-key-event (fn [e]
                           (let [keycode (event-keycode e)
                                 keys-down (d/get :commands :modifiers-down)
                                 modifier? (contains? modifiers keycode)]
                             (if-let [command-name (get-keyset-command (conj keys-down keycode))]
                               (let [{:keys [command]} (get @commands command-name)]
                                 (some-> editor/current-editor (command))
                                 (.stopPropagation e)
                                 (.preventDefault e))
                               (when modifier?
                                 (d/transact! [[:db/update-attr :commands :modifiers-down conj keycode]
                                               [:db/update-attr :commands :timeouts conj (js/setTimeout #(let [keys-down (d/get :commands :modifiers-down)]
                                                                                                           (when (and (seq keys-down)
                                                                                                                      (not= keys-down #{(keystring->code "shift")}))
                                                                                                             (d/transact! [[:db/add :commands :which-key/active? true]]))) which-key-delay)]])))))]

    (clear-keys)

    (events/listen key-handling-element "key" handle-key-event)
    (events/listen js/window "keydown" #(when meta-key-stuck?
                                          (handle-key-event %)) true)

    (events/listen js/window "mousedown"
                   (fn [e]
                     (let [keycode (button->code (.-button e))]
                       (when-let [command-name (get-keyset-command (conj (d/get :commands :modifiers-down) keycode))]
                         (let [{:keys [command name]} (get @commands command-name)]
                           (when (some-> editor/current-editor (command))
                             (.preventDefault e)))))))

    (events/listen js/window "keyup"
                   (fn [e]
                     (let [keycode (event-keycode e)
                           modifier? (modifiers keycode)]
                       (when modifier?
                         (doseq [timeout (d/get :commands :timeouts)]
                           (js/clearTimeout timeout))
                         (d/transact! [[:db/update-attr :commands :modifiers-down disj keycode]])
                         (when (empty? (d/get :commands :modifiers-down))
                           (d/transact! [[:db/add :commands :which-key/active? false]]))))))

    (events/listen js/window #js ["blur" "focus"] #(when (= (.-target %) (.-currentTarget %))
                                                     (clear-keys)))))

(defonce _ (do (init-listeners)
               (init-meta-key-management)))

(defn get-hints [keys-pressed]
  (keep (fn [[keyset results]]
          (when (set/subset? keys-pressed keyset)
            (when (:exec results)                           ;; take off this condition later for multi-step keysets
              {:keyset  keyset
               :results results}))) @mappings))

(defcommand :copy/form
            ["Cmd-C"]
            ""
            edit/copy-form)

(defcommand :cut/line
            ["Ctrl-K"]
            "Cut to end of line / node"
            edit/kill)

(defcommand :cut/form
            ["Cmd-X"]
            "Cuts current highlight"
            edit/cut-form)

(defcommand :delete/form
            ["Cmd-Backspace"]
            "Deletes current highlight"
            edit/delete-form)

(defcommand :cursor/hop-left
            ["Alt-Left"]
            "Move cursor left one form"
            edit/hop-left)

(defcommand :cursor/hop-right
            ["Alt-Right"]
            "Move cursor right one form"
            edit/hop-right)

(defcommand :selection/expand
            ["Cmd-]" "Cmd-1"]
            "Select parent form, or form under cursor"
            edit/expand-selection)

(defcommand :selection/shrink
            ["Cmd-[" "Cmd-2"]
            "Select child of current form (remembers :expand-selection history)"
            edit/shrink-selection)

(defcommand :comment/line
            ["Cmd-/"]
            "Comment the current line"
            edit/comment-line)

(defcommand :comment/uneval-form
            ["Cmd-;"]
            ""
            edit/uneval-form)

(defcommand :comment/uneval-top-level-form
            ["Cmd-Shift-;"]
            ""
            edit/uneval-top-level-form)

(defcommand :slurp
            ["Shift-Cmd-K"]
            ""
            edit/slurp)

(defn eval-editor [cm scope]
  (let [traverse (case scope :top-level tree/top-loc
                             :bracket identity)]
    (when-let [source (or (cm/selection-text cm)
                          (->> cm
                               :magic/cursor
                               :bracket-loc
                               (traverse)
                               (tree/string (:ns @eval/c-env))))]

      (d/transact! [[:db/update-attr :repl/state :eval-log (fnil conj []) (assoc (eval/eval-str source)
                                                                            :id (d/unique-id)
                                                                            :source source)]]))))

(defcommand :eval/top-level
            ["Shift-Cmd-Enter"]
            "Evaluate the top-level form"
            (fn [editor] (eval-editor editor :top-level)))

(defcommand :eval/form
            ["Cmd-Enter"]
            "Evaluate the current form"
            (fn [editor] (eval-editor editor :bracket)))

(defcommand :eval/on-click
            ["Option-Click"]
            "Evaluate the clicked form"
            (fn [editor]
              (eval-editor editor :bracket)
              true))

;; icons for shift, command, option

;; ctrl/command
;; alt/option
;; shift

;; navigator.platform
;; isMac == navigator.platform.indexOf('Mac') > -1
;; https://stackoverflow.com/questions/10527983/best-way-to-detect-mac-os-x-or-windows-computers-with-javascript-or-jquery
;; Mac*  MacIntel
;; iOs - iP* iPad, iPhone, iPod

