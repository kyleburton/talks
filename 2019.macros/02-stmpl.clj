(defmacro stmpl [s]
  (let [sbv (gensym)]
    `(let [~sbv (java.lang.StringBuilder.)]
       ~@(map
          (fn [[ttype tok]]
            (cond
              (= :string ttype)
              `(.append ~sbv ~tok)

              (= :code   ttype)
              `(.append ~sbv (str ~(read-string tok)))))
          (tokenize s "[]"))
       (.toString ~sbv))))
