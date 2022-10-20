(ns maria.prose.links
  (:require ["prosemirror-state" :refer [Plugin]]
            ["react-dom/client" :refer [createRoot]]
            [applied-science.js-interop :as j]
            [shadow.cljs.modern :refer [defclass]]
            [yawn.view :as v]
            [maria.prose.schema :refer [schema]]
            [maria.icons :as icons]
            [clojure.string :as str]
            [maria.util :as u]))

(def link-mark-type (j/get-in schema [:marks :link]))

(defn get-mark [marks type]
  (first (filter #(identical? (j/get % :type) type)
                 (vec marks))))

(defn mark-extend [^js $pos ^js type]
  (let [parent (.-parent $pos)
        start-index (loop [start-index (.index $pos)]
                      (if (or (<= start-index 0)
                              (not (.isInSet type (.. $pos -parent (child (dec start-index)) -marks))))
                        start-index
                        (recur (dec start-index))))
        end-index (loop [end-index (.indexAfter $pos)]
                    (if (or (>= end-index (.. $pos -parent -childCount))
                            (not (.isInSet type (.. $pos -parent (child end-index) -marks))))
                      end-index
                      (recur (inc end-index))))
        [start-pos end-pos] (loop [start-pos (.start $pos)
                                   end-pos start-pos
                                   i 0]
                              (if (>= i end-index)
                                [start-pos end-pos]
                                (let [size (.. parent (child i) -nodeSize)]
                                  (if (< i start-index)
                                    (recur (+ start-pos size) (+ end-pos size) (inc i))
                                    (recur start-pos (+ end-pos size) (inc i))))))]
    #js[start-pos end-pos]))

(defn mark-at [^js $pos type]
  (or (some-> $pos .-nodeBefore .-marks (get-mark type))
      (some-> $pos .-nodeAfter .-marks (get-mark type))))

(defn resolve-mark [$pos type]
  (when-let [mark (mark-at $pos type)]
    (to-array (into [mark] (mark-extend $pos type)))))

(j/js
  (defn open-link [view $pos]
    (let [{:keys [dispatch state]} view
          {:keys [doc]} state]
      (when-let [[mark from to] (resolve-mark $pos link-mark-type)]
        (let [text (str "[" (.textBetween doc from to) "](" (.. mark -attrs -href) ")")]
          (dispatch (-> (.-tr state)
                        (.removeMark from to link-mark-type)
                        (.insertText text from to)))
          (.focus view))))))

(j/js
  (defn edit-href [view $pos href]
    (let [{:keys [state dispatch]} view]
      (when-let [[mark from to] (resolve-mark $pos link-mark-type)]
        (dispatch (-> (.-tr state)
                      (.removeMark from to link-mark-type)
                      (cond-> href (.addMark from to (.create link-mark-type {:href href})))))
        (.focus view)))))

(j/js
  (defn open-link-on-backspace [state dispatch view]
    (let [$cursor (.. state -selection -$cursor)
          [_ to] (mark-extend $cursor link-mark-type)]
      (when (= to (.-pos $cursor))
        (open-link view $cursor)
        true))))

(defn on-keys
  "Returns event handler which handles keys in order, stopping at first true return value.

  (on-keys 'Escape' f, 'Return' f)"
  [k f & kfs]
  (let [idx (reduce (fn [index [k f]]
                      (update index k (fnil conj []) f)) {} (into [[k f]] (partition 2 kfs)))]
    (fn [^js event]
      (reduce
       (fn [_ f]
         (let [ret (f event)]
           (cond-> ret
                   (true? ret) (reduced))))
       nil
       (idx (.-key event))))))

(def target-value (j/get-in [:target :value]))

(v/defview link-tooltip
  {:key :href}
  [{:as props :keys [href title style view $pos]}]
  (let [[edit-text set-text!] (v/use-state nil)
        input-el (v/use-ref)
        c:icon-btn " text-gray-700 inline-flex items-center p-2 cursor-pointer hover:text-gray-500"
        exit! #(set-text! nil)
        save! #(do (.preventDefault %)
                   (edit-href view $pos (u/some-str edit-text))
                   (exit!))
        remove! #(edit-href view $pos nil)]

    ;; select input after showing it
    (v/use-effect #(some-> @input-el (j/call :select)) [(some? edit-text)])

    (when props
      [:div.bg-white.rounded-md.shadow-md.absolute.flex.overflow-hidden.divide-x
       {:style style}
       (if edit-text
         [:<>
          [:input.inline-flex.items-center.p-2 {:value edit-text
                                                :ref input-el
                                                :on-change (comp set-text! target-value)
                                                :on-key-down (on-keys "Escape" exit!
                                                                      "Enter" save!)}]
          [:button {:class c:icon-btn
                    :on-click save!} (icons/check "w-5 h-5")]
          [:button {:class c:icon-btn
                    :on-click remove!} (icons/trash "w-5 h-5")]]
         [:<>
          [:a.inline-flex.items-center.p-2.no-underline.hover:underline.truncate
           {:class "max-w-[20rem]" :href href
            :title title}
           (str/replace href #"https?://" "")]
          [:div {:class c:icon-btn
                 :on-click #(set-text! href)} (icons/pencil "w-5 h-5")]])])))

(defclass Tooltip
  (field root)
  (field div)
  (field view)
  (constructor [this prose-view]
               (set! view prose-view)
               (set! div
                     (-> (js/document.createElement "div")
                         (j/!set :className "pm-tooltip-link")))
               (.. js/document -body (appendChild div))
               #_(.. view -dom -parentNode (appendChild div))
               (set! root (createRoot div))
               (.update this prose-view nil))
  Object
  (update [this ^js view ^js prev-state]
          (let [state (.-state view)
                changed? (and prev-state (or (not (.. prev-state -selection (eq (.-selection state))))
                                             (not (.. prev-state -doc (eq (.-doc state))))))
                $head (.. state -selection -$head)
                href (-> $head
                         (mark-at link-mark-type)
                         (j/get-in [:attrs :href]))]
            (cond
              (not changed?) nil ;; no-op
              (not href) (.render root (link-tooltip nil))
              :else
              (j/let [[from to] (mark-extend $head link-mark-type)
                      ^js {start-left :left
                           start-top :top} (.coordsAtPos view from)
                      ^js {end-bottom :bottom} (.coordsAtPos view to)
                      ^js {inner-height :innerHeight
                           scroll-y :scrollY} js/window
                      above? (let [mid-y (- end-bottom (-> end-bottom (- start-top) (/ 2)))]
                               (> mid-y (/ inner-height 2)))]
                (.render root (link-tooltip {:href href
                                             :view view
                                             :$pos $head
                                             :style (merge {:left start-left}
                                                           (if above?
                                                             {:bottom (-> (- inner-height start-top scroll-y)
                                                                          (+ 10))}
                                                             {:top (+ scroll-y end-bottom 10)}))}))))))
  (destroy [this]
           (.unmount root)
           (.. div -parentNode (removeChild div))))

(defn plugin []
  (new Plugin #js{:view (fn [editor-view]
                          (new Tooltip editor-view))}))