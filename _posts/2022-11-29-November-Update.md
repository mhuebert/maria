This is my third and final update for the Fall/2022 funding of Maria.cloud by ClojuristsTogether. A huge thank-you to everyone involved for making this possible!

Work concluded this month includes:

- Popovers for adding & editing **links** and **images** graphically.
- Improved **stacktrace view** shows doc/metadata for vars, and highlights relevant code regions on hover.
- **Autocomplete** is now implemented on top of sci.
- Keymaps are now consistent with Maria.cloud and the "eval-region" extension was simplified.
- **Async module loading:** we can now include additional dependencies in a release, to be loaded on-demand via `ns` or `require`. Suggestions/PRs for additional modules are welcome.

My top goals for this 3-month effort were to 

✅ rewrite the document model using a single ProseMirror instance, with code cells handled by a "Node View" using CodeMirror 6,

✅ eliminate all custom "paredit" code in favour of the existing CodeMirror 6 clojure-mode,

✅ use sci instead of the self-hosted compiler.

Along the way I also added some improvements over the existing code:

✅ Top-level "await" for asynchronous document evaluation (primarily so that evaluator waits for `require` and `ns` forms to finish before proceeding, but can now be used for other purposes as well).

✅ A stacktrace viewer that can highlight relevant code.

✅ Curriculum files are now "proper" cljs/sci files complete with namespace declarations, so they can be used from Clojure (when clj-compatibility is complete). This will let students use their own editor to work through the curriculum, should they choose.

Much of this work went very well and even faster than expected, but a large number of other features also need to be updated or re-implemented in order to work with the new code. A few of these remain incomplete, and are blockers for deploying this work to Maria.cloud.

🔲 command-palette

🔲 curriculum browsing & loading

🔲 integration with GitHub (gists) for persistence and sharing

Meanwhile, the latest (in-progress) code continues to be auto-deployed to 2.maria.cloud.
