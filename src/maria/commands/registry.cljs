(ns maria.commands.registry
  (:require [clojure.set :as set]
            [maria.editor :as editor]
            [goog.events :as events]
            [goog.object :as gobj]
            [cljs.pprint :refer [pprint]]
            [re-db.d :as d]
            [clojure.string :as string]
            [re-view-material.icons :as icons]
            [goog.events.KeyCodes :as KeyCodes])
  (:import [goog.events KeyCodes]))

(defonce commands (atom {}))
(defonce mappings (atom {}))

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

(defn normalize-keycode [key-code]
  (let [key-code (KeyCodes/normalizeKeyCode key-code)]
    (get {91 17} key-code key-code)))

(defn init-listeners []
  (let [clear-keys #(d/transact! [[:db/add :commands :modifiers-down #{}]
                                  [:db/add :commands :which-key/active? false]])
        which-key-delay 500
        handle-keydown (fn [e]
                         (let [keycode (normalize-keycode (.-keyCode e))
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
    (events/listen js/window "keydown" handle-keydown true)

    (events/listen js/window "mousedown"
                   (fn [e]
                     (let [keycode (button->code (.-button e))]
                       (when-let [command-name (get-keyset-command (conj (d/get :commands :modifiers-down) keycode))]
                         (let [{:keys [command name]} (get @commands command-name)]
                           (when (some-> editor/current-editor (command))
                             (.preventDefault e)))))))

    (events/listen js/window "keyup"
                   (fn [e]
                     (let [keycode (normalize-keycode (.-keyCode e))
                           modifier? (modifiers keycode)]
                       (when modifier?
                         (doseq [timeout (d/get :commands :timeouts)]
                           (js/clearTimeout timeout))
                         (d/transact! [[:db/update-attr :commands :modifiers-down disj keycode]])
                         (when (empty? (d/get :commands :modifiers-down))
                           (d/transact! [[:db/add :commands :which-key/active? false]]))))))

    (events/listen js/window #js ["blur" "focus"] #(when (= (.-target %) (.-currentTarget %))
                                                     (clear-keys)))))

(defn get-hints [keys-pressed]
  (keep (fn [[keyset results]]
          (when (set/subset? keys-pressed keyset)
            (when (:exec results)                           ;; take off this condition later for multi-step keysets
              {:keyset  keyset
               :results results}))) @mappings))


(defonce _ (init-listeners))