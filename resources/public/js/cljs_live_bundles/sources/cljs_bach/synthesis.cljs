(ns cljs-bach.synthesis)

(defn audio-context
  "Construct an audio context in a way that works even if it's prefixed."
  []
  (if js/window.AudioContext. ; Some browsers e.g. Safari don't use the unprefixed version yet.
    (js/window.AudioContext.)
    (js/window.webkitAudioContext.)))

(defn current-time
  "Return the current time as recorded by the audio context."
  [context]
  (.-currentTime context))

; Definitions

(defn subgraph
  ([input output] {:input input :output output})
  ([singleton] (subgraph singleton singleton)))

(defn source
  "A graph of synthesis nodes without an input, so another graph can't connect to it."
  [node]
  (subgraph nil node))

(defn sink
  "A graph of synthesis nodes without an output, so it can't connect to another graph."
  [node]
  (subgraph node nil))

; Plumbing

(defn run-with
  "Convert a synth (actually a reader fn) into a concrete subgraph by supplying context and timing."
  [synth context at duration]
  (synth context at duration))

(defn destination
  "The destination of the audio context i.e. the speakers."
  [context at duration]
  (sink (.-destination context)))

(defn plug [param input context at duration]
  "Plug an input into an audio parameter, accepting both numbers and synths."
  (if (number? input)
    (.setValueAtTime param input at)
    (-> input (run-with context at duration) :output (.connect param))))

(defn gain
  "Multiply the signal by level."
  [level]
  (fn [context at duration]
    (subgraph
      (doto (.createGain context)
        (-> .-gain (plug level context at duration))))))

(def pass-through
  "Pass the signal through unaltered."
  (gain 1.0))


; Envelopes

(defn envelope
  "Build an envelope out of [segment-duration final-level] coordinates."
  [& corners]
  (fn [context at duration]
    (let [audio-node (.createGain context)]
      (-> audio-node .-gain (.setValueAtTime 0 at))
      (loop [x at, coordinates corners]
        (when-let [[[dx y] & remaining] coordinates]
          (-> audio-node .-gain (.linearRampToValueAtTime y (+ x dx)))
          (recur (+ dx x) remaining)))
      (subgraph audio-node))))

(defn adshr
  "An ADSR envelope that also lets you specify the hold duration."
  [attack decay sustain hold release]
  (envelope [attack 1.0] [decay sustain] [hold sustain] [release 0]))

(defn adsr
  "A four-stage envelope."
  [attack decay sustain release]
  (fn [context at duration]
    (let [remainder (- duration attack decay sustain)
          hold (max 0.0 remainder)
          node (adshr attack decay sustain hold release)]
      (-> node (run-with context at duration)))))

(defn percussive
  "A simple envelope."
  [attack decay]
  (envelope [attack 1.0] [decay 0.0]))


; Combinators

(defn update-graph
  "Like update-in, but for the node graph a synth will return (and variadic)."
  [f & synths]
  (fn [context at duration]
    (->> synths
         (map #(run-with % context at duration))
         (apply f))))

(defn connect
  "Use the output of one synth as the input to another."
  [upstream-synth downstream-synth]
  (update-graph
    (fn [graph1 graph2]
      (.connect (:output graph1) (:input graph2))
      (subgraph (:input graph1) (:output graph2)))
    upstream-synth
    downstream-synth))

(defn connect->
  "Connect synths in series."
  [& nodes]
  (reduce connect nodes))

(defn join
  "Join the graphs in parallel, with upstream and downstream as the source and sink."
  [upstream downstream & graphs]
  (doseq [graph graphs]
    (.connect (:output graph) (:input downstream))
    (when (:input graph)
      (.connect (:output upstream) (:input graph))))
  (subgraph (:input upstream) (:output downstream)))

(defn add
  "Add together synths by connecting them all to the same upstream and downstream gains."
  [& synths]
    (apply update-graph join pass-through pass-through synths))


; Noise

(defn raw-buffer
  [generate-bit! context duration]
  (let [sample-rate 44100
        frame-count (* sample-rate duration)
        buffer (.createBuffer context 1 frame-count sample-rate)
        data (.getChannelData buffer 0)]
    (doseq [i (range sample-rate)]
      (aset data i (generate-bit! i)))
    buffer))

(def buffer (memoize raw-buffer))

(defn noise
  "Make noise according to the supplied strategy for creating bits."
  [generate-bit!]
  (fn [context at duration]
    (source
      (doto (.createBufferSource context)
        (-> .-buffer (set! (buffer generate-bit! context (+ duration 1.0))))
        (.start at)))))

(def white-noise
  "Random noise."
  (let [white (fn [_] (-> (js/Math.random) (* 2.0) (- 1.0)))]
    (noise white)))

(defn constant
  "Make a constant value by creating noise with a fixed value."
  [x]
  (noise (constantly x)))

; Oscillators

(defn oscillator
  "A periodic wave."
  [type freq]
  (fn [context at duration]
    (source
      (doto (.createOscillator context)
        (-> .-frequency .-value (set! 0))
        (-> .-frequency (plug freq context at duration))
        (-> .-type (set! type))
        (.start at)
        (.stop (+ at duration 1.0)))))) ; Give a bit extra for the release

(def sine (partial oscillator "sine"))
(def sawtooth (partial oscillator "sawtooth"))
(def square (partial oscillator "square"))
(def triangle (partial oscillator "triangle"))


; Filters

(defn biquad-filter
  "Attenuate frequencies beyond the cutoff, and intensify the cutoff frequency based on the value of q."
  ([type freq]
   (biquad-filter type freq 1.0))
  ([type freq q]
   (fn [context at duration]
     (subgraph
       (doto (.createBiquadFilter context)
         (-> .-frequency .-value (set! 0))
         (-> .-frequency (plug freq context at duration))
         (-> .-Q (plug q context at duration))
         (-> .-type (set! type)))))))

(def low-pass (partial biquad-filter "lowpass"))
(def high-pass (partial biquad-filter "highpass"))


; Effects

(defn stereo-panner
  "Pan the signal left (-1) or right (1)."
  [pan]
  (fn [context at duration]
    (subgraph
      (doto (.createStereoPanner context)
        (-> .-pan (plug pan context at duration))))))

(defn delay-line
  "Delay the signal."
  [seconds]
  (fn [context at duration]
    (subgraph
      (let [maximum 5]
        (doto (.createDelay context maximum)
          (-> .-delayTime (plug seconds context at duration)))))))

(defn convolver
  "Linear convolution."
  [generate-bit!]
  (fn [context at duration]
    (subgraph
      (doto (.createConvolver context)
        (-> .-buffer (set! (buffer generate-bit! context (+ duration 1.0))))))))

(def reverb
  "Crude reverb."
  (let [duration 5
        decay 3
        sample-rate 44100
        length (* sample-rate (+ duration 1.0))
        logarithmic-decay (fn [i]
                            (* (-> i (js/Math.random) (* 2.0) (- 1.0))
                               (Math/pow (- 1 (/ i length)) decay)))]
    (convolver logarithmic-decay)))

(defn enhance
  "Mix the original signal with one with the effect applied."
  [effect level]
  (add pass-through (connect-> effect (gain level))))
