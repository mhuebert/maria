(ns maria.code.completions
  (:require ["@codemirror/autocomplete" :as a]
            ["@codemirror/view" :as cm.view]
            [applied-science.js-interop :as j]
            [clojure.edn :as edn]
            [clojure.core :as c]
            [maria.code.commands :as commands]
            [maria.repl.api :refer [*context*]]
            [maria.util :as u]
            [nextjournal.clojure-mode.node :as n]
            [sci.core :as sci]
            [sci.impl.namespaces :as sci.ns]))

(def symbol-nodes #{"Operator"
                    "DefLike"
                    "NS"
                    "Symbol"})

(j/js
  (defn completions [node-view {:keys [state explicit pos]}]
    (when-let [node (some-> (n/nearest-touching state pos -1)
                            (u/guard #(or explicit (= (n/end %) pos)))
                            (u/guard (comp symbol-nodes n/name)))]

      (let [{:as from-to :keys [from to]} (n/from-to node)
            text (.sliceDoc state from pos)]
        ;; clojure.core/x        ns: fully qualified, looks in the-ns publics
        ;; c/x                   ns: alias, looks in the-ns publics
        ;; x                     no ns, look at whole current-ns
        (when-let [sym (try (edn/read-string (.sliceDoc state from pos))
                            (catch js/Error e nil))]
          (let [ctx @*context*
                current-ns (or (commands/code:ns node-view)
                               (sci.ns/sci-find-ns ctx 'user))
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
                results {:from from
                         :to to
                         :validFor #"^[^ \[\](){}@#]+"
                         :options
                         (to-array
                          (mapv (fn [^:clj [s s-var]]
                                  (let [^:clj {:keys [ns name]} (meta s-var)
                                        sym-name (c/name name)
                                        ns-name (sci.ns/sci-ns-name ns)]
                                    {:label sym-name
                                     :sym (symbol ns-name sym-name)
                                     :detail ns-name
                                     ;; :type "y" ;; indicates icon
                                     ;; :detail ;; short string shown after label
                                     ;; :info ;; shown when completion is selected
                                     ;; :apply ".." ;; a string to replace completion range with, or function that will be called
                                     }))
                                syms))}]
            results))))))



(j/js

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