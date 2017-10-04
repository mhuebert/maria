;; # Editor Quickstart

;; Welcome! Here are some quick pointers about how to use the Maria editor.

;; ### Creating code blocks

;; Press the **Enter** (or **Return**) key on a blank line to create a new code block. Try it now: put the cursor at the end of this line, and then press **Enter** twice.

;; ### Selections

;; Maria has some special commands to make it easy to select code.

;; 1. Use **Command/Control** (Mac/PC) to select the form next to the cursor. Use the **Arrow Keys** to expand the selection left, right, and up. The down arrow returns the selection to the start position, one step at a time.

;;    ![](https://i.imgur.com/5t0bFWP.gif)

;; 2. Add **Shift** to temporarily expand the selection to the top-level form:

;;    ![](https://i.imgur.com/rZrUupp.gif)

;; Try it now: put the cursor inside the code block, and try selecting the various elements. Use **Command/Control** + **Arrow Keys** to grow the selection left, right, up and down. Add **Shift** to grow the selection to the whole block.

[:a "b" 3]

;; ### Evaluating code

;; The most important part! Hold down **Command/Control** and then press **Enter** (or **Return**) key to evaluate the currently selected code. Just like with selections, adding **Shift** expands the selection to the whole block.

;; Try it now, in this code block:

[(circle 10)
 (circle 20)
 (circle 30)]

;; ### The Command Palette

;; Press **Command/Control-P** to open the command palette, where you can search through all of the commands available in Maria. Results are filtered to only show what is relevant to the current context (ie. you will see different results if you are in a code or prose block).

;; ### 'Which-Key?'

;; If you hold down a modifier key (`command`, `control`, `option/alt` or `shift`) for one second, the bottom bar will expand to show you all of the commands that you can activate by adding one or more additional keys. Eg/ if you hold down `command` for one second, *which-key* will show all of the key shortcuts that 'begin' with the `command` key.

;; ### Reporting bugs

;; At the top-right corner of the screen is a [Bug Report](https://github.com/mhuebert/maria/issues) link. **Please report bugs!** We will be grateful. Maria is very young and still needs a lot of care and attention to become wise and stable.