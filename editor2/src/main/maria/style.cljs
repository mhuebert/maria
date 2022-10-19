(ns maria.style
  (:require ["@lezer/highlight" :refer [tags styleTags]]
            ["@codemirror/language" :as highlight]
            [applied-science.js-interop :as j]
            [clojure.string :as str]
            [shadow.resource :as rc]))

(def prose-element :div.relative)

(def tag-colors
  {:variableName "#98a14b"
   :operator "#c66"
   :bool "#987aa0"
   :null "#888"
   :keyword "#987aa0"
   :docString "#a78938"
   :comment "#a3685a"
   :number "#987aa0"
   :string "#a78938"
   :regexp "#a78938"})

(defn color-var [k] (str "--code-" (name k)))

(defn css-color-vars []
  (str
   ":root {"
   (->> tag-colors
        (map (fn [[k color]]
               (str (color-var k) ": " color ";")))
        (str/join \newline))
   "}"))

(def code-styles
  ;; map from token types (as defined in the grammar) to universally-defined
  ;; "tags" (defined by @lezer/highlight) which can have color applied.
  (-> (j/lit {["DefLike"
               "Operator"
               "NS"] :operator
              "Symbol" :variableName
              "Boolean" :bool
              ["Nil"
               "Discard!"] :null

              "Keyword" :keyword
              "DocString/..." :docString
              "Number" :number
              "LineComment" :comment
              "RegExp" :regexp
              ["Character"
               "StringContent"] :string})
      (j/extend! (clj->js {"\"\\\"\"" :string}))
      js/Object.entries
      (->> (reduce (fn [out [parser-type tag-name]]
                     (assoc out parser-type (j/get tags (name tag-name)))) {}))
      clj->js
      styleTags))

(def code-highlight-style
  (->> (keys tag-colors)
       (map (fn [tag-name]
              (j/obj :tag (j/!get tags tag-name)
                     :color (str "var(" (color-var tag-name) ")")
                     ;; for dev
                     :tag-name (name tag-name))))
       into-array
       (.define highlight/HighlightStyle)))

(def code-theme
  (j/lit {".cm-content" {:padding "1rem"
                         :white-space "pre-wrap"
                         :max-width "100%"}}))

(def tailwind
  [:style {:type "text/tailwindcss"}
   (str
    (rc/inline "nextjournal.viewer.css")

    (css-color-vars)
    (rc/inline "maria.cloud.css")
    )])