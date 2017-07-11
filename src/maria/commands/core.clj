(ns maria.commands.core
  (:require [clojure.string :as string]))

(def str->modifier {"alt"       :option
                    "option"    :option

                    "meta"      :command
                    "ctrl"      :command
                    "control"   :command
                    "cmd"       :command
                    "command"   :command

                    "shift"     :shift

                    "enter"     :enter
                    "backspace" :backspace
                    "tab"       :tab
                    "esc"       :escape
                    "escape"    :escape
                    "space"     :space
                    "page_up"   :page-up
                    "page_down" :page-down
                    "left"      :left
                    "up"        :up
                    "down"      :down
                    "right"     :right})

(defn normalize-key [k]
  (if (= 1 (count k)) (string/upper-case k)
                      (or (str->modifier (string/lower-case k))
                          (throw (Error. (str "Invalid modifier key: " k))))))

(defn parse-key-pattern [patterns]
  (conj (->> (string/split patterns #"\s")
             (map (fn [s] (->> (string/split s #"[-+]")
                               (map normalize-key)
                               (set))))
             (interpose :keys)
             (vec)) :exec))

(defn spaced-name [k]
  (let [s (name k)]
    (str (string/upper-case (first s)) (string/replace (subs s 1) "-" " "))))

(defmacro defcommand [name-as-keyword
                      key-patterns
                      docstring & body]
  (let [normalized-key-patterns (if (string? key-patterns) [key-patterns] key-patterns)
        parsed-key-patterns (mapv parse-key-pattern normalized-key-patterns)
        commands 'maria.commands.core/commands
        mappings 'maria.commands.core/mappings
        form `(do (swap! ~commands assoc ~name-as-keyword {:name         ~name-as-keyword
                                                           :display-name ~(spaced-name name-as-keyword)
                                                           :doc          ~docstring
                                                           :key-pattern  ~normalized-key-patterns
                                                           :command      ~(if
                                                                            (vector? (first body))
                                                                            `(fn ~(name name-as-keyword) ~@body)
                                                                            (first body))})
                  (doseq [pattern# ~parsed-key-patterns]
                    (swap! ~mappings assoc-in pattern# ~name-as-keyword)))]
    form))