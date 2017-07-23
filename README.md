# Maria

The ClojureScript coding environment for beginners.


## Why?

>*Our work is not to teach, but to help the absorbent mind in its work of development.* –Maria Montessori

A recurring problem newcomers have with Clojure is that one must learn everything at once: the JVM, and stack traces, and a new editor, and functional programming, and and and.... What if we shielded folks new to Clojure from the complexity of tooling, stack traces, and programming language esoterica? What if we got out of their way and let them feel the power of programming for themselves? This project aims to delay or remove as many of these obstacles as possible so that people can explore.

The Maria editor is a code playground. There is no installation, zero configuration, and explaining how to make it work takes approximately one sentence. Clojure's sharp edges are rounded off, for instance, by wrapping stack traces so errors are presented humanely. A handful of helper functions do the same for types and SVG shapes. We use in-place evaluation because we find it the most effective and humane way to interact with a computer.

The [curriculum](https://github.com/mhuebert/maria/blob/master/doc/pedagogy.md) we use alongside the editor uses progressive disclosure of the language, to prevent overwhelming folks. Instead of explaining all Clojure's many features as a functional, hosted lisp--which is a big topic--we introduce folks to _programming_ and let language features follow. In this spirit, we named the project after pedagogical pioneer [Maria Montessori](https://www.wikiwand.com/en/Maria_Montessori), whose method "stresses the development of a [learner's] own initiative and natural abilities, especially through practical play" and "at their own pace".


## Play

So...please go to [maria.cloud](https://maria.cloud) and play around with it live! :)


## Development

First, compile stylesheets:

```
yarn install;
webpack -p;
```

### standard figwheel

```shell
lein dev
```

### production build

(uses `:optimizations :simple`)

```shell
lein build-web
```

### build fresh dependency bundles

(uses [cljs-live](/mhuebert/cljs-live)  - requires Planck 2.5.0 to be installed)

_Note - this step can be problematic and is unnecessary for a lot of local development._

```shell
lein build-bundles
```

### cursive/IntelliJ figwheel+repl

One-time setup:

1. menu command 'Run > Edit Configurations...'
2. click "+" to add a new configuration
3. choose 'Clojure REPL > local'
5. give it a name, eg/ "Figwheel REPL"
5. choose 'Use clojure.main in normal JVM process'
6. under 'Parameters', enter `script/repl.clj`

Now, you can run the REPL using a hotkey (default is shift+F10), menu command (Run > Run 'Figwheel REPL') or toolbar button.

This will start a development build and webserver, by default accessible at `http://0.0.0.0:3449/`.

### emacs+nrepl figwheel

Bruce now recommends a different method for starting Figwheel with an
nREPL server to allow interactive form evaluation (as opposed to only
recompile on save). The new way is a bit more cumbersome to start, but
also more flexible, and it has been quite robust so far.

First start an nREPL server from the command line at the top level of
the `maria` project hierarchy:

``` shell
lein repl
```

Then, from an emacs buffer open to one of the project files, do a
`cider-connect` and follow the prompts.

Lastly, switch to `*cider-repl-localhost*` and execute these commands
there:

``` clojure
(use 'figwheel-sidecar.repl-api)
(start-figwheel!)
(cljs-repl)
```

At that stage, you should have both a running figwheel and the ability
to evaluate forms in the browser from emacs.
