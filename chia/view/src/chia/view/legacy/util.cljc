(ns chia.view.legacy.util)

(def lifecycle-keys
  "Mapping of methods-map keys to React lifecycle keys."
  {:view/initial-state                  "chia$initialState"
   :view/did-catch                      "componentDidCatch"
   :view/did-mount                      "componentDidMount"
   :view/will-receive-state             "componentWillReceiveState"
   :view/should-update                  "shouldComponentUpdate"
   :view/did-update                     "componentDidUpdate"
   :view/will-unmount                   "componentWillUnmount"
   :view/render                         "render"})