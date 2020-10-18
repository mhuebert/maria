

### Changed from re-view

- The implementation is based on hooks. The old `defview` macro is deprecated but kept around in the `chia.view.legacy` namespace for a limited form of backwards compatibility. The new primary interface for creating views is `v/defn`.
    
    How does `v/defn` compare to `v/defview`?

    - View functions can be multi-arity. Arglists are not modified in any way, there is no `this` passed in as the component instance.
    - There is no single "state atom" per-component, instead we use the `(v/use-state <initial-state>)` hook.
    - Hooks are used for lifecycle-related methods.
- CLJS-friendly wrappers around all the main hooks have been provided. Hooks cannot be easily used out-of-the-box without some nuanced massaging: https://github.com/mhuebert/chia/blob/master/view/src/chia/view/hooks.cljs#L13
- There is no hard dependency on `chia.db`, a new `chia.reactive` namespace handles reactive dependencies in a general way. Reactive data sources based on `chia.reactive` do not have the same limitations as React hooks - they can be used within loops / conditions.
- Support for React contexts


Other features/differences:

- Component instantiation order is tracked. We do this to ensure that when we flush the render queue, parents render before their children, preventing double-renders.
- Changes to Hiccup syntax:
  - Vector syntax supported for calling Chia views: [my-view {..props..} & children]
  - #js array syntax for interop with other React components: #js [myComponent {..props..} & children]
    - If `props` is a Clojure map, it will be converted to a javascript map with the same formatting as Chia components.

### This is a work in progress

The hooks api has not been finalized. 
