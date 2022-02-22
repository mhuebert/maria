(ns chia.view.hiccup.spec
  (:require [clojure.spec.alpha :as s :include-macros true]
            [clojure.spec.gen.alpha :as gen]
            [clojure.string :as string]
            [chia.view.hiccup :as hiccup]
            ["react" :as react]))


(defn gen-wrap
  "Wraps return values of generator of expr with f"
  [expr f]
  (s/with-gen expr #(gen/fmap f (s/gen expr))))

(defn gen-set
  "With generator from set"
  [expr set]
  (s/with-gen expr #(s/gen set)))

(s/def ::primitive (s/nilable (s/or :string string?
                                    :number number?
                                    :boolean boolean?)))

(s/def ::fn (-> fn?
                (gen-set #{identity})))

(s/def ::tag (-> keyword?
                 (gen-set #{:div :span :a})))

(s/def ::not-camelCase #(not (re-find #"[a-z][A-Z][a-z]" (str %))))

(s/def ::style-key (-> (s/and keyword?
                              ::not-camelCase)
                       (gen-set #{:font-size :color :background-color})))

(s/def ::style-map (s/or :map (s/map-of ::style-key ::primitive :gen-max 5 :conform-keys true)))

(s/def ::prop-key (-> keyword?
                      (gen-set #{:class :width :height})))

(s/def ::listener-key (-> (s/and keyword?
                                 #(string/starts-with? (name %) "on-"))
                          (gen-set #{:on-click})))

(s/def ::prop-map (s/every (s/or :nil (s/tuple keyword? nil?)
                                 :style-prop (s/tuple #{:style} ::style-map)
                                 :classes (s/tuple #{:classes} (s/or :vec vector?
                                                                     :set set?
                                                                     :seq seq?
                                                                     :list list?))
                                 :ref (s/tuple #{:ref} fn?)
                                 :key (s/tuple #{:key} (s/or :string string?
                                                             :number number?))
                                 :dangerous-html (s/tuple #{:dangerouslySetInnerHTML} (s/map-of #{:__html} string?))
                                 :listener (s/tuple ::listener-key ::fn)
                                 :prop (s/tuple ::prop-key ::primitive))
                           :kind map?
                           :into {}
                           :gen-max 10))

(defn is-valid-element? [x]
  (and (object? x)
       (or (boolean (aget x "c$view"))
           (react/isValidElement x))))

(s/def ::native-element (-> is-valid-element?
                            (gen-set #{#js {"c$view" #js {}}})))

(s/def ::element (s/or :element ::native-element
                       :hiccup ::hiccup-element
                       :primitive ::primitive
                       :element-list (s/coll-of ::element :into () :gen-max 5)))

(s/def ::hiccup-element
  (-> (s/and vector?
             (s/cat :tag ::tag
                    :props (s/? ::prop-map)
                    :body (s/* ::element)))
      (gen-wrap vec)))


(s/fdef hiccup/element
        :args (s/cat :opts (s/? (s/keys :opt-un [::fn]))
                     :body ::element)
        :ret (s/or :element ::native-element
                   :primitive ::primitive))

#_(doall (->>
           ;(s/exercise ::element 6)
           (s/exercise-fn chia.view.hiccup/element)
           (map prn)))


