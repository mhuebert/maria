# Maria

The ClojureScript coding environment for beginners.

>Our work is not to teach, but to help the absorbent mind in its work of development.

–Maria Montessori

## Play

Go to [maria.cloud](maria.cloud) and play around with the live version.

## Development

[![CircleCI](https://circleci.com/gh/mhuebert/maria.svg?style=svg)](https://circleci.com/gh/mhuebert/maria)

First, compile stylesheets:

```
yarn install;
webpack -p;
```

### standard figwheel

``` shell
lein figwheel
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
