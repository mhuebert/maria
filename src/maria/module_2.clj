(ns maria.module-2)

;; HOFs: map, reduce. Thinking recursively.

;; adapted from Steven Luscher https://twitter.com/steveluscher/status/741089564329054208
(map cook [🌽 🐮 🐔])
;; => (🍿 🍔 🍳)

(filter vegetarian? [🍿 🍔 🍳])
;; =>  (🍿 🍳)

(reduce eat [🍿 🍳])
;; => 💩


;;;;; possibly from http://www.globalnerdy.com/2016/06/23/map-filter-and-reduce-explained-using-emoji/ via reddit https://www.reddit.com/r/ProgrammerHumor/comments/55ompo/map_filter_reduce_explained_with_emojis/
(map cook '🐄🍠🐔🌽)
;; => (🍔🍟🍗🍿)

(filter vegetarian? '(🍔🍟🍗🍿))
;; => '(🍟🍿)

(reduce eat '(🍔🍟🍗🍿))
;; => 💩



;; from
;; https://qph.ec.quoracdn.net/main-qimg-7a03304c6e80dd28c3a66e3ece50bceb-c?convert_to_webp=true

(map prepare ingredients)

(reduce (fn [sandwich ingredient]
          (conj sandwich ingredient))
        []
        ingredients)

;; or

(let [slice #(str "sliced " %)
      ingredients (map slice ["bread" "lettuce" "tomato" "onion" "pepper" "turkey"])
      empty-sandwich []]
  (reduce (fn [sandwich ingredient]
            (conj sandwich ingredient))
          empty-sandwich
          ingredients))

["sliced bread" "sliced lettuce" "sliced tomato" "sliced onion" "sliced pepper" "sliced turkey"]
