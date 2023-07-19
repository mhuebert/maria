(ns maria.editor.code.completions
  (:require ["@codemirror/autocomplete" :as a]
            ["@codemirror/view" :as cm.view]
            [applied-science.js-interop.alpha :refer [js]]
            [applied-science.js-interop :as j]
            [clojure.core :as c]
            [edamame.core :as edamame]
            [maria.editor.code.commands :as commands]
            [maria.editor.util :as u]
            [nextjournal.clojure-mode.node :as n]
            [sci.core :as sci]
            [sci.impl.namespaces :as sci.ns]))

(def symbol-nodes #{"Operator"
                    "DefLike"
                    "NS"
                    "Symbol"})


(j/defn completions [NodeView ^:js {:keys [state explicit pos]}]
  (when-let [node (some-> (n/nearest-touching state pos -1)
                          (u/guard #(or explicit (= (n/end %) pos)))
                          (u/guard (comp symbol-nodes n/name)))]

    (j/let [^:js {:keys [from to]} (n/from-to node)
            text (.sliceDoc state from pos)]
      ;; clojure.core/x        ns: fully qualified, looks in the-ns publics
      ;; c/x                   ns: alias, looks in the-ns publics
      ;; x                     no ns, look at whole current-ns
      (when-let [sym (try (edamame/parse-string text)
                          (catch js/Error e nil))]
        (let [ctx @(j/get-in NodeView [:ProseView :!sci-ctx])
              current-ns (commands/code:ns NodeView)
              ns-name (namespace sym)
              from (if ns-name
                     (+ from 1 (count ns-name))
                     from)
              sci-ns (if ns-name
                       (or (sci/find-ns ctx (symbol ns-name))
                           (some-> (sci.ns/sci-ns-aliases ctx current-ns)
                                   (get (symbol ns-name))))
                       current-ns)
              syms (if ns-name
                     (sci.ns/sci-ns-publics ctx sci-ns)
                     (sci.ns/sci-ns-map ctx sci-ns))
              results #js{:from from
                          :to to
                          :validFor #"^[^ \[\](){}@#]+"
                          :options
                          (to-array
                           (keep (fn [[s s-var]]
                                   (when-let [{:as m :keys [ns name]} (meta s-var)]
                                     (try
                                       (let [sym-name (c/name name)
                                             ns-name (sci.ns/sci-ns-name ns)]
                                         #js{:label sym-name
                                             :sym (symbol ns-name sym-name)
                                             :detail ns-name
                                             ;; :type "y" ;; indicates icon
                                             ;; :detail ;; short string shown after label
                                             ;; :info ;; shown when completion is selected
                                             ;; :apply ".." ;; a string to replace completion range with, or function that will be called
                                             })
                                       (catch js/Error e
                                         (js/console.error e)
                                         (prn :s-var s-var)
                                         (prn :meta (meta s-var))))))
                                 syms))}]
          results)))))



(js

  (def styles
    {".cm-tooltip.cm-tooltip-autocomplete"
     {:border "none"
      :background-color "white"
      :box-shadow "2px 2px 8px 0 rgba(0,0,0,.2)"}
     ".cm-tooltip.cm-tooltip-autocomplete > ul"
     {:min-width "min(225px, 95vw)"}
     ".cm-tooltip.cm-tooltip-autocomplete > ul > li"
     {:padding "0.4rem"
      :margin-top "-1px"
      :color "black"
      :border-bottom "1px solid rgba(0,0,0,0.05)"
      :display "flex"
      :font-size "0.8rem"}
     ".cm-tooltip-autocomplete ul li[aria-selected]"
     {:background-color "rgba(0, 0, 0, .03)"}
     ".cm-completionLabel"
     {:flex "auto"}
     ".cm-completionDetail"
     {:color "rgba(0,0,0,0.5)"
      :font-style "normal"}})

  (defn plugin []
    [(.theme cm.view/EditorView styles)
     (.of cm.view/keymap
          (j/push! a/completionKeymap {:key "Tab" :run a/acceptCompletion}))
     (a/autocompletion {:activateOnTyping true
                        :selectOnOpen false
                        :maxRenderedOptions 20
                        :icons false
                        :defaultKeymap false})]))