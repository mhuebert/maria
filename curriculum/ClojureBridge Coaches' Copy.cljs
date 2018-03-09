;; # ClojureBridge Coaches' Copy

;; This document is for ClojureBridge coaches to prepare for working with learners going through the [ClojureBridge-specific version of Learn Clojure with Shapes](https://www.maria.cloud/intro-cb), which uses minimal text because it assumes a coach (that's you!) is nearby. This saves time reading, at the cost of losing context and a playful, helpful tone. The minimal-text version shares the same curriculum but relies on you the coach to provide encouragmement, explanation, and a guiding hand.

;; Note that some ClojureBridge learners may prefer to use the regular, full-text version of [Learn Clojure with Shapes](https://www.maria.cloud/intro). Leave the choice up to them. It can be helpful to have some students following each version in a group, so that the team can share amongst themselves the perspectives of both the text and the coaches' explanations.


;; ## Pre-Event Preparation

;; 1. Before the workshop, go through the full-text version of [Learn Clojure with Shapes](https://www.maria.cloud/intro) to get a sense of the topics that are covered. Notice also which concepts are _not_ covered and which topics are not covered exhaustively. Teaching is prioritization; coaching is largely about what **not** to mention.
;; 2. Read this document.
;; 3. Explore some of the follow-up learning modules in Maria.cloud, so you can point learners to the one that makes sense for them after they finish with the Learn Clojure with Shapes intro.
;; 4. Finally, skim the ClojureBridge-specific version of Learn Clojure with Shapes [with code blocks pre-evaluated](https://www.maria.cloud/intro-cb?eval=true) to see what learners will see.


;; ## Introductions

;; Before starting with the curriculum, take a few minutes to introduce yourself and let learners introduce themselves. Have icebreaker questions and conversation starters ready. Take your time. A friendly relationship is necessary for students to trust you enough to ask questions.


;; ## First Steps: "Welcome to ClojureBridge!"

;; It's time for learners to try the Maria REPL. The key here is to give them the absolute minimum instruction necessary to get results for themselves.

;; For the first section, we want students to be exposed to the following concepts:

;; 1. the key command to evaluate (`control-enter` (`command-enter` on Mac), and that "evaluate" means to run code so it does something
;; 2. parentheses are used to call functions (e.g. `(circle)`)
;; 3. functions take parameters (e.g. `50`, and other numbers they should try)
;; 4. how to learn more about things: `doc`, parameter list at bottom of screen, `what-is`
;; 5. learners should feel free to experiment with Maria at any time: encourage them to try absurd numbers or nove expressions around. It is *their* playground.

;; Resist the urge to over-explain. Let students direct the conversation. Encourage them to experiment rather than giving them answers.

;; Some useful ideas:

;; - values are "words" and functions make sentences out of the words.
;; - when unsure about a value or expression, try throwing the helper functions at it: `doc`, `what-is`, evaluating it in different ways

;; One classroom-management strategy is to have learners let you know when they reach the end of a section. Of course they can continue if they want, or they can take a quick break and stretch their legs, talk with each other about what they've learned, or ask questions of the coaches. This is also a good time to talk to them about what they'd like to do with programming, either today or eventually. Maybe they can browse the [Gallery](https://www.maria.cloud/gallery?eval=true).


;; ## Starting To Explore: "Shapes 🔺 and Colors 🌈"

;; Once the student has a basic comprehension of the interaction mode used in the maria.cloud REPL, we introduce nested expressions through colored shapes.

;; The goal here is to reinforce the lessons of the first section, encourage experimentation, and to introduce evaluation of sub-expressions. Make sure to have them try switching between evaluating an inner expression and evaluating the top-level expression by adding `Shift` to the key chord.

;; This is often a good time to introduce some form of "inventory": a list of functions the learner has been exposed to. We find this is best kept in a non-digital format like a whiteboard or sheet of paper.

;; Notice that we send learners on a field trip to the [Editor Quickstart](https://maria.cloud/quickstart) to level up their ability to manipulate code. We want to remove barriers to writing code, so some structural editing commands are necessary.

;; Sometimes learners find this a good spot to go off-script and draw a bunch of composite shapes. This is awesome and should be encouraged. Don't railroad people to a set curriculum. Address their questions as they come up.


;; ## Anonymous and Higher Order Functions: "⚡️ Computing Superpowers 💪🏽"

;; The remaining sections keeps most of their text, but learners still benefit from a coach to talk with about the material.

;; It can help to walk the learner through reading the expressions out loud and explaining to _you_ what is going on.

;; Make sure they understand how the `fn` diagram relates to code.


;; ## `let` -> `def` -> `defn`: "👨🏾‍🚀 Names 👩🏻‍🚀"

;; This section is almost unchanged from the fulltext version.

;; Take time to have learners explain the `defn` diagram to you.


;; ## Problem Solving with Code: "🔬 Putting Your Programmer Tools to Work 🔨"

;; The goal of this last section is to give the learner a template to solve future problems with. It remains relatively text-heavy. We want to instill in the learner the habit of incrementally building up manageable chunks of ad-hoc code, then editing them into functions.

;; Learners who want to go off-script at this stage should be strongly encouraged. Solving the problem (or some other challenge) in their own way with interaction with you can be very productive. If they get lost, the curriculum is there to return to.

;; When they finish, celebrate. Make it a big deal.


;; ## The Launchpad: "Where to Go From Here"

;; Our goal now is to provide a platter for the learner to choose from for what to do next. Many of the items on that platter are working with Maria. Other valid options include playing around with 4clojure or other Clojure puzzles, diving into leiningen and Luminus to stamp out a database-backed website, exploring a data set on their laptop with the `clj` REPL, or whatever else they think they can tackle in the time you have.

;; Have a conversation about what they'd like to work on. Some will have a strong idea, others will need some guidance from you. Be prepared to explain and skim some options together.
