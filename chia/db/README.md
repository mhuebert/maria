# chia.db

![badge](https://img.shields.io/clojars/v/chia.db.svg)

chia.db is a client-side data store for handling global state in ClojureScript apps. It was built in tandem with [chia.view](https://www.github.com/braintripping/chia.view) to support views which automatically update when underlying data changes. It is inspired by Datomic and DataScript.

## Installation

In `project.clj`, include the dependency `[chia.db "xx"]`.

## Background

ClojureScript apps usually store state in [atoms](https://www.chia.view.io/docs/explainers/atoms) which are looked up via namespace or symbol references. With `chia.db`, global state is stored in a single location, as a collection of maps (`entities`), each of which has a unique ID (under the `:db/id` key). Data is read via unique ID, or by indexed attributes. We spend less time thinking about 'locations' in namespaces (or bindings in closures) and more time thinking about data itself.

## Usage

It is normal to use just one chia.db namespace, `chia.db`, for reads and writes throughout an app.

```clj
(ns my-app.core
  (:require [chia.db :as d]))
```

### Writing data

To write data, pass a collection of transactions to `d/transact!`. There are two kinds of transactions.

1. Map transactions are a succinct way to transact entire entities. A map __must__ have a `:db/id` attribute.

    ```clj
    {:db/id 1 
     :name "Matt"}

    ;; usage

    (d/transact! [{:db/id 1 
                   :name "Matt"
                   :website "https://matt.is"}])
    ```

2. Vector transactions allow more fine-grained control.

    ```clj
    ;; add an attribute
    [:db/add <id> <attribute> <value>]

    ;; retract an attribute
    [:db/retract-attr <id> <attribute> <value (optional)>]

    ;; retract an entity
    [:db/retract-entity <id>]

    ;; update an attribute
    [:db/update-attr <id> <attr> <f> <& args>]

    ;; usage

    (d/transact! [[:db/add 1 :name "Matt"]])

    ```

### Reading data

Read a single entity by passing its ID to `d/entity`.

```clj
(d/entity 1)
;; => {:db/id 1, :name "Matt"}
```

An entity pattern read (:e__) is logged.

Read an attribute by passing an ID and attribute to `d/get`.

```clj
(d/get 1 :name)
;; => "Matt"
```

An entity-attribute pattern read (`:ea_`) is logged.

Read nested attributes via `d/get-in`.

```clj
(d/get-in 1 [:address :zip])
```

An entity-attribute pattern read (`:ea_`) is logged.

### Listening for changes in the db

Use `d/listen` to be notified when data which matches a pattern changes. Five patterns are supported:

    Pattern Value        Pattern Key          What it matches
    id                   :e__                 a single entity, by id
    [id attr]            :ea_                 a specific entity-attribute pair
    [attr val]           :_av                 a specific attribute-value pair
    attr                 :_a_                 a specific attribute
   
Pass `d/listen` a map of the form `{<pattern> [<...values...>]}` (see the above table for how values should be formatted for each pattern), along with a listener function to be called when data that matches one of the patterns has changed. A listener will be called at most once per transaction. 

Examples:

```clj
;; entity
(d/listen {:e__ [1]} #(println "The entity with id 1 was changed"))

;; entity-attribute
(d/listen {:ea_ [[1 :name]]} #(println "The :name attribute of entity 1 was changed"))

;; attribute-value
(d/listen {:_av [[:name "Matt"]]} #(println "The value 'Matt' has been removed or added to the :name attribute of an entity"))

;; attribute
(d/listen {:_a_ [:name]} #(println "A :name attribute has been changed"))

;; call d/listen! with a single argument (listener function) to be notified on all changes
(d/listen #(println "The db has changed"))
```

### Indexes

Use `d/merge-schema!` to update indexes.

```clj
(d/merge-schema! {:children {:db/index true, :db/cardinality :db.cardinality/many}})
```

### Finding entities

Use `d/entity-ids` and `d/entities` to find entities which match a collection of predicates, each of which should be:

1. An attribute-value **vector**, to match entities which contain the attribute-value pair. If the attribute is indexed, this will be very fast. Logs an attribute-value pattern read (:_av).

```clj
(d/entity-ids [[:name "Matt"]])
;; or
(d/entities [[:name "Matt"]])
```

2. A **keyword**, to match entities that contain the keyword. Logged as an attribute pattern read (:_a_).

```clj
(d/entity-ids [:name])
;; or
(d/entities [:name])
```

3. A **predicate function**, to match entities for which the predicate returns true.

```clj
(d/entity-ids [:name (fn [{:keys [name]}] (= name "Matt"))])
;; or
(d/entities [:name (fn [{:keys [name]}] (= name "Matt"))])
```

`d/entities` logs an entity pattern read (:e__) for every entity returned.

### Pattern read logging

The `chia.db/capture-patterns` macro logs read patterns which occur during execution. This is to support reactive views which update when underlying data changes.
