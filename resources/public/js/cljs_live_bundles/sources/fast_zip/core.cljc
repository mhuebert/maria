;;   Copyright (c) Rich Hickey. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

;;   functional hierarchical zipper, with navigation, editing and enumeration
;;   see Huet

(ns ^{:doc "Functional hierarchical zipper, with navigation, editing,
  and enumeration.  See Huet"
      :author "Rich Hickey, modified by Alexander K. Hudek"}
  fast-zip.core
  (:refer-clojure :exclude [replace remove next]))

(deftype ZipperOps [branch? children make-node])

(deftype ZipperPath [l r ppath pnodes changed?])

(deftype ZipperLocation [^ZipperOps ops node ^ZipperPath path])

(defn zipper
  "Creates a new zipper structure.

  branch? is a fn that, given a node, returns true if can have
  children, even if it currently doesn't.

  children is a fn that, given a branch node, returns a seq of its
  children.

  make-node is a fn that, given an existing node and a seq of
  children, returns a new branch node with the supplied children.
  root is the root node."
  {:added "1.0"}
  [branch? children make-node root]
  (ZipperLocation. (ZipperOps. branch? children make-node) root nil))

(defn seq-zip
  "Returns a zipper for nested sequences, given a root sequence"
  {:added "1.0"}
  [root]
  (zipper
   seq?
   identity
   (fn [node children] (with-meta children (meta node)))
   root))

(defn vector-zip
  "Returns a zipper for nested vectors, given a root vector"
  {:added "1.0"}
  [root]
  (zipper
   vector?
   seq
   (fn [node children] (with-meta (vec children) (meta node)))
   root))

(defn xml-zip
  "Returns a zipper for xml elements (as from xml/parse),
  given a root element"
  {:added "1.0"}
  [root]
  (zipper
   (complement string?)
   (comp seq :content)
   (fn [node children]
     (assoc node :content (and children (apply vector children))))
   root))

(defn node
  "Returns the node at loc"
  [^ZipperLocation loc]
  (.-node loc))

(defn branch?
  "Returns true if the node at loc is a branch"
  [^ZipperLocation loc]
  ((.-branch? ^ZipperOps (.-ops loc)) (.-node loc)))

(defn children
  "Returns a seq of the children of node at loc, which must be a branch"
  [^ZipperLocation loc]
  ((.-children ^ZipperOps (.-ops loc)) (.-node loc)))

(defn make-node
  "Returns a new branch node, given an existing node and new children.
  The loc is only used to supply the constructor."
  [^ZipperLocation loc node children]
  ((.-make-node ^ZipperOps (.-ops loc)) node children))

(defn path
  "Returns a seq of nodes leading to this loc"
  [^ZipperLocation loc]
  (if-let [^ZipperPath p (.-path loc)] (.-pnodes p)))

(defn lefts
  "Returns a seq of the left siblings of this loc"
  [^ZipperLocation loc]
  (if-let [^ZipperPath p (.-path loc)] (seq (reverse (.-l p)))))

(defn rights
  "Returns a seq of the right siblings of this loc"
  [^ZipperLocation loc]
  (if-let [^ZipperPath p (.-path loc)] (.-r p)))

(defn down
  "Returns the loc of the leftmost child of the node at this loc,
  or nil if no children"
  [^ZipperLocation loc]
  (when (branch? loc)
    (when-let [cs (children loc)]
      (let [node (.-node loc), ^ZipperPath path (.-path loc)]
        (ZipperLocation.
         (.-ops loc)
         (first cs)
         (ZipperPath.
          '()
          #?(:clj (.next ^clojure.lang.ISeq cs) :cljs (cljs.core/next cs))
          path
          (if path (conj (.-pnodes path) node) [node])
          nil))))))

(defn up
  "Returns the loc of the parent of the node at this loc, or nil if at the top"
  [^ZipperLocation loc]
  (let [^ZipperPath path (.-path loc)]
    (when-let [pnodes (and path (.-pnodes path))]
      (let [pnode (peek pnodes)]
        (if (.-changed? path)
          (ZipperLocation.
           (.-ops loc)
           (make-node loc pnode (concat (reverse (.-l path)) (cons (.-node loc) (.-r path))))
           (if-let [^ZipperPath ppath (.-ppath path)]
             (ZipperPath. (.-l ppath) (.-r ppath) (.-ppath ppath) (.-pnodes ppath) true)))
          (ZipperLocation.
           (.-ops loc)
           pnode
           (.-ppath path)))))))

