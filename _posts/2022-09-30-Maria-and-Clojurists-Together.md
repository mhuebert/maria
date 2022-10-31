---
redirect_from: /2020/09/30/Maria-and-Clojurists-Together.html
---

We’re excited to announce that I ([Matt](https://twitter.com/mhuebert)) have received a three-month grant from [ClojuristsTogether](https://www.clojuriststogether.org) to work on [Maria.cloud](https://www.maria.cloud). This is the first of three monthly updates.

### What is Maria?

Maria combines carefully written curriculum with a clean, no-install editor. It is designed to introduce complete beginners to fundamental ideas of programming, with an editing environment that encourages REPL-driven development and structural editing from day one. It has been widely appreciated as a good tool for introducing Clojure to beginners and used at several workshops like ClojureBridge.

### What did we propose?

_We built Maria 5 years ago and would like to bring it back into active development, starting with a refactor/simplification of the core of the editor so that we have a good base to build on top of._

_Top priorities would be (1) replace the selfhost compiler with sci for a more lightweight runtime, (2) replace the code editor with CodeMirror 6 using the cm6 clojure mode I wrote last year for Nextjournal, (3) upgrade to latest version of ProseMirror, (4) add a "publish" feature so that people can share what they make with the world. This funding may not cover all of the work listed above, so we'll remain open to receiving funds from elsewhere. This would be the first funding we have ever taken for this project._

### What are some benefits of these changes?

- Using [sci]([url](https://github.com/babashka/sci)) instead of ClojureScript’s self-hosted compiler should dramatically reduce the bundle size and speed up Maria on mobile/old/slow devices.

- Using Nextjournal’s [clojure-mode](https://github.com/nextjournal/clojure-mode) means we depend on a community-supported & standard structural editor and I can delete my own old unmaintained implementation.

- Using [ProseMirror](https://prosemirror.net) to manage a single toplevel doc means we can leverage tools in the ProseMirror ecosystem for new features, eg. [yjs-prosemirror](https://github.com/yjs/y-prosemirror) for real-time collaboration, and I can delete my own old unmaintained Clojure parser and block system.

- Keybindings should be more reliable/stable.

- The Maria codebase should be smaller and easier to work with.

### What has been achieved so far?

Progress is automatically deployed to https://2.maria.cloud on every push to `main`. So far I’ve implemented:

- A ProseMirror view which renders code blocks using CodeMirror 6 with clojure-mode.
- Conversion of Clojure source files into Markdown which can be managed by ProseMirror, and a reverse step to convert Markdown back to Clojure. (This is an inversion of top-level forms - when in “markdown mode”, comments are treated as prose and code is wrapped in fenced code blocks.)
- A sci context that includes Maria’s shapes library
- The REPL tools `doc` and `dir`
- Evaluating selections, blocks, and entire docs via hotkeys
- Showing results next to code, with rendering support for shapes

The editor itself is a nuanced, fidgety thing which requires a lot of careful attention to get right.

### Next steps

- Bring in Maria’s value-viewer code
- Support the cells library
- Test & support remaining curriculum
- Figure out how to integrate ProseMirror/CodeMirror keymaps with Maria’s command bar and “which-key” features
- Integrate or re-implement Maria’s auth & persistence features

### Ancillary tools (aka the scenic route)

In the course of this work I’ve also spent time on a couple of support tools.

In [js-interop](https://github.com/applied-science/js-interop) I’m working on a `j/js` macro, which is like a “deep” version of `j/lit`, meaning that literals become JavaScript data structures ({} => object, [] => array, keywords => strings), and destructuring forms in let/fn/defn are treated as js by default. Literals identified as belonging to Clojure proper or tagged ^:clj are not touched. This was inspired by [@borkdude’s](https://twitter.com/borkdude) experiments in [new cljs compilers](https://github.com/squint-cljs). `j/js` can make interop-heavy code easier to read and write but is not without tradeoffs; one needs to be extra-aware of whether one is looking at code in a “js” or “clj” context. It was particularly helpful in writing code related to ProseMirror/CodeMirror. I'm quite sure I want something like this to exist but the API/behaviour remains in flux. See the [PR](https://github.com/applied-science/js-interop/pull/32).

I’ve resumed work on [yawn](https://github.com/mhuebert/yawn), a hiccup compiler/interpreter that targets React. I was planning to stick with Reagent but it lacks a protocol that would enable custom rendering of arbitrary types, which we need for our viewers (eg. to render shapes properly). Yawn is designed with performance in mind and processes hiccup forms at compile-time where possible. It is REPL-friendly via support for react-refresh, so re-evaluating a view will immediately update on-screen while preserving state & without re-rendering from root.

### The end

Thanks again to Clojurists Together & all its supporters for making this work possible!