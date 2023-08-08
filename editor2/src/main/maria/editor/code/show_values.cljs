(ns maria.editor.code.show-values
  (:refer-clojure :exclude [var?])
  (:require ["react" :as react]
            [applied-science.js-interop :as j]
            [clojure.core]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as str]
            [maria.editor.code.commands :as commands]
            [maria.editor.code.error-marks :as error-marks]
            [maria.editor.code.repl :as repl]
            [maria.editor.icons :as icons]
            [maria.editor.util]
            [maria.ui :as ui]
            [maria.ui :refer [defview]]
            [maria.editor.views :as views]
            [nextjournal.clojure-mode.node :as n]
            [promesa.core :as p]
            [re-db.reactive :as r]
            [sci.core :as sci]
            [shapes.core :as shapes]
            [yawn.hooks :as h]
            [yawn.view :as v]
            [cells.core :as cells]
            [reagent.core :as reagent]
            [shadow.lazy :as lazy]))


(def COLL-PADDING 4)

(defn var? [x] (instance? sci.lang/Var x))

(declare show)

(defn set-error-highlight! [NodeView id location]
  (some-> (commands/code-node-by-id NodeView id)
          (j/get :CodeView)
          (error-marks/set-highlight! location)))

(defn inline-form [NodeView id location]
  (when-let [state (some-> (commands/code-node-by-id NodeView id)
                           (j/get-in [:CodeView :state]))]
    (j/let [^:js {:keys [from to]} (error-marks/get-range state location)]
      (n/string state from to))))

