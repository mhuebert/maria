This is my second update for the Fall/2022 funding of Maria.cloud by ClojuristsTogether. 

Work has progressed well overall, with most of Maria's original curriculum now rendering nicely in the new editor. Highlights include:

- The **Cells** library has been re-implemented to work in sci.
- A large portion of Maria's **value viewing** behaviour has been implemented, including views for shapes and cells.
  - TODO: The plan is to bring in [Clerk](https://github.com/nextjournal/clerk)'s viewer code into Maria so that we can use all of Clerk's [built-in viewers](https://github.clerk.garden/nextjournal/book-of-clerk/commit/70d0459fbe941e689e0c2e7df0afc887eaf5900b/#🔍_viewers), and any custom viewers built for Clerk would also work in Maria.
- I've added support for top-level `await` so that evaluation can pause for blocks that must complete before the rest of the document can run. Vars are handled as a special case, so that one can `await` the value of a `def` form.
  - TODO: implement an `async-load-fn` for sci as described [here](https://github.com/babashka/sci/blob/master/doc/async.md) so that users can import arbitrary js deps and lazy-load ClojureScript namespaces.
- **Namespace lookup** When evaluating within a code block, we determine the current namespace by moving "up" from the node until we find an evaluated ns form (defaulting to `user`). The alternative (default) behaviour would be to use the namespace from the REPL's internal state, which is more likely to confuse users who are looking at a document as a top-to-bottom flow.
- **Namespaces in curriculum**: all Maria docs will now include an `ns` form instead of the previous behaviour of having lots of built-in tools in scope automatically. 
- **doc/arglists coverage** was improved, by [fixing this in sci](https://github.com/babashka/sci/pull/827)
- Implemented the **eldoc** feature for seeing docs/arglists for the current operation
- Implemented UI for editing links and images via a tooltip within prose blocks

For more details see the [commit log](https://github.com/mhuebert/maria/commits/main). 

Thanks again to [ClojuristsTogether](https://www.clojuriststogether.org) for supporting this work!

Remaining high-priority tasks include:

- Command palette (needs to handle ProseMirror, CodeMirror, hybrid & other commands)
- Curriculum (integration into UI / sidebar)
- Persistence (local storage and publishing to GitHub gists)
- Error messages (a lot of the original code for this won't work now that we've changed the evaluator)

— Matt