(ns maria.curriculum)

(defn repo-path [path]
  (js/encodeURIComponent (str "https://raw.githubusercontent.com/mhuebert/maria/master/" path)))

(def modules
  [["intro" "Learn Clojure with Shapes" (repo-path "curriculum/Learn Clojure with Shapes.cljs")]
   ["quickstart" "Editor Quickstart" (repo-path "curriculum/Editor Quickstart.cljs")]
   ["gallery" "Example Gallery" (repo-path "curriculum/Example Gallery.cljs")]
   ["animation-quickstart" "Animation Quickstart" (repo-path "curriculum/Animation Quickstart.cljs")]
   ["what-if" "What if?" (repo-path "curriculum/What If.cljs")]
   ["cells" "Welcome to Cells" (repo-path "curriculum/Welcome to Cells.cljs")]
   ["data-flow" "Data Flow" (repo-path "curriculum/Data Flow.cljs")]
   ["shannons-entropy" "Shannon's Entropy" (repo-path "curriculum/Shannon's Entropy.cljs")]
   ["cb-intro" "Learn Clojure with Shapes (ClojureBridge)" (repo-path "curriculum/Learn Clojure with Shapes (ClojureBridge).cljs")]
   ["cb-london" "London ClojureBridge" (js/encodeURIComponent "https://gist.githubusercontent.com/londonclojurians/9afbc2adf3638f1bcf6a6c08b6f33226/raw/clojurebridge-shapes.cljs")]
   ["cb-coaches" "ClojureBridge Coaches' Copy" (repo-path "curriculum/ClojureBridge Coaches' Copy.cljs")]])

(def owner {:username  "curriculum"
            :local-url "/curriculum"})

(def docs (for [[slug friendly-name id] modules]
            {:db/id                id
             :doc.owner/username   "curriculum"
             :owner                owner
             :slug                 slug
             :persistence/provider :maria/curriculum
             :persisted            {:owner owner
                                    :id    id
                                    :files {friendly-name {}}}}))

(def by-id (reduce (fn [m {:keys [db/id] :as item}]
                     (assoc m id item)) {} docs))

(def slugs (set (mapv :slug docs)))
