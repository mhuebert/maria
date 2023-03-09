(ns maria.cloud.build
  (:require [babashka.process :as bp]
            [babashka.fs :as fs]
            [cljs-static.page :as page]
            [cljs-static.shadow :as shadow]
            [clojure.string :as str]
            [edamame.core :as eda]))

(defn parse-ns-meta [file]
  (let [src (slurp file)
        [_ns name & args :as form] (eda/parse-string src)
        opts (merge {:name name} (dissoc (meta name) :row :col :end-row :end-col))
        [opts args] (if (string? (first args))
                      [(assoc opts :doc (first args)) (rest args)]
                      [opts args])
        [opts args] (if (map? (first args))
                      [(merge opts (first args)) (rest args)]
                      [opts args])
        opts (if-let [title (or (:title opts)
                                (->> (str/split-lines src)
                                     (drop (:end-row (meta form)))
                                     (drop-while str/blank?)
                                     first
                                     (re-find #";+\s+#+\s*(.*)")
                                     second))]
               (assoc opts :title title)
               opts)]

    opts))

(defn read-curriculum-namespaces []
  (into []
        (comp (map fs/file)
              (map #(-> (parse-ns-meta %)
                        (assoc :path (str "/cljs/" (fs/file-name %)))))
              (remove #(str/ends-with? (:path %) "macros.clj")))
        (fs/list-dir (fs/file "src/main/maria/curriculum"))))

(defn index-html []
  (page/root "Maria"
             {:meta {:viewport "width=device-width, initial-scale=1"}
              :styles [{:href "https://prosemirror.net/css/editor.css"}
                       {:href "/css/tailwind.css"}]
              :scripts/head [{:src "https://polyfill.io/v3/polyfill.min.js?version=3.111.0&features=URLSearchParams%2CURL"}]
              :props/html {:class "bg-[#eeeeee]"}
              :body [:div#maria-live]
              :scripts/body [{:type "application/x-maria:env"
                              :value (str {:maria/curriculum (read-curriculum-namespaces)})}
                             {:src (shadow/module-path :editor :main)}]}))

(defn tailwind-watch!
  {:shadow.build/stage :flush}
  [state]
  (defonce _tailwind (bp/process
                      {:in :inherit
                       :out :inherit
                       :err :inherit
                       :shutdown bp/destroy-tree}
                      "npx tailwindcss -w -i src/maria.cloud.css -o public/css/tailwind.css"))
  state)