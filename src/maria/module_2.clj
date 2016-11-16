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
