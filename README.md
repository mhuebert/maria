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

## production build

```shell
lein build-web
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

## Bundling dependencies

In order to work in an ordinary web browser, ClojureScript needs to be
'compiled' (or _converted_) to javascript. During compilation, all project
 files (eg. source code you've written, plus dependencies) are gathered,
 converted to javascript, and mashed into a single file.

 The process of compilation is designed for maximum efficiency: a lot of
 information is stripped out of the original ClojureScript files because it
 isn't necessary for the program to be run. This is optimal for most apps, but
 development tools like Maria are different: we need access to all the original
 information so that we can provide a fully-functioning 'live environment'.

[cljs-live](https://www.github.com/mhuebert/cljs-live) is a tool for bundling
dependencies for the self-hosted ClojureScript compiler while including all of this
extra information.


///\\\///\\\

warning, cljs-live is not yet stable.

///\\\///\\\

To update the live dependency bundles (which also updates sources for the `(source ...)` REPL command):

 1. Make sure planck is installed (this has been tested with version `2.4.0`)
 2. Run `lein build-bundles`

 Dependencies are specified in `live-deps.clj`; This is the file you would edit if
 you wanted to modify or add dependencies.

 Bundles are written to `resources/public/js/cljs_bundles` and are checked into
 version control (this may change in the future as cljs-live stabilizes and we
 develop a better build process).
