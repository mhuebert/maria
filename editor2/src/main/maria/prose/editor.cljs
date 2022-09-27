(ns maria.prose.editor
  (:require [applied-science.js-interop :as j]
            [tools.maria.component :refer [with-element]]
            [maria.prose.input-rules :as input-rules]
            [maria.keymap :as keys]
            [maria.style :as style]
            [maria.code.parse-clj :as parse-clj :refer [clj->md]]
            ["react" :as re]
            ["react-dom/client" :refer [createRoot]]
            ["prosemirror-view" :refer [EditorView]]
            ["prosemirror-state" :refer [EditorState]]
            ["prosemirror-markdown" :as md]
            ["prosemirror-example-setup" :refer [exampleSetup]]
            ["prosemirror-history" :refer [history]]
            ["prosemirror-dropcursor" :refer [dropCursor]]
            ["prosemirror-gapcursor" :refer [gapCursor]]
            ["prosemirror-schema-list" :as cmd-list]
            [clojure.string :as str]
            [maria.code.NodeView :as node-view]))

(defn md->doc [source] (.parse md/defaultMarkdownParser source))
(defn doc->md [doc] (.serialize md/defaultMarkdownSerializer doc))

;; 1. print markdown normally, but add a marker prefix to code lines.
;; 2. for each line, strip the prefix from code lines, add `;; ` to prose lines.
;; 3. normalize spacing to 1 empty line between blocks.

(def prefix "¡Ⓜ")
(defn prefix-lines [s]
  (str prefix
       (str/replace-all s #"[\n\r]" (str "\n" prefix))))

(defn clojure-block? [lang]
  (re-matches #"(?i)clj\w?|clojure|clojurescript" lang))

(def clj-serializer (j/js
                      (let [{:keys [nodes marks]
                             {original :code_block} :nodes} md/defaultMarkdownSerializer]
                        (new md/MarkdownSerializer
                             (j/extend! {}
                               nodes
                               {:code_block
                                (fn [{:as state :keys [delim]}
                                     {:as node :keys [textContent] {lang :params} :attrs}]
                                  (if (and (str/blank? delim)
                                           (or (clojure-block? lang)
                                               (str/blank? lang)))
                                    (do
                                      (when-not (str/blank? textContent)
                                        (.text state (-> textContent str/trim prefix-lines) false))
                                      (.closeBlock state node))
                                    (original state node)))})
                             marks))))

(defn doc->clj
  "Returns a Clojure string from a ProseMirror markdown doc."
  [md-doc]
  (let [md (.serialize clj-serializer md-doc)
        code? #(str/starts-with? % prefix)]
    (->> (str/split-lines md)
         (partition-by code?)
         (keep (fn [ss]
                 (if (code? (first ss))
                   (str/join \newline (map #(subs % 2) ss))
                   ;; trim prose of leading/trailing newlines
                   (let [ss (cond->> ss (str/blank? (first ss)) (drop 1))
                         ss (cond->> ss (str/blank? (last ss)) (drop-last))]
                     (when (seq ss)
                       (str/join \newline (map #(str ";; " (str/trim %)) ss)))))))
         (str/join "\n\n"))))

(-> "```\n(+ 1 2)\n```" md->doc doc->clj)

(defn md->clj
  "Given a Markdown string, returns Clojure source.
  Code blocks (```) are treated as Clojure code unless marked with a
  non-clj language (eg. ```python). Everything else becomes a line comment."
  [md-string]
  (-> md-string md->doc doc->clj))

(comment
 (= (md->clj "# hello")
    ";; # hello")
 (= (clj->md ";; # hello")
    "# hello")
 (= (md->clj "```\n(+ 1 2)\n```")
    "(+ 1 2)")
 (= (md->clj "Hello\n```\n(+ 1 2)\n```")
    ";; Hello\n\n(+ 1 2)")
 (= (md->clj "- I see\n```\n1\n```")
    ";; * I see\n\n1")
 (= (md->clj "```\na\n```\n\n```\nb\n```")
    "a\n\nb"))

(defn plugins []
  #js[keys/prose-keys
      keys/default-keys
      input-rules/maria-rules
      (dropCursor)
      (gapCursor)
      (history)])

(defn editor [{:keys [source]}]
  (with-element {:el style/prose-element}
    (fn [^js element]

      (let [state (j/js
                    (.create EditorState {:doc (-> source
                                                   parse-clj/clj->md
                                                   md->doc)
                                          :plugins (plugins)}))
            view (j/js
                   (EditorView. element {:state state
                                         :nodeViews {:code_block node-view/editor}

                                         ;; no-op tx for debugging
                                         #_#_:dispatchTransaction (fn [tx]
                                                                    (this-as ^js view
                                                                      (let [state (.apply (.-state view) tx)]
                                                                        (.updateState view state))))}))]
        (fn [] (j/call view :destroy))))))

#_(defn ^:dev/before-load clear-console []
    (.clear js/console))
