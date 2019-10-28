  (defmacro parallel-cond [& forms]
    `(let [c (chan 1)]
       ~@(map (fn [[pred expr]]
                `(go (and ~pred (>! c ~expr))))
              (partition 2 forms))
       (let [[result from-where] (alts!! [c (async/timeout 200)])]
         (async/close! c)
         result)))
