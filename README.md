# Maria

The ClojureScript coding environment for beginners.


## Why?

>*Our work is not to teach, but to help the absorbent mind in its work of development.* –Maria Montessori

A recurring problem newcomers have with Clojure is that one must learn everything at once: the JVM, and stack traces, and a new editor, and functional programming, and and and.... What if we shielded folks new to Clojure from the complexity of tooling, stack traces, and programming language esoterica? What if we got out of their way and let them feel the power of programming for themselves? This project aims to delay or remove as many of these obstacles as possible so that people can explore.

The Maria editor is a code playground. There is no installation, zero configuration, and explaining how to make it work takes approximately one sentence. Clojure's sharp edges are rounded off, for instance, by wrapping stack traces so errors are presented humanely. A handful of helper functions do the same for types and SVG shapes. We use in-place evaluation because we find it the most effective and humane way to interact with a computer.

The [curriculum](https://github.com/mhuebert/maria/wiki/curriculum) we use alongside the editor uses progressive disclosure of the language, to prevent overwhelming folks. Instead of explaining all Clojure's many features as a functional, hosted lisp--which is a big topic--we introduce folks to _programming_ and let language features follow. In this spirit, we named the project after pedagogical pioneer [Maria Montessori](https://www.wikiwand.com/en/Maria_Montessori), whose method "stresses the development of a [learner's] own initiative and natural abilities, especially through practical play" and "at their own pace".


## Play

So...please go to [maria.cloud](https://maria.cloud) and play around with it live! :)


## Contributing

We welcome your effort to make the beginner's path to Clojure smoother. Here's how:

#### Contributing to fixing bugs
The most helpful thing you can do is simply play around! 😸 and [report bugs as GitHub issues](http://github.com/mhuebert/maria/issues/new).

#### Contributing to better error messages
While you play around, **please send us any un-wrapped Clojure error you find**. We track these in the [Error Handling wiki](https://github.com/mhuebert/maria/wiki/Error-Handling). This is so helpful for making errors easier for new people!

Please edit the wiki directly to add your experience. Or, [file a GitHub issue](http://github.com/mhuebert/maria/issues/new) explaining what you were doing and what error came up. Our goal with this is to protect users from jargon-filled error messages, and that requires lots of eyes pointing out all the ways a particular error could cause trouble.

#### Contributing to the curriculum
Like Clojure itself, the Maria curriculum is open-source, but is not primarily a community effort. We've chosen a particular approach and we write with a particular voice for a particular audience. Therefore other approaches, although valid, may not belong here. With that caveat, those of us who write the curriculum *are* open to contributions, which have been accepted according to these rough guidelines:

 - for typos, bugs, and obvious mistakes, we actively welcome your issues and pull requests
 - if you built something cool, please show us! :) it may end up in our [Gallery](http://www.maria.cloud/gallery?eval=true) of examples, if you're OK with that
 - for more substantial contributions, from suggestions for phrasing up to entirely new modules, we ask that you do the following to be on the same page as us:
   - be familiar with our [Curriculum wiki](https://github.com/mhuebert/maria/wiki/curriculum)
   - be familiar with [Quick: An Introduction to Racket with Pictures](http://docs.racket-lang.org/quick/)
   - be familiar with our existing curriculum: [Learn Clojure with Shapes](http://www.maria.cloud/intro), [Welcome to Cells](http://www.maria.cloud/cells), the [Gallery](http://www.maria.cloud/cells), and all the rest
   - be at least passingly familiar with at least some of the [Secondary Resources](https://github.com/mhuebert/maria/wiki/Curriculum#secondary-resources) listed on our Pedagogy wiki

   With that shared understanding attained, please get in touch with us by email so we can discuss ideas in depth.


## Development

From the `/editor` directory:

```
git clone https://github.com/mhuebert/maria.git;
cd editor;
```

First, install javascript dependencies and compile stylesheets:

```
yarn install;
webpack -p;
```

### build in development mode, start a local server, and live-reload changes:

```shell
yarn run watch;
```

When these builds have completed, open your web browser to http://localhost:8701.

### running a REPL

The default `nrepl` port, configured in `shadow-cljs.edn`, is `7888`.

Once connected, the following will print out a list of things you can do:

```
(require '[shadow.cljs.devtools.api :as shadow])
(shadow/help)
```

to directly enter the `live` environment (provided you've opened up your browser):

```
(shadow.cljs.devtools.api/repl :live)
```

### make a production build:

```shell
yarn run release;
```


