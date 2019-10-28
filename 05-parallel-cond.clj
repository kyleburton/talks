(defmacro parallel-cond [timeout & forms]
  (let [chv (gensym)]
    `(let [~chv (chan 1)]
       ~@(map (fn [[pred expr]]
                `(go (and ~pred (>! ~chv ~expr))))
              (partition 2 forms))
       (let [[res# _] (alts!! [~chv (async/timeout ~timeout)])]
         (async/close! ~chv)
         res#))))
