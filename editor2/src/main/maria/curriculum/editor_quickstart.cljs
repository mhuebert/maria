(ns maria.curriculum.editor-quickstart
  "How to use the Maria.cloud editor"
  {:hide-source true}
  (:require [shapes.core :refer :all]))

;; # Editor Quickstart

;; Welcome! Here are some quick pointers about how to use the Maria editor.

;; ### Creating code blocks

;; Maria has "code blocks" and "prose blocks". The text you're reading is in a "prose block", which means it is text meant to explain instead of be evaluated. "Code blocks" are for code to get evaluated.

;; Press the **Enter** (or **Return**) key on a blank line to create a new code block. Try it now: put the cursor at the end of this line, and then press **Enter** twice.

;; ### Selections

;; Maria has some special commands to make it easy to select code.

;; Use **Command/Control** (Mac/PC) to select the form next to the cursor. Use the **Arrow Keys** to expand the selection left, right, and up. The down arrow returns the selection to the start position, one step at a time.

;;    ![](https://i.imgur.com/5t0bFWP.gif)

;; Try it now: put the cursor inside the code block, and try selecting the various elements. Use **Command/Control** + **Arrow Keys** to grow the selection left, right, up and down.

[:a "b" 3]

;; ### Moving Parentheses

;; Since every open-parenthesis needs to be "balanced" with a close-parenthesis for your code to work, Maria does her best to help with that. One thing Maria provides that will help you keep your parens balanced is to use `Shift-Control-<left-or-right-arrow>` (`Shift-Command-<left-or-right-arrow>` on Mac) to extend your parentheses rightward or retract them leftward.

;; Across programming culture this is universally called "slurping" and "barfing" the part of the expression that moves into or out of your parens. Yes, the terms are gross 🤮 but it's what people say.

;; Try it out. Put your cursor inside `(map circle)` and press `Shift-Control-<right-arrow>` (`Shift-Command-<right-arrow>` on Mac) to "slurp" the vector into your `map` expression. Do the same with `left-arrow` to "barf" the vector back out.

(map circle) [25 25 25]


;; ### Evaluating code

;; The most important part! Hold down **Command/Control** and then press **Enter** (or **Return**) key to evaluate the currently selected code. Adding **Shift** evaluates the whole block, even though the selection doesn't change.

;; Try it now, in this code block. From inside the expression, evaluate just one circle, then all three, without moving the cursor:

[(circle 10)
 (circle 20)
 (circle 30)]

;; ### The Command Palette

;; Press **Command/Control-P** to open the command palette, where you can search through all of the commands available in Maria. Results are filtered to only show what is relevant to the current context (i.e. you will see different results depending on if you are in a code or prose block).

;; ### 'Which-Key?'

;; If you hold down a modifier key (`command`, `control`, `option/alt` or `shift`) for one second, the bottom bar will expand to show you all of the commands that you can activate by adding one or more additional keys. E.g., if you hold down `command` for one second, *which-key* will show all of the key shortcuts that 'begin' with the `command` key.

;; ### Reporting bugs

;; At the top-right corner of the screen is a [Bug Report](https://github.com/mhuebert/maria/issues) link. **Please report bugs!** We will be grateful. Maria is very young and still needs a lot of care and attention to become wise and stable.
