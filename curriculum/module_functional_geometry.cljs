;;;; Follow-up module based on Peter Henderson's Functional Geometry
;; https://eprints.soton.ac.uk/257577/1/funcgeo2.pdf

;; See `fish` in shapes.cljs



;; Create DSL. We have a choice here of how closely to mirror the paper--it will be confusing and of no use to ape its names and muddle ours.
(defn flip [shape]
  (rotate 180 shape))

(defn rot
  ([shape] (rotate 90 shape)))

(defn rot45 [shape]
  ;; TODO also resize to half
  ;; TODO rotate around the *origin*
  (rotate 45 shape))

;; alias
(def over layer)



;; template for combining
(layer fish
       (position 36 60 (rotate 180 fish)))
