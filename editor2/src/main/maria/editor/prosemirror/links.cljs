(ns maria.editor.prosemirror.links
  (:require ["prosemirror-state" :refer [Plugin TextSelection]]
            ["prosemirror-inputrules" :as input-rules]
            [applied-science.js-interop :as j]
            [clojure.string :as str]
            [maria.editor.ui.icons :as icons]
            [maria.editor.prosemirror.schema :refer [schema]]
            [maria.editor.util :as u]
            [shadow.cljs.modern :refer [defclass]]
            [yawn.hooks :as h]
            [yawn.view :as v]
            [yawn.root :as root]))

(def mark:link (.. schema -marks -link))
(def node:image (.. schema -nodes -image))

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
      (when-let [[mark from to] (resolve-mark $pos mark:link)]
        (let [text (str "[" (.textBetween doc from to) "](" (.. mark -attrs -href) ")")]
          (dispatch (-> (.-tr state)
                        (.removeMark from to mark:link)
                        (.insertText text from to)))
          (.focus view))))))

(j/js
  (defn edit-href [view $pos href]
    (let [{:keys [state dispatch]} view]
      (when-let [[mark from to] (resolve-mark $pos mark:link)]
        (dispatch (-> (.-tr state)
                      (.removeMark from to mark:link)
                      (cond-> href (.addMark from to (.create mark:link {:href href})))))
        (.focus view)))))

(j/js
  (defn open-link-on-backspace [{:keys [selection]} dispatch view]
    (let [$cursor (.-$cursor selection)
          [_ _ to] (resolve-mark $cursor mark:link)]
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
  {:key :value}
  [{:as props :keys [value set-value! style]}]
  (let [[edit-text set-text!] (h/use-state nil)
        [hidden? set-hidden!] (h/use-state (nil? props))
        input-el (h/use-ref)
        c:icon-btn "hover:text-gray-700 text-gray-500 inline-flex items-center p-2 cursor-pointer"
        exit! #(set-text! nil)
        save! #(do (.preventDefault %)
                   (set-value! edit-text)
                   (exit!))
        remove! #(set-value! nil)]

    ;; select input after showing it
    (h/use-effect #(some-> @input-el (j/call :select)) [(some? edit-text)])

    ;; hide on escape
    (h/use-effect (fn []
                    (when-not hidden?
                      (let [f (on-keys "Escape" (fn [^js event]
                                                  (.preventDefault event)
                                                  (set-hidden! true)))]
                        (.addEventListener js/window "keydown" f #js{:capture true})
                        #(.removeEventListener js/window "keydown" f #js{:capture true}))))
                  [hidden?])
    (when-not hidden?
      [:div.bg-white.rounded-md.shadow-md.absolute.flex.overflow-hidden
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
           {:class "max-w-[20rem]" :href value}
           (str/replace value #"https?://" "")]
          [:div {:class c:icon-btn
                 :on-click #(set-text! value)} (icons/pencil-square:mini "w-5 h-5")]])])))

(defn tooltip-position [^js view from to]
  (j/let [^js {start-left :left
               start-top :top} (.coordsAtPos view from)
          ^js {end-bottom :bottom} (.coordsAtPos view to)
          ^js {inner-height :innerHeight
               scroll-y :scrollY} js/window
          above? (let [mid-y (- end-bottom (-> end-bottom (- start-top) (/ 2)))]
                   (> mid-y (/ inner-height 2)))]
    (merge {:left start-left}
           (if above?
             {:bottom (-> (- inner-height start-top scroll-y)
                          (+ 10))}
             {:top (+ scroll-y end-bottom 10)}))))

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
               (set! root (root/create div))
               (.update this prose-view nil))
  Object
  (update [this ^js view ^js prev-state]
          (let [state (.-state view)
                selection (.-selection state)
                changed? (and prev-state (or (not (.. prev-state -selection (eq (.-selection state))))
                                             (not (.. prev-state -doc (eq (.-doc state))))))
                $head (.. selection -$head)
                link-href (-> $head
                              (mark-at mark:link)
                              (j/get-in [:attrs :href]))
                ^js image-node (-> selection .-node (u/guard #(= (j/get % :type) node:image)))]
            (cond
              (not changed?) nil ;; no-op
              link-href (j/let [[from to] (mark-extend $head mark:link)]
                          (root/render root (link-tooltip {:value link-href
                                                           :set-value! #(edit-href view $head (u/some-str %))
                                                           :style (tooltip-position view from to)})))
              image-node (j/let [^js {:as attrs :keys [src]} (.. image-node -attrs)
                                 from (.. selection -$from -pos)
                                 to (.. selection -$to -pos)]
                           (j/log :image-node image-node)
                           (root/render root (link-tooltip {:value src
                                                            :set-value! #(.dispatch view
                                                                                    (.. state -tr
                                                                                        (setNodeAttribute from "src" %)))
                                                            :style (tooltip-position view from to)})))
              :else (.render root (link-tooltip nil)))))
  (destroy [this]
           (root/unmount-soon root)
           (.. div -parentNode (removeChild div))))

(j/js
  (defn plugins []
    [(new Plugin {:view (fn [editor-view]
                          (new Tooltip editor-view))})
     (input-rules/inputRules
      {:rules
       [(input-rules/InputRule.
         #"(\!?)\[(.*)\]\((.*)\)\s?$"
         (fn [{:keys [doc tr]} [_ kind label href] from to]
           (if (= kind "!")
             (let [image (.. schema -nodes -image)]
               (.. tr
                   (setSelection (.create TextSelection doc from to))
                   (replaceSelectionWith (.create image {:src href
                                                         :title label
                                                         :alt label}))))
             (.. tr
                 (insertText label from to)
                 (addMark from (+ from (count label)) (.create mark:link {:href href}))
                 (removeStoredMark mark:link)))))]})]))