(ns maria.commands.registry
  (:require [goog.object :as gobj]
            [cljs.pprint :refer [pprint]]
            [clojure.string :as string]
            [goog.events.KeyCodes :as KeyCodes])
  (:import [goog.events KeyCodes]))

(defonce commands (atom {}))
(defonce mappings (atom {}))

(def mouse-icon (-> [:svg {:fill "currentColor", :height "14", :view-box "0 0 24 24", :width "14", :xmlns "http://www.w3.org/2000/svg"}
                     [:path {:d "M0 0h24v24H0z", :fill "none"}]
                     [:path {:d "M13 1.07V9h7c0-4.08-3.05-7.44-7-7.93zM4 15c0 4.42 3.58 8 8 8s8-3.58 8-8v-4H4v4zm7-13.93C7.05 1.56 4 4.92 4 9h7V1.07z"}]]
                    (with-meta (name "z"))))

(defn endkey->keycode [k]
  (let [k (string/upper-case k)
        k (get {"1"            "ONE"
                "2"            "TWO"
                "3"            "THREE"
                "4"            "FOUR"
                "5"            "FIVE"
                "6"            "SIX"
                "7"            "SEVEN"
                "8"            "EIGHT"
                "9"            "NINE"
                "["            "OPEN_SQUARE_BRACKET"
                "]"            "CLOSE_SQUARE_BRACKET"
                "/"            "BACKSLASH"
                \\             "SLASH"
                "'"            "APOSTROPHE"
                ","            "COMMA"
                "."            "PERIOD"
                "="            "EQUALS"
                "+"            "PLUS"
                "-"            "DASH"
                ";"            "SEMICOLON"
                "`"            "TILDE"
                "CLICK"        0
                "CLICK_MIDDLE" 1
                "CLICK_RIGHT"  2} k k)]
    (gobj/get KeyCodes k k)))

(defn modifier->keycode [k]
  (->> (case (string/upper-case k)
         ("CMD"
           "M"
           "META"
           "COMMAND") "META"

         ("CTRL"
           "CONTROL") "CTRL"

         ("ALT"
           "A"
           "OPT"
           "OPTION") "ALT"

         ("SHIFT"
           "S") "SHIFT")
       (gobj/get KeyCodes)))

(defn normalize-keyset-string [patterns]
  (->> (string/split patterns #"\s")
       (mapv (fn [s]
               (let [keys (string/split s #"[-+]")]
                 (conj (set (mapv modifier->keycode (drop-last keys)))
                       (endkey->keycode (last keys))))))))

(def code->symbol
  (->> {(.-CTRL KeyCodes)                 "⌃"
        (.-META KeyCodes)                 "⌘"
        (.-SHIFT KeyCodes)                "⇧"
        (.-ALT KeyCodes)                  "⎇"
        (.-ENTER KeyCodes)                "⏎"
        (.-BACKSPACE KeyCodes)            "⌫"
        (.-LEFT KeyCodes)                 "←"
        (.-TAB KeyCodes)                  "⇥"
        (.-RIGHT KeyCodes)                "→"
        (.-UP KeyCodes)                   "↑"
        (.-DOWN KeyCodes)                 "↓"
        (.-HOME KeyCodes)                 "↖"
        (.-END KeyCodes)                  "↘"
        (.-OPEN_SQUARE_BRACKET KeyCodes)  "["
        (.-CLOSE_SQUARE_BRACKET KeyCodes) "]"
        (.-BACKSLASH KeyCodes)            "/"
        (.-SLASH KeyCodes)                \\
        (.-APOSTROPHE KeyCodes)           "'"
        (.-COMMA KeyCodes)                ","
        (.-PERIOD KeyCodes)               "."
        (.-EQUALS KeyCodes)               "="
        (.-PLUS_SIGN KeyCodes)            "+"
        (.-DASH KeyCodes)                 "-"
        (.-SEMICOLON KeyCodes)            ";"
        (.-TILDE KeyCodes)                "`"
        0                                 mouse-icon
        1                                 mouse-icon
        2                                 mouse-icon}))

(def modifiers #{(.-META KeyCodes)
                 (.-CTRL KeyCodes)
                 (.-ALT KeyCodes)
                 (.-SHIFT KeyCodes)})

(defn show-key [key-code]
  (or (get code->symbol key-code)
      (if (string? key-code)
        key-code
        (.fromCharCode js/String key-code))))

(defn get-keyset-commands [keys-pressed]
  (get-in @mappings [keys-pressed :exec]))

(def normalize-keycode KeyCodes/normalizeKeyCode)





