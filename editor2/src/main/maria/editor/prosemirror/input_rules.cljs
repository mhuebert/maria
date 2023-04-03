(ns maria.editor.prosemirror.input-rules
  (:require ["prosemirror-inputrules" :as rules]
            ["prosemirror-model" :as model]
            [applied-science.js-interop.alpha :refer [js]]
            [maria.editor.prosemirror.schema :refer [schema]]))

;; An input rule defines a regular expression which, when matched on the current input,
;; invokes a command.

(js
  (def maria-rules*

    (let [{{:as nodes :keys [blockquote
                             ordered_list bullet_list
                             code_block
                             heading]} :nodes} schema]
      [~@rules/smartQuotes
       rules/ellipsis
       rules/emDash

       ;; type > for blockquote
       (rules/wrappingInputRule #"^\s*>\s$" blockquote)

       ;; type 1. or 2. etc. for ordered list
       (rules/wrappingInputRule #"^(\d+)\.\s$" ordered_list
                                (fn get-attrs
                                  [[_ match-order]]
                                  {:order match-order})
                                (fn join?
                                  [[_ match-order] {:as node
                                                    :keys [childCount]
                                                    {:keys [order]} :attr}]
                                  (= match-order (+ childCount order))))
       ;; type -, +, or * for a bullet list
       (rules/wrappingInputRule #"^\s*([-+*])\s$" bullet_list)

       ;; type ``` for a code block
       (rules/textblockTypeInputRule #"^```$" code_block)

       ;; type #, ##, ### etc. for h1, h2, h3 etc.
       (rules/textblockTypeInputRule #"^(#{1,6})\s$" heading
                                     (fn [[_ match]] {:level (count match)}))]))

  (def maria-rules
    (rules/inputRules {:rules maria-rules*})))
