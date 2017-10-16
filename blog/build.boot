(set-env!
 :source-paths #{"src" "content"}
 :dependencies '[[perun "0.3.0" :scope "test"]
                 [hiccup "1.0.5"]])

(require '[io.perun :refer :all])
