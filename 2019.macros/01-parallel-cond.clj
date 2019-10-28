(defn takes-random-time [t]
  (-> t rand long Thread/sleep)
  (zero? (-> 2 rand int)))
