# Maria

The ClojureScript coding environment for beginners.


## Why?

>*Our work is not to teach, but to help the absorbent mind in its work of development.* –Maria Montessori

A recurring problem newcomers have with Clojure is that one must learn everything at once: the JVM, and stack traces, and a new editor, and functional programming, and and and.... What if we shielded folks new to Clojure from the complexity of tooling, stack traces, and programming language esoterica? What if we got out of their way and let them feel the power of programming for themselves? This project aims to delay or remove as many of these obstacles as possible so that people can explore.

The Maria editor is a code playground. There is no installation, zero configuration, and explaining how to make it work takes approximately one sentence. Clojure's sharp edges are rounded off, for instance, by wrapping stack traces so errors are presented humanely. A handful of helper functions do the same for types and SVG shapes. We use in-place evaluation because we find it the most effective and humane way to interact with a computer.

The [curriculum](https://github.com/mhuebert/maria/blob/master/doc/pedagogy.md) we use alongside the editor uses progressive disclosure of the language, to prevent overwhelming folks. Instead of explaining all Clojure's many features as a functional, hosted lisp--which is a big topic--we introduce folks to _programming_ and let language features follow. In this spirit, we named the project after pedagogical pioneer [Maria Montessori](https://www.wikiwand.com/en/Maria_Montessori), whose method "stresses the development of a [learner's] own initiative and natural abilities, especially through practical play" and "at their own pace".


## Play

So...please go to [maria.cloud](maria.cloud) and play around with it live! :)


## Development

[![CircleCI](https://circleci.com/gh/mhuebert/maria.svg?style=svg)](https://circleci.com/gh/mhuebert/maria)

First, compile stylesheets:

```
yarn install;
webpack -p;
```

### standard figwheel

``` shell
;; compile project & run dev server
lein dev
```

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

### cljs-live deps

///\\\///\\\

warning, cljs-live is still unstable and difficult to use

///\\\///\\\

Dependencies for the self-hosted ClojureScript compiler are compiled using
[cljs-live](https://www.github.com/mhuebert/cljs-live), with deps specified
 in `live-deps.clj` and compiled to `resources/public/js/cljs_live_cache_core.js`.

 1. Make sure planck is installed
 2. Clone (git@github.com:mhuebert/cljs-live.git) and put a symlink to bundle.sh on your path
 3. In this directory (maria), run `bootstrap.cljs --deps live-deps.clj` to generate an updated version of `resources/public/js/cljs_live_cache.js`

 (Inspect live-deps.clj to see what is included in the cache)
