(ns cljs-static.shadow
  (:require [cljs-static.assets :as a]
            [com.stuartsierra.dependency :as dep]
            [clojure.tools.reader.edn :as edn]
            [shadow.cljs.devtools.config :as config]
            [clojure.java.shell :refer [sh]]))

(defn get-build [id]
  (config/get-build! id))

(defmacro with-shadow-state [build-state & body]
  `(let [build-state# ~build-state]
     (do ~@body)
     build-state#))

(defn eval-if-fn [f] (if (fn? f) (f) f))

(defn- resolve-sym [sym]
  (require (symbol (namespace sym)))
  (resolve sym))

(defn- resolve-content [form]
  (cond (string? form) form
        (symbol? form) (eval-if-fn @(resolve-sym form))
        (list? form) (let [[f-sym & args] form]
                       (apply @(resolve-sym f-sym) args))
        :else (throw (ex-info "Content must be a string, symbol, or simple function call."
                              {:form form}))))

(defn write-assets!
  "Writes assets to the output-dir of the current shadow build.

   `assets` should be a map of {<path>, <form>}, where `form` will be evaluated.

   Intended for use in a shadow-cljs.edn config file."
  {:shadow.build/stage :flush}
  [build-state assets]
  (if (and (get-in build-state [::generated assets])
           (not (:always? assets)))
    build-state
    (with-shadow-state build-state
      (binding [a/*public-path* (:public-path assets)]
        (doseq [[path content] (dissoc assets :always? :public-path)]
          (a/write-asset! path (resolve-content content))))
      (-> build-state
          (assoc-in [::generated assets] true)))))

(defn transitive-module-deps
  "Give shadow module config, return sorted transitive dependencies, including ks."
  [module-ks modules]
  (let [graph (reduce-kv (fn [graph k {:keys [depends-on]}]
                           (reduce #(dep/depend %1 k %2) graph depends-on))
                         (dep/graph) modules)
        deps (mapcat (partial dep/transitive-dependencies graph) module-ks)]
    (sort (dep/topo-comparator graph) (set (into module-ks deps)))))

(defn read-manifest [{:as   build
                      :keys [output-dir]}]
  (edn/read-string (slurp (str output-dir "/manifest.edn"))))

(def module-index
  (memoize
   (fn [manifest]
     (->> manifest
          (reduce (fn [m {:keys [module-id output-name]}]
                    (assoc m module-id output-name)) {})))))

(defn module-path [build-id module-k]
  "Reads module path for `module-k` from shadow-cljs manifest.edn"
  (let [{:as   build
         :keys [asset-path]} (get-build build-id)
        index (module-index (read-manifest build))]
    (str asset-path "/" (or (index module-k)
                            (throw (ex-info "Module not found" {:key module-k}))))))

(defn module
  "Script tag for module `k`"
  [build-id k]
  [:script {:src (module-path build-id k)}])

(defn modules
  "Sorted list of script tags for modules `ks` and their transitive dependencies."
  [build-id ks & {:keys [exclude]}]
  {:pre [(keyword? build-id) (vector? ks)]}
  (->> (cond->> (transitive-module-deps ks (:modules (config/get-build! build-id)))
                exclude (remove exclude))
       (map (partial module build-id))))

