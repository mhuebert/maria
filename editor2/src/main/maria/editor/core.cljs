(ns maria.editor.core
  (:require ["prosemirror-view" :refer [EditorView]]
            ["prosemirror-state" :refer [EditorState]]
            ["prosemirror-markdown" :as md]
            ["prosemirror-history" :refer [history]]
            ["prosemirror-dropcursor" :refer [dropCursor]]
            ["prosemirror-gapcursor" :refer [gapCursor]]
            ["prosemirror-schema-list" :as cmd-list]
            ["react-dom" :as react-dom]
            [applied-science.js-interop :as j]
            [applied-science.js-interop.alpha :refer [js]]
            [clojure.string :as str]
            [maria.cloud.menubar :as menubar]
            [maria.editor.code-blocks.NodeView :as node-view]
            [maria.editor.code-blocks.commands :as commands]
            [maria.editor.code-blocks.parse-clj :as parse-clj :refer [clj->md]]
            [maria.editor.code-blocks.sci :as sci]
            [maria.editor.icons :as icons]
            [maria.editor.keymaps :as keymaps]
            [maria.editor.prosemirror.input-rules :as input-rules]
            [maria.editor.prosemirror.links :as links]
            [maria.editor.prosemirror.schema :as markdown]
            [maria.ui :as ui]
            [yawn.hooks :as h]))

;; 1. print markdown normally, but add a marker prefix to code lines.
;; 2. for each line, strip the prefix from code lines, add `;; ` to prose lines.
;; 3. normalize spacing to 1 empty line between blocks.

(def prefix "¡Ⓜ")
(defn prefix-lines [s]
  (str prefix
       (str/replace-all s #"[\n\r]" (str "\n" prefix))))

(defn clojure-block? [lang]
  (re-matches #"(?i)clj\w?|clojure|clojurescript" lang))

(def clj-serializer (js
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

(-> "```\n(+ 1 2)\n```" markdown/md->doc doc->clj)

(defn md->clj
  "Given a Markdown string, returns Clojure source.
  Code blocks (```) are treated as Clojure code unless marked with a
  non-clj language (eg. ```python). Everything else becomes a line comment."
  [md-string]
  (-> md-string markdown/md->doc doc->clj))

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

(js
  (defn plugins []
    [keymaps/prose-keymap
     keymaps/default-keys
     input-rules/maria-rules
     (dropCursor)
     (gapCursor)
     (history)
     ~@(links/plugins)]))

(defn init [{:as opts :keys [initial-value
                             make-sci-ctx]
             :or {make-sci-ctx sci/initial-context}}
            ^js element]
  (let [state (js (.create EditorState {:doc (-> initial-value
                                                 parse-clj/clj->md
                                                 markdown/md->doc)
                                        :plugins (plugins)}))
        view (-> (js (EditorView. element {:state state
                                           :nodeViews {:code_block node-view/editor}
                                           ;; no-op tx for debugging
                                           #_#_:dispatchTransaction (fn [tx]
                                                                      (this-as ^js view
                                                                        (let [state (.apply (.-state view) tx)]
                                                                          (.updateState view state))))}))
                 (j/assoc! :!sci-ctx (atom (make-sci-ctx))))]
    (commands/prose:eval-doc! view)
    #(j/call view :destroy)))

(ui/defview editor [options]
  [:<>

   [menubar/title!
    [:div.m-1.px-3.py-1.bg-zinc-100.border.border-zinc-200.rounded
     (:title options)
     [icons/chevron-down:mini "w-4 h-4 ml-1 -mr-1 text-zinc-500"]]]

   (let [!ref (h/use-ref nil)]
     (h/use-effect
      (fn [] (some->> @!ref (init options)))
      [@!ref (:initial-value options)])

     [:div.relative.notebook {:ref !ref}])])

#_(defn ^:dev/before-load clear-console []
    (.clear js/console))
