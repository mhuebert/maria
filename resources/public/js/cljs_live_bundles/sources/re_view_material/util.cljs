(ns re-view-material.util
  (:require [re-view.core :refer [defview]]
            [clojure.string :as string]
            [goog.object :as gobj]
            [goog.dom.classes :as classes]
            [goog.dom :as gdom]
            [clojure.set :as set]))

(defn ensure-str [s]
  (when-not (contains? #{nil ""} s)
    s))

(defn keypress-value [^js/React.SyntheticEvent e]
  (let [target (.-target e)
        raw-value (.-value target)
        new-input (.fromCharCode js/String (.-which e))
        value (str (subs raw-value 0 (.-selectionStart target))
                   new-input
                   (subs raw-value (.-selectionEnd target) (.-length raw-value)))]
    value))

(defn keypress-action [^js/React.SyntheticEvent e]
  (let [str-char (ensure-str (.fromCharCode js/String (.-which e)))
        non-char-keys {13 "enter"
                       8  "backspace"}
        code (.-which e)]
    (string/join "+"
                 (cond-> []
                         (.-altKey e) (conj "alt")
                         (.-ctrlKey e) (conj "ctrl")
                         (.-metaKey e) (conj "meta")
                         (.-shiftKey e) (conj "shift")
                         true (conj (get non-char-keys code str-char))))))

(defn add-styles
  ([target styles]
   (add-styles target styles {}))
  ([target styles prev-styles]
   (when (or styles prev-styles)
     (let [style (gobj/get target "style")]
       (doseq [attr (-> #{}
                        (into (keys styles))
                        (into (keys prev-styles)))]
         (.setProperty style attr (get styles attr)))))))



(defn concat-handlers [handlers]
  (when-let [handlers (seq (keep identity handlers))]
    (fn [^js/React.SyntheticEvent e]
      (reduce (fn [res f] (f e)) nil handlers))))

(defn collect-handlers
  "Combines specified handlers from props"
  [props handlers]
  (reduce-kv (fn [m key handler]
               (assoc m key (concat-handlers [handler
                                              (get props key)]))) {} handlers))

(defn handle-on-save [handler]
  (when handler
    (fn [^js/React.SyntheticEvent e]
      (when (#{"ctrl+S" "meta+S" "enter"} (keypress-action e))
        (.preventDefault e)
        (handler)))))

(defn collect-text [text]
  (if (string? text)
    (some-> (ensure-str text) (list))
    (seq (for [msg text
               :when (ensure-str msg)]
           msg))))

(defn find-node [^js/Element root p]
  (if (p root) root (gdom/findNode root p)))

(defn find-tag [^js/Element root re]
  (gdom/findNode root (fn [^js/Element el]
                        (some->> (.-tagName el) (re-find re)))))

(defn closest [^js/Element root p]
  (if (p root) root (gdom/getAncestor root p)))

(defn log-ret [note x]
  (println note x)
  x)

(defn force-layout [^js/HTMLElement el]
  (.-offsetWidth el))

(defview sync-element!
  "Manage classes and styles for an uncontrolled DOM element (eg. `body` or `html`).
  :getElement should return the DOM element, :class and :style behave as normal."
  {:life/did-mount  (fn [{:keys [view/state get-element] :as this}]
                      (let [^js/Element element (get-element this)]
                        (swap! state assoc
                               :element element
                               :style-obj (.-style element)))
                      (.componentDidUpdate this))
   :life/did-update (fn [{{style :style
                           class :class}      :view/props
                          {prev-style :style
                           prev-class :class} :view/prev-props
                          :keys               [view/state]}]
                      (let [{:keys [element style-obj]} @state
                            class (some-> (string/split class #"\s+") (set))
                            prev-class (some-> (string/split prev-class #"\s+") (set))
                            styles-removed (set/difference (set (keys prev-style)) (set (keys style)))
                            class-removed (set/difference prev-class class)]
                        (doseq [attr styles-removed]
                          (.setProperty style-obj attr nil))
                        (doseq [[attr val] style]
                          (.setProperty style-obj attr val))
                        (doseq [class class-removed]
                          (classes/remove element class))
                        (doseq [class class]
                          (classes/add element class))))}
  [_]

  [:span.dn])