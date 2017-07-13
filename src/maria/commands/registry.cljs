(ns maria.commands.registry
  (:require [clojure.set :as set]
            [goog.object :as gobj]
            [cljs.pprint :refer [pprint]]
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

(def symbols {"CTRL"                 (if mac? "⌘" "^")
              "COMMAND"              (if mac? "⌘" "^")
              "SHIFT"                "⇧"
              "ALT"                  "⎇"
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
       (interpose :keys)
       (vec)))

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

(defn get-keyset-commands [keys-pressed]
  (get-in @mappings [keys-pressed :exec]))

(defn normalize-keycode [key-code]
  (let [key-code (KeyCodes/normalizeKeyCode key-code)]
    (get {91 17} key-code key-code)))





