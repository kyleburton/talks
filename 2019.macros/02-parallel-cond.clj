  (let [c (chan 1)]
    (go (and (takes-random-time 100) (>! c :result1)))
    (go (and (takes-random-time 100) (>! c :result2)))
    (go (and (takes-random-time 100) (>! c :result3)))
    (go (and (takes-random-time 100) (>! c :result4)))
    (go (and (takes-random-time 100) (>! c :result5)))
    (let [[result from-where] (alts!! [c (async/timeout 200)])]
      (async/close! c)
      result))
