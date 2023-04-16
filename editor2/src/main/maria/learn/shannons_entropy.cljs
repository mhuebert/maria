(ns maria.learn.shannons-entropy
  {:title "Shannon's Entropy"})
;; # Shannon's entropy calculation

;; [Claude Shannon](https://en.wikipedia.org/wiki/Claude_Shannon)'s measure of entropy is essentially a measure of the unpredictability of a signal that has been converted into a stream of samples. His work was initially concerned with efficient representation of a signal over a network of switching relays, but it has turned out to be of great value in many areas of computer science. Applications include error correcting codes for noisy channels (how many extra bits does it take to tolerate a given level of noise?), data compression (what is the least number of bits that can represent a signal?), machine learning (which of the available sub-samples is the most informative?), and so on. One should also remember that a signal sent from one place to another in space has the same properties as ones stored, for example on a disk, which are in effect signals sent through time.

;; The name "entropy" for this measure was suggested to Shannon by [Von Neumann](https://en.wikipedia.org/wiki/John_von_Neumann) because the calculation is the same as [the one used to measure disorder in quantum statistical mechanics](http://en.wikipedia.org/wiki/Von_Neumann_entropy).

(defn log2
  "log2(x), log2(0) = 0"
  [x]
  (if (= x 0) 0 (/ (Math/log x) (Math/log 2))))

(defn entropy
  "Entropy(S) = -p + log2(p+) - p- log2(p-)"
  [p-pos p-neg]
  (+ (* (- p-pos) (log2 p-pos))
     (* (- p-neg) (log2 p-neg))))

;; The entropy of a signal containing only positive or negative samples is zero. That is, there is no uncertainty and the entire message can be compressed to one bit.

(entropy 1 0)

;; All the samples are positive, so the entropy is 0.

(entropy 0 1)

;; All the samples are negative, so the entropy is 0 again.

;; At the other end of the spectrum is a signal containing exactly half 1s and 0s, which has a statistically random distribution (think of it like a fair coin).

(entropy 1/2 1/2)

;; Half the sample is positive, half the sample is negative, so the entropy is 1.

;; ## The Summarization Metaphor

;; In a machine learning context, one can think of the entropy of the signal as representing how easy it is to summarize it. A very low entropy signal, say one with all zeroes, is very easy to summarize: "it's all zeroes." A complex message with a nearly even number of 1s and 0s is hard to summarize.

;; It is worth considering the obvious metaphor here for human learning and memory formation as a species of data compression in which a series of experiences become a general understanding.

;; Use this routine to play around with lists of 1s and 0s until you have an intuitive understanding of the entropy of different inputs.

(defn seq-entropy
  "Calculate the entropy of sequence 'sq' assuming the all positive
  numbers represent positive samples."
  [sq]
  (let [len (count sq)
        pos (count (filter pos? sq))
        neg (- len pos)]
    (entropy (/ pos len) (/ neg len))))

(seq-entropy '(1 0 1 0 1 1 1 1 1))

;; This should evaluate to 0.7642045065086203

;; ## The Law of Large Numbers

;; Write a function: `(defn random-bit-sequence [len odds] ...)` that returns a random bit sequence with each bit having `odds` probability of being a 1. Use that function to explore how the random sequences converge to the entropy expected from `odds` as `len` increases (i.e. the Law of Large Numbers).

;; Show your work.

(defn random-bit-sequence [len odds]
  ;; when you've worked out the problem, your code goes here
  )

;; ## The Trick Question?  Consider:
(defn string-to-bits
  "Returns a list containing 1s and 0s for the bits that make up the
  bytes of string 's'."
  [s]
  (mapcat #(for [i (range 8)]
             (if (not= 0 (bit-and (int (Math/pow 2 i)) %)) 1 0))
          (.encode (js/TextEncoder.) s)))

(seq-entropy (string-to-bits "    "))
;; This should evaluate to 0.5435644431995964.

(seq-entropy (string-to-bits "abcd"))
;; This should evaluate to 0.9744894033980523.

;; Have a play with the function above and figure out why these strings have these entropy values. This might be a trick question.
