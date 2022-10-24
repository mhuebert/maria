(ns maria.style
  (:require ["@lezer/highlight" :refer [tags styleTags]]
            ["@codemirror/language" :as highlight]
            [applied-science.js-interop :as j]
            [clojure.string :as str]
            [shadow.resource :as rc]))

(def prose-element :div.relative)

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
  (->> ["variableName"
        "operator"
        "bool"
        "null"
        "keyword"
        "docString"
        "comment"
        "number"
        "string"
        "regexp"]
       (map (fn [tag-name]
              (j/obj :tag (j/!get tags tag-name)
                     :color (str "var(--code-" tag-name ")")
                     ;; for dev
                     :tag-name tag-name)))
       into-array
       (.define highlight/HighlightStyle)))

(def code-theme
  (j/lit {".cm-content" {:padding "1rem"
                         :white-space "pre-wrap"
                         :max-width "100%"}}))