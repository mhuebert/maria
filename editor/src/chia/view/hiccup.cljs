(ns chia.view.hiccup
  (:require ["react" :as react]
            [applied-science.js-interop :as j]
            [chia.view.hiccup.impl :as hiccup]
            [chia.util.perf :as perf]
            [chia.util :as u]))

(def -react-element react/createElement)
(def -react-fragment react/Fragment)
(def -react-element? react/isValidElement)

(defprotocol IElement
  (-to-element [this] "Returns a React element representing `this`"))

(declare to-element)

(defonce ^:private sentinel #js{})

(defn make-element
  "Returns a React element. `tag` may be a string or a React component (a class or a function).
   Children will be read from `form` beginning at index `start`."
  [tag js-props form start]
  (let [form-count (count form)]
    (case (- form-count start)                              ;; fast cases for small numbers of children
      0 (-react-element tag js-props)
      1 (let [first-child (nth form start)]
          (if (seq? first-child)
            ;; a single seq child should not create intermediate fragment
            (make-element tag js-props (vec first-child) 0)
            (-react-element tag js-props (to-element first-child))))
      (let [out #js[tag js-props]]
        (loop [i start]
          (if (== i form-count)
            (.apply -react-element nil out)
            (do
              (.push out (to-element (nth form i)))
              (recur (inc i)))))))))

(defonce sentinel #js{})

(defn props? [props]
  (not (identical? props sentinel)))

(defn get-props
  "Returns props at index `i` in `form`, or a sentinel value if props were not found.
   Props can be `nil` or a Clojure map.
   Call `props?` on the result to determine if props were found.
   Props can be nil or a Clojure map."
  [form i]
  {:post [(or (identical? % sentinel) ((u/nilable map?) %))]}
  (let [props (nth form i sentinel)]
    (if (identical? props sentinel)
      sentinel
      (if (and (or (nil? props) (map? props))
               (not (satisfies? IElement props)))
        props
        sentinel))))

(defn to-element [form]
  (cond (vector? form) (let [tag (-nth form 0)]
                         (cond (fn? tag) (to-element (apply tag (rest form)))
                               (keyword? tag)
                               (if (perf/identical? :<> tag)
                                 (make-element -react-fragment nil form 1)
                                 (let [parsed-key (hiccup/parse-key-memo (name tag))
                                       props (get-props form 1)
                                       props? (props? props)]
                                   (make-element (.-tag parsed-key)
                                                 (hiccup/props->js parsed-key (when props? props))
                                                 form
                                                 (if props? 2 1))))

                               :else (let [props (get-props form 1)
                                           props? (props? props)]
                                       (make-element tag
                                                     (when props? (hiccup/props->js props))
                                                     form
                                                     (if props? 2 1)))))

        (seq? form) (make-element -react-fragment nil form 0)

        (satisfies? IElement form) (-to-element form)

        (array? form) (let [props (get-props form 1)
                            props? (props? props)]
                        (make-element (aget form 0)
                                      (when props? (hiccup/props->js props))
                                      form
                                      (if props? 2 1)))

        :else form))

(defn update-props [el f & args]
  {:pre [(vector? el)]}
  (let [props (get-props el 1)
        props? (props? props)]
    (if props?
      (assoc el 1 (apply f props args))
      (into [(el 0) (apply f {} args)] (subvec el 1)))))

(defn element
  "Converts Hiccup form into a React element. If a non-vector form
   is supplied, it is returned untouched. Attribute and style keys
   are converted from `dashed-names` to `camelCase` as spec'd by React.

   - optional -
   :wrap-props (fn) is applied to all props maps during parsing.
   :create-element (fn) overrides React.createElement."
  ([form]
   (to-element form))
  ([{:keys [wrap-props]} form]
   (binding [hiccup/*wrap-props* wrap-props]
     (to-element form))))



;; patch IPrintWithWriter to print javascript symbols without throwing errors
(when (exists? js/Symbol)
  (extend-protocol IPrintWithWriter
    js/Symbol
    (-pr-writer [sym writer _]
      (-write writer (str "\"" (.toString sym) "\"")))))