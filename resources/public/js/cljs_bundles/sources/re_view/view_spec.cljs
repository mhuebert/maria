(ns re-view.view-spec
  (:require [re-view.util :as util]
            [clojure.string :as string]
            [clojure.set :as set])
  (:require-macros [re-view.core :as v]))

(def spec-registry
  "Global registry for view specs"
  {})

(defn defspecs
  "Define a view spec"
  [specs]
  (set! spec-registry (merge spec-registry (reduce-kv (fn [m k v]
                                                        (cond-> m
                                                                (not (map? v)) (assoc k {:spec      v
                                                                                         :spec-name k}))) specs specs))))

(def Hiccup? #(and (vector? %)
                   (keyword? (first %))))

(def SVG? #(and (Hiccup? %)
                (string/starts-with? (name (first %)) "svg")))

(def Element? (util/any-pred
                util/is-react-element?
                Hiccup?
                string?
                nil?))

(def builtins [[:Boolean boolean?]
               [:String string?]
               [:Number number?]
               [:Function fn?]
               [:Map map?]
               [:Vector vector?]
               [:Element Element?]
               [:Hiccup Hiccup?]
               [:SVG SVG?]
               [:Object object?]
               [:Keyword keyword?]])

(defspecs (into {} builtins))
(def ^:private spec-kinds (reduce (fn [m [name pred]] (assoc m pred name)) {} builtins))

(defn resolve
  "Resolves a spec. Keywords are looked up in the spec registry recursively until a function or set is found.
  If a map's :spec is a namespaced keyword, it is resolved and merged (without overriding existing keys)"
  [k]
  (cond (keyword? k) (resolve (or (get spec-registry k)
                                  (throw (js/Error (str "View spec not registered: " k)))))
        (set? k) {:spec      k
                  :spec-name :Set}
        (fn? k) {:spec k}
        (map? k) (let [spec (get k :spec)]
                   (if (or (fn? spec)
                           (set? spec))
                     k
                     (merge k (resolve spec))))
        :else (throw (js/Error (str "Invalid spec: " k)))))

(defn spec-kind [{:keys [spec-name spec]}]
  (or (get spec-kinds spec)
      (if (set? spec) :Set
                      spec-name)))

(defn normalize-props-map
  "Resolves specs in map"
  [{:keys [props/keys
           props/required] :as props}]
  (as-> props props
        (reduce-kv (fn [m k v]
                     (assoc m k (resolve v))) props (dissoc props :props/keys :props/required))
        (reduce (fn [m k]
                  (assoc m (keyword (name k)) (resolve k))) props keys)
        (reduce (fn [m k]
                  (assoc m (keyword (name k)) (assoc (resolve k) :required true))) props required)
        (reduce-kv (fn [m k v]
                     (let [{:keys [default pass-through required] :as spec} v]
                       (cond-> (assoc m k spec)
                               (not pass-through) (update :props/consumed conj k)
                               required (update :props/required conj k)
                               default (assoc-in [:props/defaults k] default))))
                   (assoc props :props/consumed []
                                :props/required []) props)))

(defn resolve-spec-vector
  "Resolves specs in vector"
  [specs]
  (when specs (let [[req opt] (split-with (partial not= :&) specs)]
                {:req   (map resolve req)
                 :&more (some-> (second opt) (resolve))})))

(defn validate-spec [k {:keys [required spec spec-name] :as spec-map} value]
  (when (and spec-map (not (fn? spec)) (not (set? spec)))
    (prn :invalid-spec? k spec-map))
  (if (nil? value)
    (when required (throw (js/Error (str "Prop is required: " k))))
    (when (and spec (not (spec value)))
      #_(println "Failed Spec" spec-map)
      #_(prn :spec spec-name :val value :spec spec)
      (throw (js/Error (str "Validation failed for prop: " k " with spec " (or spec-name spec) " and value " value))))))

(defn validate-props [display-name
                      {:keys [props/required]
                       :as   prop-specs} props]
  (let [prop-keys (keys props)]
    (try
      (doseq [k (into required (filterv #(not (#{"props" "spec"} (namespace %))) (keys props)))]
        (validate-spec k (get prop-specs k) (get props k)))
      (catch js/Error e
        (println "Error validating props in " display-name)
        (throw e))))
  props)

(defn validate-children [display-name {:keys [req &more] :as children-spec} children]
  (when children-spec
    (try
      (let [children (util/flatten-seqs children)]
        (loop [remaining-req req
               remaining-children children
               i 0]
          (if (empty? remaining-req)
            (when-not (empty? remaining-children)
              (if &more
                (doseq [child remaining-children]
                  (validate-spec :children-& &more child))
                (throw (js/Error (str "Expected fewer children. Provided " (count children) ", expected " (count req) (when &more " or more") ".")))))
            (if (empty? remaining-children)
              (throw (js/Error (str "Expected more children in " display-name ". Provided " (count children) ", expected " (count req) (when &more " or more") ".")))
              (do (validate-spec (keyword (str "children-" i))
                                 (first remaining-req)
                                 (first remaining-children))
                  (recur (rest remaining-req)
                         (rest remaining-children)
                         (inc i)))))))
      (catch js/Error e
        (.error js/console (str "Error validating children in " display-name))
        (throw (js/Error e))
        )))
  children)