(defview show-stacktrace [opts stack]
  ;; - for built-in, look up clojure.core metadata / link to source?
  (into [:div.flex.flex-col.-mx-2]
        (keep (fn [{:as frame :keys [file ns line column sci/built-in local name]}]
                (let [icon-classes "w-5 h-5 -mt-1"
                      fqn (when name
                            (if (= ns 'user)
                              name
                              (symbol ns name)))
                      code-cell (when (and file (str/starts-with? file "code-"))
                                  (str "#" file))
                      m (when fqn
                          (let [m (meta (repl/resolve-symbol (:sci/context opts)
                                                             nil
                                                             fqn))]
                            (when (or (:doc m) (not-empty (:arglists m)))
                              m)))
                      fqn (if m
                            (symbol (str (:ns m)) (str (:name m)))
                            fqn)
                      inline-str (when (and (not fqn) line)
                                   (inline-form (:NodeView opts) file [line column]))]
                  [:div.flex.flex-row.gap-1.py-1.px-2
                   (when (and file line)
                     {:class "hover:bg-gray-200"
                      :on-mouse-enter #(set-error-highlight! (:NodeView opts) file [line column])
                      :on-mouse-leave #(set-error-highlight! (:NodeView opts) file nil)})
                   (when (and code-cell line)
                     [:div.no-underline.cursor-pointer.text-gray-700.hover:underline
                      {:href code-cell}
                      (icons/map-pin:mini icon-classes)])
                   (when m
                     (views/doc-tooltip m (icons/information-circle:mini icon-classes)))
                   (if fqn
                     [:div
                      [:span.text-gray-500 (str (namespace fqn)) "/"]
                      [:span.text-black (str (.-name ^clj fqn))]]
                     [:<>
                      [:span.text-gray-500.italic "<expr>"]
                      #_[:div.pre-wrap.truncate inline-str]
                      ])

                   (when built-in [:span.text-gray-500.ml-1 "(built-in)"])
                   [:div.flex-grow]
                   (when line
                     [:div.text-gray-700 line ":" column])
                   #_[:div {:on-click #(prn frame)} (icons/command-line:mini "w-5 h-5")]
                   ])))
        (force stack)))

(defview show-error [opts error]
  [:<>
   [:div.bg-red-100.text-red-800.border.border-red-200.flex.items-start
    [:div.m-2.flex-grow (ex-message error)]
    [:a.m-2.cursor-pointer.text-red-800.hover:text-red-900
     {:title "Print to console"
      :on-click #(do (js/console.error error)
                     (some-> (sci/stacktrace error) seq pprint))} (icons/command-line:mini "w-5 h-5")]]
   #_[:pre (ui/pprinted (ex-data error))]
   (when-let [stack (seq (sci/stacktrace error))]
     [:div.p-2
      [show-stacktrace opts stack]])])

(declare get-viewers)

(defview show
  [opts x]
  (assert (:sci/context opts))
  (assert (:sci/get-ns opts))
  (let [opts
        (-> opts
            (update :depth (fnil inc -1))
            (update :stack (fnil conj ()) x)
            (update :viewers #(or % (get-viewers (:sci/context opts)))))
        out (reduce (fn [_ viewer] (if-some [out (viewer opts x)]
                                     (reduced out)
                                     _))
                    ::no-viewer
                    (:viewers opts))]
    (if (= ::no-viewer out)
      (do (js/console.log "no viewer for:" x)
          (prn :no-viewer-for x (type x))
          (str "No viewer for " (type x)))
      out)))

(defview more-btn [on-click]
  [:div.inline-block.-mt-1 {:on-click on-click}
   (icons/ellipsis:mini "w-4 h-4 cursor-pointer ")])

(defn punctuate ^v/el [s]
  (v/x [:div.inline-block s]))

(defview show-brackets [left right more children !wrapper !parent interpose-comma?]
  (let [bracket-classes "flex flex-none text-brackets"]
    [:div.inline-flex.max-w-full.whitespace-nowrap {:ref !wrapper}
     [:div.items-start {:class bracket-classes} (punctuate left)]

     (-> [:div.inline-flex.flex-wrap.items-end.gap-list.overflow-hidden
          {:ref !parent
           :class (when interpose-comma? "interpose-comma")}]
         (into (map (fn [x] x)) children))

     [:div.items-end {:class bracket-classes}
      (when more [more-btn more])
      (punctuate right)]]))

(defn measure-node [^js node]
  (some->> (if (= 3 (j/get node :nodeType))
             (-> (js/document.createRange)
                 (doto (.selectNodeContents node))
                 (.getClientRects)
                 first
                 ((juxt (j/get :width) (j/get :height))))
             [(.-offsetWidth node) (.-offsetHeight node)])
           (mapv Math/round)))

(defn compute-box-limit [^js wrapper-el ^js parent-el]
  (when-let [^js item (and wrapper-el parent-el (.-lastChild parent-el))]
    (j/let [wrapper-width (- (.. wrapper-el -offsetWidth)
                             (.. wrapper-el -firstChild -offsetWidth)
                             (.. wrapper-el -lastChild -offsetWidth))
            [item-width
             item-height] (measure-node item)
            total-height (Math/round (-> (j/get js/window :innerHeight)
                                         (/ 2)))]
      (loop [available-height (- total-height item-height COLL-PADDING)
             available-width (- wrapper-width item-width COLL-PADDING)
             limit 1]
        (let [available-width (- available-width item-width)]
          (cond (= limit 500) limit

                (and (neg? available-height) (neg? available-width)) ;; return, dimensions exceeded
                limit

                (neg? available-width)                      ;; wrap, width exceeded
                (recur (- available-height item-height COLL-PADDING) (- wrapper-width item-width COLL-PADDING) (inc limit))

                :else                                       ;; width is available, add to row
                (recur available-height available-width (inc limit))))))))

(defn use-limit [coll initial-limit]
  (j/let [!inc (h/use-ref initial-limit)
          [showing set-showing!] (h/use-state initial-limit)
          !wrapper (h/use-ref)
          !parent (h/use-ref)
          total (bounded-count (+ showing @!inc) coll)]
    [(take showing coll)
     (when (> total showing)
       (fn []
         (when-let [new-limit (compute-box-limit @!wrapper @!parent)]
           (reset! !inc new-limit))
         (set-showing! (+ showing @!inc))))
     !wrapper
     !parent]))

(defview show-coll [left right opts coll]
  (let [[coll more! !wrapper !parent] (use-limit coll 10)]
    [show-brackets
     left
     right
     more!
     (into []
           (comp (map #(do [:span.inline-flex.align-center (show opts %)])))
           coll)
     !wrapper
     !parent
     (or (map? coll) (vector? coll))]))

(defview show-map-entry [opts this]
  [:div.inline-flex.gap-list
   [:span (show opts (key this))] " "
   [:span (show opts (val this))]])

(def loader
  (v/x [:div.cell-status
        [:div.circle-loading
         [:div]
         [:div]]]))

(defview show-promise [opts p]
  (let [[v v!] (h/use-state ::loading)]
    (h/use-effect
      (fn []
        (v! ::loading)
        (p/then p v!)
        nil)
      [p])
    (v/x
      (if (= v ::loading)
        loader
        (show opts v)))))

(let [show-katex-loadable (lazy/loadable maria.editor.extensions.katex/show-katex)]
  (defn show-katex [opts x]
    (if (lazy/ready? show-katex-loadable)
      (@show-katex-loadable :div x)
      (show opts (p/do (lazy/load show-katex-loadable)
                       (@show-katex-loadable :div x))))))

(defview show-var [opts x]
  (v/x [:<>
        (when (fn? @x) [:div.text-gray-500.mb-1 (str x)])
        (show opts @x)]))

(defview show-str [opts x]
  ;; TODO - collapse via button, not clicking anywhere on the text
  (let [[collapse toggle!] (h/use-state (or (> (count (str/split-lines x)) 10)
                                            (> (count x) 200)))
        x (str \" x \")]
    (if collapse
      [:div.line-clamp-6.text-string {:on-click #(toggle! not)} x]
      [:div.text-string {:class ["max-h-[80vh] overflow-y-auto"]
                         :on-click #(toggle! not)} x])))

(def show-map (partial show-coll "{" "}"))
(def show-set (partial show-coll "#{" "}"))
(def show-list (partial show-coll "(" ")"))
(def show-vector (partial show-coll \[ \]))

(defview show-function [_ f]
  (let [[showing toggle!] (h/use-state false)]
    ;; TODO show arrow
    [:div {:on-click #(toggle! not)}
     [:div "ƒ"]
     (when showing
       [:div.pre-wrap (str f)])]))

(defview show-shape [_ x]
  (v/x (shapes/to-hiccup x)))

(defn show-identity [opts x] x)
(defn show-terminal [f] (fn [opts x] (f x)))
(defn show-inline
  ([^string classes xf] (fn [opts x] (v/x [:span {:class classes} (xf x)])))
  ([^string classes] (fn [opts x] (v/x [:span {:class classes} x]))))

(defview show-watchable [opts x]
  (show opts (h/use-deref x)))

(def cell-value-circle
  (v/x [:div.cell-status
        [:div.circle-value
         [:div]
         [:div]]]))

(defview show-cell [opts cell]
  ;; always watch status & value together, to avoid cell value being unwatched during loading/error states.
  (let [{:keys [loading error]} (h/use-deref (cells.async/!status cell))
        [cell-error value] (r/deref-result cell)
        error (or error cell-error)]
    (cond loading loader
          error (show-error opts error)
          #_cell-value-circle
          ;; should we indicate that this is a cell somehow?
          :else (show opts (if-let [v (cells/get-view cell)]
                             (v cell)
                             value)))))

(v/defview show-ns [opts ns]
  [:div.text-sm.font-mono

   [:div.text-slate-800 (str ns)]
   [:span.mr-2.text-slate-500 "namespace"]])

(defn by-type [views]
  (fn [opts x]
    (when-some [f (views (type x))]
      (f opts x))))

(defn reagent-eval
  "Evaluates reagent form within namespace of current cell"
  [opts form]
  (commands/code:eval-form-in-show opts
                                   `(~'reagent.core/as-element [(fn [] ~form)])))

(defn handles-keys
  "Specify keywords indicating what kind of values a viewer handles.
   Helpful for adding viewers before or after other viewers."
  [ks f]
  (with-meta f {::keys ks}))

(def builtin-viewers
  "Ordered list of viewers functions"
  (flatten (list
             (fn [_ x]
               (when (= x ::loading)
                 loader))


             (handles-keys #{:maria/cells}
               (fn [opts x]
                 (when (cells/cell? x)
                   (show-cell opts x))))

             (handles-keys #{:react-element}
               ;; show react elements directly
               (fn [opts x]
                 (when (react/isValidElement x)
                   x)))

             (handles-keys #{:hiccup}
               (fn [opts x]
                 (when (:hiccup (meta x))
                   (v/x x))))

             (handles-keys #{:TeX}
               ;; THIS IS TEMPORARY FOR TESTING
               (fn [opts x]
                 (when-let [s (:TeX (meta x))]
                   (show-katex opts s))))

             (handles-keys #{:number
                             :symbol
                             :var
                             :keyword
                             :map-entry
                             :string
                             :boolean
                             :map
                             :function
                             :promise
                             :namespace
                             :vector
                             :set
                             :list
                             :error}
               (by-type {js/Number (show-inline "text-number")
                         Symbol (show-inline "text-variableName" str)
                         js/String show-str
                         js/Boolean (show-inline "text-bool" pr-str)
                         Keyword (show-inline "text-keyword" str)
                         MapEntry show-map-entry
                         Var show-var
                         sci.lang/Var show-var
                         js/Function show-function
                         MetaFn show-function
                         js/Promise show-promise
                         PersistentArrayMap show-map
                         PersistentHashMap show-map
                         TransientHashMap show-map
                         PersistentTreeSet show-set
                         PersistentHashSet show-set
                         List show-list
                         LazySeq show-list
                         shapes/Shape show-shape
                         sci.lang/Namespace show-ns
                         APersistentVector show-vector
                         PersistentVector show-vector
                         Subvec show-vector
                         RedNode show-vector
                         js/Error show-error
                         ExceptionInfo show-error
                         }))

             (handles-keys #{:error}
               (fn [opts x]
                 (when (instance? js/Error x)
                   (show-error opts x))))
             (handles-keys #{:function}
               (fn [opts x]
                 (when (fn? x)
                   (show-function opts x))))

             (handles-keys #{:cljs.core/IWatchable}
               (fn [opts x] (when (satisfies? IWatchable x)
                              (show-watchable opts x))))
             (handles-keys #{:seq}
               (fn [opts x] (when (seq? x) (show-list opts x))))
             (handles-keys #{:nil}
               (fn [opts x] (when (nil? x) (punctuate "nil"))))
             (handles-keys #{:object}
               (fn [opts x] (when (object? x) (show-map opts (js->clj x :keywordize-keys true)))))
             #_(fn [x] (clerk.sci-viewer/inspect x)))))

(defn add-viewers
  [ctx key viewers]
  (update-in ctx [::ns-viewers (repl/current-ns-name ctx)]
             (fn [x]
               (-> x
                   (->> (filterv #(not= key (first %))))
                   (conj [key viewers])))))

(defn get-viewers [ctx]
  ;; added-viewers are added in front of builtin viewers,
  ;; most recently added viewer takes precedence
  (into (or @(:!viewers ctx) builtin-viewers)
        (mapcat second)
        (get-in ctx [::ns-viewers (repl/current-ns-name ctx)])))

(defn first-index [pred coll]
  (reduce (fn [out i]
            (if (pred (nth coll i))
              (reduced i)
              i))
          0
          (range (count coll))))

(defn last-index [pred coll]
  (max 0 (- (count coll)
            (inc (first-index pred (reverse coll))))))

(defn insert [where pred coll x]
  (let [i (case where :before (first-index pred coll)
                      :after (inc (last-index pred coll)))]
    (concat (take i coll)
            x
            (drop i coll))))

(defn add-global-viewers!
  "Adds viewers to the global list of viewers, before/after the given viewer-key.
   See builtin-viewers to see what viewer-keys can be referred to"
  [ctx where viewer-key added-viewers]
  {:pre [(#{:before :after} where)]}
  (let [!viewers (:!viewers ctx)]
    (->> (insert where
                 (comp viewer-key ::keys meta)
                 (or @!viewers builtin-viewers)
                 added-viewers)
         (reset! !viewers))
    ctx))

(comment

  (for [where [:before :after]]
    (insert where
            (comp :foo ::keys meta)
            [nil
             nil
             (handles-keys #{:foo} (fn []))
             nil
             (handles-keys #{:foo} (fn []))]
            [:X]))

  (= [2 4] ((juxt first-index last-index) :foo [#{:bar}
                                                #{:bar}
                                                #{:foo}
                                                #{:bar}
                                                #{:foo}
                                                #{:bar}]))

  (= [0 0]
     ;; not-found
     ((juxt first-index last-index) :foo [#{:bar}]))

  (= [0 0]
     ;; first entry
     ((juxt first-index last-index) :foo [#{:foo}]))

  (= [1 1]
     ;; midpoint
     ((juxt first-index last-index) :foo [#{:bar} #{:foo} #{:bar}]))

  (= [2 2]
     ;; does not matter how many are trailing
     ((juxt first-index last-index) :foo [#{:bar} #{:bar}
                                          #{:foo}
                                          #{:bar} #{:bar}])
     ((juxt first-index last-index) :foo [#{:bar} #{:bar}
                                          #{:foo}])
     ((juxt first-index last-index) :foo [#{:bar} #{:bar}
                                          #{:foo}
                                          #{:bar} #{:bar} #{:bar}]))
  (= [2 5]
     ;; selects last occurrence
     ((juxt first-index last-index) :foo [#{:bar} #{:bar}
                                          #{:foo}
                                          #{:bar} #{:bar}
                                          #{:foo}])))


