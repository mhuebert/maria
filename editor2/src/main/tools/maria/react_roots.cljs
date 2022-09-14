(ns tools.maria.react-roots)

(defonce !react-roots (atom {}))

(defn ^:dev/after-load render []
  (doseq [[^js root form-fn] @!react-roots] (.render root (form-fn))))

(defn init! [^js root form-fn]
  (.render root (form-fn))
  (swap! !react-roots assoc root form-fn))

(defn unmount! [^js root]
  (.unmount root)
  (swap! !react-roots dissoc root))
