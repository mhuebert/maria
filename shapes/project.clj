(defproject shapes "0.1.0-SNAPSHOT"
  :description "A shapes library"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/clojurescript "1.9.854"]]

  :source-paths ["src"]
  :cljsbuild {:builds []}
  :deploy-via :clojars)
