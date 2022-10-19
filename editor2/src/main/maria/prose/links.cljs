(ns maria.prose.links
  (:require ["prosemirror-state" :refer [Plugin]]
            ["react-dom/client" :refer [createRoot]]
            [applied-science.js-interop :as j]
            [shadow.cljs.modern :refer [defclass]]
            [yawn.view :as v]
            [maria.prose.schema :refer [schema]]
            ))

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

(j/js
  (defn open-link
    ([view] (open-link (.-state view) (.-dispatch view) view))
    ([{:as state :keys [doc selection]} dispatch view]
     (when-let [$cursor (.-$cursor selection)]
       (when-let [mark (some-> $cursor .-nodeBefore .-marks (get-mark link-mark-type))]
         (let [[from to] (mark-extend $cursor link-mark-type)
               text (str "[" (.textBetween doc from to) "](" (.. mark -attrs -href) ")")]
           (dispatch (-> (.-tr state)
                         (.removeMark from to link-mark-type)
                         (.insertText text from to)))
           (.focus view)))))))

(j/js
  (defn open-link-on-backspace [state dispatch view]
    (let [$cursor (.. state -selection -$cursor)
          [_ to] (mark-extend $cursor link-mark-type)]
      (when (= to (.-pos $cursor))
        (open-link state dispatch view)
        true))))

(v/defview link-tooltip [{:as props :keys [href title style view]}]
  (when props
    [:div.bg-white.p-3.rounded-md.shadow-md.absolute
     {:style style}
     [:div {:on-click #(open-link view)} "edit"]
     [:a {:href href :title title} href]]))

(defn mark-at [^js $pos type]
  (or (some-> $pos .-nodeBefore .-marks (get-mark type))
      (some-> $pos .-nodeAfter .-marks (get-mark type))))

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
                selection-changed? (and prev-state (not (.. prev-state -selection (eq (.-selection state)))))
                $head (.. state -selection -$head)
                href (-> $head
                         (mark-at link-mark-type)
                         (j/get-in [:attrs :href]))]
            (cond
              (not selection-changed?) nil ;; no-op
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