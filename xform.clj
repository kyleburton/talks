(->>
 ["sort" "this" "by" "length"]
 (map (fn [s] [(count s) s]))
 (sort-by first)
 (mapv second))