(defn root
  "zips all the way up and returns the root node, reflecting any changes."
  [^ZipperLocation loc]
  (if (#?(:clj identical? :cljs =) :end (.-path loc))
    (.-node loc)
    (let [p (up loc)]
      (if p
        (recur p)
        (.-node loc)))))

(defn right
  "Returns the loc of the right sibling of the node at this loc, or nil"
  [^ZipperLocation loc]
  (let [^ZipperPath path (.-path loc)]
    (when-let [r (and path (.-r path))]
      (ZipperLocation.
       (.-ops loc)
       (first r)
       (ZipperPath.
        (conj (.-l path) (.-node loc))
        #?(:clj (.next ^clojure.lang.ISeq r) :cljs (cljs.core/next r))
        (.-ppath path)
        (.-pnodes path)
        (.-changed? path))))))

(defn rightmost
  "Returns the loc of the rightmost sibling of the node at this loc, or self"
  [^ZipperLocation loc]
  (let [^ZipperPath path (.-path loc)]
    (if-let [r (and path (.-r path))]
      (ZipperLocation.
       (.-ops loc)
       (last r)
       (ZipperPath.
        (apply conj (.-l path) (.-node loc) (butlast r))
        nil
        (.-ppath path)
        (.-pnodes path)
        (.-changed? path)))
      loc)))

(defn left
  "Returns the loc of the left sibling of the node at this loc, or nil"
  [^ZipperLocation loc]
  (let [^ZipperPath path (.-path loc)]
    (when (and path (seq (.-l path)))
      (ZipperLocation.
       (.-ops loc)
       (peek (.-l path))
       (ZipperPath.
        (pop (.-l path))
        (cons (.-node loc) (.-r path))
        (.-ppath path)
        (.-pnodes path)
        (.-changed? path))))))

(defn leftmost
  "Returns the loc of the leftmost sibling of the node at this loc, or self"
  [^ZipperLocation loc]
  (let [^ZipperPath path (.-path loc)]
    (if (and path (seq (.-l path)))
      (ZipperLocation.
       (.-ops loc)
       (last (.-l path))
       (ZipperPath.
        '()
        (concat
         #?(:clj (.next ^clojure.lang.ISeq (reverse (.-l path)))
            :cljs (cljs.core/next (reverse (.-l path))))
         [(.-node loc)] (.-r path))
        (.-ppath path)
        (.-pnodes path)
        (.-changed? path)))
      loc)))

(defn insert-left
  "Inserts the item as the left sibling of the node at this loc, without moving"
  [^ZipperLocation loc item]
  (if-let [^ZipperPath path (.-path loc)]
    (ZipperLocation.
     (.-ops loc)
     (.-node loc)
     (ZipperPath. (conj (.-l path) item) (.-r path) (.-ppath path) (.-pnodes path) true))
    (throw (new #?(:clj Exception :cljs js/Error) "Insert at top"))))

(defn insert-right
  "Inserts the item as the right sibling of the node at this loc, without moving"
  [^ZipperLocation loc item]
  (if-let [^ZipperPath path (.-path loc)]
    (ZipperLocation.
     (.-ops loc)
     (.-node loc)
     (ZipperPath. (.-l path) (cons item (.-r path)) (.-ppath path) (.-pnodes path) true))
    (throw (new #?(:clj Exception :cljs js/Error) "Insert at top"))))

(defn replace
  "Replaces the node at this loc, without moving"
  [^ZipperLocation loc node]
  (ZipperLocation.
   (.-ops loc)
   node
   (if-let [^ZipperPath path (.-path loc)]
     (ZipperPath. (.-l path) (.-r path) (.-ppath path) (.-pnodes path) true))))

(defn insert-child
  "Inserts the item as the leftmost child of the node at this loc, without moving"
  [^ZipperLocation loc item]
  (replace loc (make-node loc (.-node loc) (cons item (children loc)))))

(defn append-child
  "Inserts the item as the rightmost child of the node at this loc, without moving"
  [^ZipperLocation loc item]
  (replace loc (make-node loc (.-node loc) (concat (children loc) [item]))))

(defn next
  "Moves to the next loc in the hierarchy, depth-first. When reaching
  the end, returns a distinguished loc detectable via end?. If already
  at the end, stays there."
  [^ZipperLocation loc]
  (let [path (.-path loc)]
    (if (#?(:clj identical? :cljs =) :end path)
      loc
      (or
       (if (branch? loc) (down loc))
       (right loc)
       (loop [p loc]
         (if-let [u (up p)]
           (or (right u) (recur u))
           (ZipperLocation. (.-ops loc) (.-node p) :end)))))))

(defn prev
  "Moves to the previous loc in the hierarchy, depth-first. If already at the root, returns nil."
  [loc]
  (if-let [lloc (left loc)]
    (loop [loc lloc]
      (if-let [child (and (branch? loc) (down loc))]
        (recur (rightmost child))
        loc))
    (up loc)))

(defn end?
  "Returns true if loc represents the end of a depth-first walk"
  [^ZipperLocation loc]
  (#?(:clj identical? :cljs =) :end (.-path loc)))

(defn remove
  "Removes the node at loc, returning the loc that would have preceded it in a depth-first walk."
  [^ZipperLocation loc]
  (if-let [^ZipperPath path (.-path loc)]
    (if (pos? (count (.-l path)))
      (loop [loc (ZipperLocation.
                  (.-ops loc)
                  (peek (.-l path))
                  (ZipperPath. (pop (.-l path)) (.-r path) (.-ppath path) (.-pnodes path) true))]
        (if-let [child (and (branch? loc) (down loc))]
          (recur (rightmost child))
          loc))
      (ZipperLocation.
       (.-ops loc)
       (make-node loc (peek (.-pnodes path)) (.-r path))
       (if-let [^ZipperPath ppath (.-ppath path)]
         (if ppath (ZipperPath. (.-l ppath) (.-r ppath) (.-ppath ppath) (.-pnodes ppath) true)))))
    (throw (new #?(:clj Exception :cljs js/Error) "Remove at top"))))

(defn edit
  "Replaces the node at this loc with the value of (f node args)"
  [^ZipperLocation loc f & args]
  (replace loc (apply f (.-node loc) args)))
