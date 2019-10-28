  (defmacro parallel-cond [& forms]
    `(let [c (chan 1)]
       (go (and pred1 (>! c result1)))
       ...repeat...
       (let [[result from-where] (alts!! [c (async/timeout 200)])]
         (async/close! c)
         result)))
