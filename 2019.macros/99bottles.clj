(do
  (doseq [ii (range 99 1 -1)]
    (println (format "%d bottles of beer on the wall, %d bottles of beer, take one down, pass it around..." ii ii)))
  (println (format "1 bottle of beer on the wall, 1 bottle of beer, take it down, pass it around..."))
  (println (format "no more bottles of beer on the wall!")))
