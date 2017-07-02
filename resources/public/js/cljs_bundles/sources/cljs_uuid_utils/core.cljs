;; Copyright (c) Frank Siebenlist. All rights reserved.
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file COPYING at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns cljs-uuid-utils.core
  "ClojureScript micro-library with an implementation of a type 4, random UUID generator compatible with RFC-4122 and cljs.core/UUID (make-random-uuid), a getter function to obtain the uuid string representation from a UUID-instance (uuid-string), a uuid-string conformance validating predicate (valid-uuid?), and a UUID factory from uuid-string with conformance validation (make-uuid-from)."
  (:require [clojure.string :as string]))

;; see https://gist.github.com/4159427 for some background


;; Future UUID-implementations may chose a different internal representation of the UUID-instance
;; The trivial uuid-string function hides those UUID-internals.
;; Further motivation for uuid-string are related to interop thru json or with existing databases.

(defn uuid-string
  "(uuid-string a-uuid)  =>  uuid-str
  Arguments and Values:
  a-uuid --- a cljs.core/UUID instance.
  uuid-str --- returns a string representation of the UUID instance
  Description:
  Returns the string representation of the UUID instance in the format of,
  \"xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx\" similarly to java.util.UUID/toString.
  Note that this is different from cljs.core/UUID's EDN string-format.
  Examples:
  (def u (make-random-uuid))  =>  #uuid \"305e764d-b451-47ae-a90d-5db782ac1f2e\"
  (uuid-string u) => \"305e764d-b451-47ae-a90d-5db782ac1f2e\""
  [a-uuid]
  (str (.-uuid a-uuid)))


(defn make-random-squuid
  "(make-random-squuid)  =>  new-uuid
  Arguments and Values:
  new-squuid --- new type 4 (pseudo randomly generated) cljs.core/UUID instance.
  Description:
  Returns pseudo randomly generated, semi-sequential SQUUID. 
  See http://docs.datomic.com/clojure/#datomic.api/squuid
  Returns a UUID where the most significant 32 bits are the current time since epoch in seconds.
  like: xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx as per http://www.ietf.org/rfc/rfc4122.txt.
  Examples:
  (make-random-squuid)  =>  #uuid \"305e764d-b451-47ae-a90d-5db782ac1f2e\"
  (type (make-random-squuid)) => cljs.core/UUID"
  []
  (letfn [(top-32-bits [] (.toString (int (/ (.getTime (js/Date.)) 1000)) 16))
          (f [] (.toString (rand-int 16) 16))
          (g [] (.toString  (bit-or 0x8 (bit-and 0x3 (rand-int 15))) 16))]
    (UUID.(string/join (concat 
                        (top-32-bits) "-"
                        (repeatedly 4 f) "-4"
                        (repeatedly 3 f) "-"
                        (g) (repeatedly 3 f) "-"
                        (repeatedly 12 f))) nil)))


(defn make-random-uuid
  "(make-random-uuid)  =>  new-uuid
  Arguments and Values:
  new-uuid --- new type 4 (pseudo randomly generated) cljs.core/UUID instance.
  Description:
  Returns pseudo randomly generated UUID,
  like: xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx as per http://www.ietf.org/rfc/rfc4122.txt.
  Examples:
  (make-random-uuid)  =>  #uuid \"305e764d-b451-47ae-a90d-5db782ac1f2e\"
  (type (make-random-uuid)) => cljs.core/UUID"
  []
  (letfn [(f [] (.toString (rand-int 16) 16))
          (g [] (.toString  (bit-or 0x8 (bit-and 0x3 (rand-int 15))) 16))]
    (UUID.(string/join (concat 
                        (repeatedly 8 f) "-"
                        (repeatedly 4 f) "-4"
                        (repeatedly 3 f) "-"
                        (g) (repeatedly 3 f) "-"
                        (repeatedly 12 f))) nil)))


(def ^:private uuid-regex 
  (let [x "[0-9a-fA-F]"] (re-pattern (str 
    "^" x x x x x x x x "-" x x x x "-" x x x x "-" x x x x "-" x x x x x x x x x x x x "$"))))


(defn valid-uuid?
  "(valid-uuid? maybe-uuid)  =>  truthy-falsy
  Arguments and Values:
  maybe-uuid --- string or UUID-instance that may represent a conformant UUID.
  truthy-falsy --- Returns either the conforming UUID-string (truthy) or nil (falsy).
  Description:
  Predicate to test whether a string representation conforms to a
  \"xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx\" format where each x is a hexadecimal character.
  Input can be a maybe-uuid string or a cljs.core/UUID instance.
  Note that the current \"cljs.core/UUID.\" constructor does not check for any conformance.
  Examples:
  (valid-uuid? \"NO-WAY\")  =>  nil
  (valid-uuid? \"4d7332e7-e4c6-4ca5-af91-86336c825e25\")  => \"4d7332e7-e4c6-4ca5-af91-86336c825e25\"
  (valid-uuid? (UUID. \"4d7332e7-e4c6-4ca5-af91-86336c825e25\"))  => \"4d7332e7-e4c6-4ca5-af91-86336c825e25\"
  (valid-uuid? (UUID. \"YES-WAY\"))  => nil"
  [maybe-uuid]
  (let [maybe-uuid-str (cond 
                         (= (type maybe-uuid) cljs.core/UUID) (uuid-string maybe-uuid)
                         (string? maybe-uuid) maybe-uuid
                         :true false)]
    (when maybe-uuid-str (re-find uuid-regex maybe-uuid-str))))


;; java equivalent "java.util.UUID/fromString" throws: IllegalArgumentException Invalid UUID string: ffa2a001-9eec-4224-a64d  java.util.UUID.fromString
;; make-uuid-from should probably throw an exception also instead of silently returning nil...

(defn make-uuid-from
  "(make-uuid-from maybe-uuid maybe-uuid)  =>  uuid-or-nil
  Arguments and Values:
  maybe-uuid --- string or UUID-instance that may represent a conformant UUID.
  uuid-or-nil --- Returns either a cljs.core/UUID instance or nil.
  Description:
  Returns a cljs.core/UUID instance for a conformant UUID-string representation, or nil.
  Input can be a string or a cljs.core/UUID instance.
  Note that if the input UUID-instance is not valid, nil is returned.
  Examples:
  (make-uuid-from \"NO-WAY\")  =>  nil
  (make-uuid-from \"4d7332e7-e4c6-4ca5-af91-86336c825e25\")  => #uuid \"4d7332e7-e4c6-4ca5-af91-86336c825e25\"
  (make-uuid-from (UUID. \"4d7332e7-e4c6-4ca5-af91-86336c825e25\"))  => #uuid \"4d7332e7-e4c6-4ca5-af91-86336c825e25\"
  (make-uuid-from (UUID. \"YES-WAY\"))  => nil"
  [maybe-uuid]
  (when-let [uuid (valid-uuid? maybe-uuid)]
    (if (= (type maybe-uuid) cljs.core/UUID)
      maybe-uuid
      (UUID. uuid nil))))
