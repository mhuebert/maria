(ns maria.views.text
  (:require [re-view.core :as v :refer [view defview]]
            [goog.events :as events]
            [re-view.routing :refer [closest]])
  (:import [goog.dom ViewportSizeMonitor]
           [goog.events EventType]))

(defn keydown-char [e]
  (let [->ascii {188 44 109 45 190 46 191 47 192 96 220 92 221 93 173 45 187 61 186 59 189 45}
        shiftUps {222 "\"" 96 "~" 49 "!" 50 "@" 51 "#" 52 "$" 53 "%" 54 "^" 55 "&" 56 "*" 57 "(" 48 ")" 45 "_" 61 "+" 91 "{" 93 "}" 92 "|" 59 ":" 39 "\"" 44 "<" 46 ">" 47 "?"}
        nulls #{;; delete
                8 9
                ;; arrows
                37 38 39 40
                ;; modifier keys
                16 17 18 91}
        which (.-which e)
        which (or (get ->ascii which) which)
        shift? (.-shiftKey e)]

    (cond (or (.-ctrlKey e) (.-metaKey e)) nil
          (and (= 13 which) (not (.-altKey e))) nil         ;; option-enter is a command; ignore it
          (contains? nulls which) nil                       ;; other non-character keys
          (and (not shift?) (> which 64) (< which 91)) (js/String.fromCharCode (+ which 32)) ;; lowercase
          (and shift? (contains? shiftUps which)) (get shiftUps which) ;; special keys
          :else (js/String.fromCharCode which))))

(defn select-keys-js
  [obj ks]
  (if-not obj
    {}
    (reduce (fn [m k] (assoc m k (aget obj (name k)))) {} ks)))

(defn get-cursor-pos [input]
  (cond
    js/document.selection (do (.focus input) (-> (.createRange js/document.selection)
                                                 (.moveStart "character" (-> input .-value .-length -))
                                                 .-text
                                                 .-length))
    (.-selectionStart input) (.-selectionStart input)
    :else 0))


;; a textarea which adjusts to the size of its contents by using an invisible div
;; to measure the size of contents. Tricky bit was to get this to work while typing
;; (avoiding flicker)


(defn value-adjust [value]
  (if (#{"\r" "\n"} (last value)) (str value " ") value))

(def width-adjust
  ;; mobile safari adds extra padding to textareas (3px on either side) - this compensates for
  (if (re-find #"iPhone|iPad|iPod" (some-> js/navigator .-userAgent))
    6 0))

(def cached-props (atom {}))
(def window-width (atom js/window.innerWidth))
(defn get-prop [prop id computed-style value node]
  (let [lookup-key (assoc computed-style
                     :value value
                     :window-width @window-width)]
    (or (get-in @cached-props [id prop lookup-key])
        (let [prop-val (aget node prop)]
          (swap! cached-props assoc-in [id prop] {lookup-key prop-val})
          prop-val))))

(defonce _
         (events/listen (ViewportSizeMonitor.) EventType.RESIZE #(reset! window-width js/window.innerWidth)))

(defn update-size
  ([this] (update-size this nil))
  ([{:keys [view/props view/state] :as this} char]
   (when-let [{:keys [input-element fake-element]} @state]
     (let [id (:node/id props)
           root-node (v/dom-node this)
           computed-style (select-keys-js (js/getComputedStyle fake-element) ["font-size" "line-height" "font-style" "padding" "font-weight"])
           value (.-innerHTML fake-element)
           _ (when char (set! (.-innerHTML fake-element) (value-adjust (str value char))))
           width (+ 1 width-adjust (max 10 (get-prop "offsetWidth" id computed-style value fake-element)))
           height (get-prop "offsetHeight" id computed-style value fake-element)]
       (set! (-> input-element .-style .-height) (str height "px"))
       (set! (-> input-element .-style .-width) (str width "px"))
       (set! (-> input-element .-style .-lineHeight) (computed-style "line-height"))
       (set! (-> root-node .-style .-height) (str height "px"))))))

(defview autosize-text
  {:display-name    "AutosizeText"
   :view/did-mount  (fn [{:keys [auto-focus view/state] :as this}]
                      (update-size this)
                      (when auto-focus (.focus (:input-element @state)))
                      )
   :view/did-update (fn [this] (update-size this))
   :cols            (fn [{:keys [view/state]}]
                      (let [fake           (:fake-element @state)
                            sample         "Order. When I switched action-handling to a timeout"
                            original-value (.-innerHTML fake)
                            original-width (-> fake .getBoundingClientRect .-width (- 9))
                            _              (set! (.-innerHTML fake) sample)
                            line-width     (-> fake .getBoundingClientRect .-width (- 9))
                            char-width     (/ line-width (count sample))
                            _              (set! (.-innerHTML fake) original-value)]
                        (/ original-width char-width)))
   :rows            (fn [{:keys [view/state]}]
                      (let [fake            (:fake-element @state)
                            original-value  (.-innerHTML fake)
                            original-height (-> fake .getBoundingClientRect .-height)
                            _               (set! (.-innerHTML fake) "|")
                            line-height     (-> fake .getBoundingClientRect .-height)
                            rows            (/ original-height line-height)
                            _               (set! (.-innerHTML fake) original-value)]
                        rows))
   :cursorLine      (fn [{:keys [view/state] :as this}]
                      ;; to get the current line of the cursor, we use our fake/shadow div.
                      (let [fake                 (:fake-element @state)
                            original-value       (.-innerHTML fake)
                            original-height      (-> fake .getBoundingClientRect .-height)
                            pos                  (.cursorPos this)
                            _                    (set! (.-innerHTML fake) "|")
                            line-height          (-> fake .getBoundingClientRect .-height)
                            _                    (set! (.-innerHTML fake) (subs original-value 0 pos))
                            prior-content-height (-> fake .getBoundingClientRect .-height)
                            rows                 (/ original-height line-height)
                            current-line         (/ prior-content-height line-height)
                            current-line         (-> current-line (max 1) (min rows))
                            _                    (set! (.-innerHTML fake) original-value)]
                        [rows current-line]))
   :lastLineX       (fn [this ch]
                      (+ ch (* (dec (.rows this)) (.cols this))))
   :focus           (fn [{:keys [view/state]}]
                      (.focus (:input-element @state)))
   :cursorPos       (fn [{:keys [view/state]}] (get-cursor-pos (:input-element @state)))}
  [{:keys [value placeholder class style on-key-down view/props view/state] :as this}]
  (let [placeholder (or placeholder "...")
        v (if (empty? value) placeholder value)]
    [:.autosize-input
     {:class (when (empty? value) "o-60")}
     [:.fixed.o-0.z-0.dib.w-100
      {:style {:top -2000}}
      [:.autosize-input-fake.w-auto
       {:ref #(when % (swap! state assoc :fake-element %))
        :class class}
       (str v (when (#{"\n" "\r"} (last v)) " "))]]
     [:input (-> props
                 (update :classes into ["autosize-input-input dib"])
                 (merge {:ref         #(when % (swap! state assoc :input-element (v/dom-node %)))
                         :on-key-down #(do (update-size this (keydown-char %))
                                           (when on-key-down (on-key-down %)))}
                        (when (re-find #"iPhone|iPad|iPod" (some-> js/navigator .-userAgent))
                          ;; remove text-indent in mobile safari
                          {:style (assoc style :marginLeft -3 :marginRight -3)})))]]))





