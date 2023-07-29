(ns maria.editor.prosemirror.input-rules
  (:require ["prosemirror-state" :as pm.state]
            ["prosemirror-commands" :as pm.cmd]
            ["prosemirror-inputrules" :as rules]
            ["prosemirror-model" :as model]
            [applied-science.js-interop :as j]
            [applied-science.js-interop.alpha :refer [js]]
            [maria.editor.prosemirror.schema :refer [schema]]))

;; An input rule defines a regular expression which, when matched on the current input,
;; invokes a command.


(js
  (defn toggle-mark-tr [state mark-name attrs]
    (let [sel (.-selection state)
          mark-type (j/get-in schema [:marks (name mark-name)])
          empty (.-empty sel)
          $cursor (.-$cursor sel)
          the-mark (.create mark-type attrs)
          the-node (or (.-node sel) (.-parent (.-$from sel)))
          tr (.-tr state)]
      (when (and $cursor
                 (not (or (and empty (not $cursor))
                          (not (.-inlineContent the-node))
                          (not (.allowsMarks (.-type the-node) #js [the-mark])))))
        (if (.isInSet mark-type (or (.-storedMarks state) (.marks $cursor)))
          (.removeStoredMark tr mark-type)
          (.addStoredMark tr the-mark))))))

(js
  (def maria-rules*

    (let [{{:as nodes :keys [blockquote
                             ordered_list bullet_list
                             code_block
                             heading]} :nodes} schema]
      [~@rules/smartQuotes
       (rules/InputRule. #"[^`\\]+`$"
                         (fn [state match start end]
                           (toggle-mark-tr state :code nil)))
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

       (rules/InputRule. #"^\($"
                         (fn [^js state match start end]
                           (let [^js $start (.. state -doc (resolve start))]
                             (when (.. $start
                                       (node -1)
                                       (canReplaceWith
                                         (.index $start -1)
                                         (.indexAfter $start -1)
                                         code_block))
                               (doto (.-tr state)
                                 (.delete start end)
                                 (.setBlockType start end code_block nil)
                                 (.insertText "()" start)
                                 (as-> tr
                                       (.setSelection tr (.create pm.state/TextSelection (.-doc tr) (inc start)))))))))

       ;; type #, ##, ### etc. for h1, h2, h3 etc.
       (rules/textblockTypeInputRule #"^(#{1,6})\s$" heading
                                     (fn [[_ match]] {:level (count match)}))]))


  (def maria-rules
    (rules/inputRules {:rules maria-rules*})))
