(ns maria.editor.doc
  (:require ["prosemirror-markdown" :as md]
            [clojure.string :as str]
            [maria.editor.prosemirror.schema :as markdown]))

;; 1. print markdown normally, but add a marker prefix to code lines.
;; 2. for each line, strip the prefix from code lines, add `;; ` to prose lines.
;; 3. normalize spacing to 1 empty line between blocks.

(defn doc->clj
  "Returns a Clojure string from a ProseMirror markdown doc."
  [md-doc]
  (let [md (markdown/serialize md-doc)
        code? #(str/starts-with? % markdown/code-prefix)]
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

(comment
  (-> "```\n(+ 1 2)\n```" markdown/markdown->doc doc->clj)
  (-> "- hello\n- there" markdown/markdown->doc doc->clj))

(defn md->clj
  "Given a Markdown string, returns Clojure source.
  Code blocks (```) are treated as Clojure code unless marked with a
  non-clj language (eg. ```python). Everything else becomes a line comment."
  [md-string]
  (-> md-string markdown/markdown->doc doc->clj))

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
