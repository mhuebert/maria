(defproject maria/cells "0.1.8-SNAPSHOT"

  :description "Interactive async in ClojureScript."

  :url "https://www.github.com/braintripping/lark/tree/master/cells"

  :license {:name "Mozilla Public License 2.0"
            :url  "https://www.mozilla.org/en-US/MPL/2.0/"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/clojurescript "1.9.946" :scope "provided"]
                 [com.stuartsierra/dependency "0.2.0"]]

  :source-paths ["src"]

  :lein-release {:deploy-via :clojars
                 :scm        :git})
