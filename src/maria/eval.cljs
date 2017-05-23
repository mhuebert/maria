(ns maria.eval
  (:require [cljs.js :as cljs]
            [cljs.analyzer :refer [*cljs-warning-handlers*]]
            [cljs-live.compiler :as c]
            [cljs-live.eval :as e]
            [cljs.tools.reader :as r]
            [cljs.repl :refer [print-doc]]
            [re-view.core :as v :refer [defview]]
            [re-view-hiccup.core :as hiccup :refer [element]]
            [maria.friendly.docstrings :refer [docstrings]]
            [maria.ns-utils :as ns-utils]
            [clojure.string :as string]
            [re-view-material.core :as ui]
            [re-view-material.icons :as icons]))

(defonce c-state (cljs/empty-state))
(defonce c-env (atom {:ns (symbol "cljs.user")}))

(defview def-display [def-map]
  (let [friendly-doc (get-in docstrings [namespace name])]
    [:.ph3 (with-out-str
             (some-> def-map
                     (select-keys [:name :doc :arglists])
                     (cond->
                       friendly-doc (assoc :doc friendly-doc))
                     print-doc)
             "Not found")
     (when (#{'cljs.core 'cljs.core$macros 'clojure.core} namespace)
       (list [:.gray.di "view on "]
             [:a {:href   (str "https://clojuredocs.org/clojure.core/" name)
                  :target "_blank"
                  :rel    "noopener noreferrer"} "clojuredocs.org"]))]))

(defview doc-display
  {:life/initial-state #(do (prn :doc-display (:view/props %))
                            (:expanded? %))
   :key                :name}
  [{:keys [doc arglists view/state show-namespace?] :as this}]
  [:.ph3.bt.b--near-white
   [:.code.flex.items-center.pointer.mv1
    {:on-click #(swap! state not)}
    (when show-namespace?
      [:span.o-60 (namespace (:name this)) "/"])
    (str (name (:name this)))
    [:.flex-auto]
    (if @state [:span.o-50 icons/ExpandLess]
               icons/ExpandMore)]
   (when @state
     (list
       [:.mv1.blue (string/join ", " (map str (second arglists)))]
       [:.gray.mv2 doc]))])

(defn doc
  "Show doc for symbol"
  [c-state c-env [_ n]]
  (let [[namespace name] (let [n (e/resolve-symbol c-state c-env n)]
                           (map symbol [(namespace n) (name n)]))]
    {:value (doc-display (merge {:expanded?       true
                                 :show-namespace? true}
                                (get-in (ns-utils/ns-map @c-state namespace) [:defs name])))}))

(defn dir
  "Display public vars in namespace"
  [c-state c-env [_ ns]]
  (let [ns (or ns (:ns @c-env))]
    {:value
     (element [:.sans-serif
               [:.b.pv2.ph3.f5 (str ns)]
               (->> (:defs (ns-utils/ns-map @c-state ns))
                    (seq)
                    (sort)
                    (map second)
                    (filter #(not (:private (:meta %))))
                    (map doc-display))])}))

;; mutate cljs-live's default repl-specials
(e/swap-repl-specials! merge {'doc doc
                              'dir dir})

(def eval (partial e/eval c-state c-env))
(def eval-str (partial e/eval-str c-state c-env))

(defonce _
         (do (set! cljs-live.compiler/debug? true)
             (c/load-bundles! ["/js/cljs_bundles/cljs.core.json"
                               "/js/cljs_bundles/maria.user.json"
                               #_"/js/cljs_bundles/quil.json"]
                              (fn []
                                (eval '(require '[cljs.core :include-macros true]))
                                (eval '(require '[maria.user :include-macros true]))
                                (eval '(in-ns maria.user))))))







