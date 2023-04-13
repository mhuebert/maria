(ns maria.cloud.build
  (:require [babashka.fs :as fs]
            [babashka.process :as bp]
            [cljs-static.page :as page]
            [cljs-static.shadow :as shadow]
            [cljs-static.assets :as assets]
            [clojure.string :as str]
            [edamame.core :as eda]
            [re-db.schema :as schema]))

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

(defn current-sha
  "Returns SHA for current git commit"
  []
  (-> (bp/sh "git" "rev-parse" "HEAD")
      :out
      str/trim))

(defn read-curriculum-namespaces []
  (mapv #(let [file (fs/file %)
               file-name (fs/file-name file)
               m (parse-ns-meta file)]
           (assoc m :curriculum/file-name file-name
                    :curriculum/name (last (str/split (str (:name m)) #"\."))
                    :curriculum/hash (assets/md5 (slurp file))))
        (fs/list-dir (fs/file "src/main/maria/curriculum"))))

(defn index-html []

  (page/root "Maria"
             {:meta {:viewport "width=device-width, initial-scale=1"}
              :styles [{:href "/css/maria.cloud.css"}]
              :scripts/head [{:src "https://polyfill.io/v3/polyfill.min.js?version=3.111.0&features=URLSearchParams%2CURL"}]
              :props/html {:class "bg-neutral-100"}
              :body [:div#maria-live]
              :scripts/body [{:type "application/re-db:schema"
                              :value (str {:curriculum/name (merge schema/unique-id
                                                                        schema/string)})}
                             {:type "application/re-db:tx"
                              :value (conj (read-curriculum-namespaces)
                                           {:db/id :maria.cloud/env
                                            :git/sha (current-sha)})}
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