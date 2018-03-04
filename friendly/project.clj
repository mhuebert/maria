(defproject maria/friendly "0.1.1-SNAPSHOT"
  :description "Code to soften a few rough edges in Clojure(Script)."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/clojurescript "1.9.854"]]

  :source-paths ["src"]
  :cljsbuild {:builds []}
  :lein-release {:deploy-via :clojars
                 :scm        :git})
