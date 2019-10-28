(ns scratchpad.macros
  (:require
   [clojure.tools.logging :as log]
   [clojure.walk          :as walk]
   [clojure.core.async    :refer [<!! >! chan go alts!!] :as async]))

(defn flip-coin []
  (-> 2 rand int zero?))

(comment
  ;; horrible examples, please don't do these!  I'm doing them here
  ;; for fun, and as simple examples so you have a sense of wha'ts
  ;; possible

  ;; we might like looking more mathmatical:
  (xlet x 1
        y 2
        ----
        (+ x y))

  )

(defmacro xlet [& forms]
  (let [[bindings _ body]
        (partition-by #(= '---- %) forms)]
    `(let ~(vec bindings)
       ~@body)))

(comment

  (xlet x 1
        y 2
        ----
        (+ x y))

  ;; => 3

  )

(comment

  ;; ... we might looking more like other languages (eg: python, or
  ;; the shell), some folks might find this easier to read:
  (xif (flip-coin)
       :then
       (log/infof "pfft, they'll press their luck and try again")
       (save-results-to-database)
       {:message "You won!"}

       :else
       (log/infof "hah, such a loser")
       (disparage-their-honor-on-the-internet)
       {:message "This is not a winning game piece, please try again!"})

  )


;; IMO it's nice that we've got all of Clojure available to us when
;; writing our macros, using take-while and drop-while here
(defmacro xif [pred & forms]
  (let [[_ & consequent]   (take-while #(not= :else %) forms)
        [_ & otherwise]    (drop-while #(not= :else %) forms)]
    (def xx [forms consequent otherwise])
    `(if ~pred
       (do
         ~@consequent)
       (do
         ~@otherwise))))

(comment

  ;; lets see how that looks
  (macroexpand '(xif (flip-coin)
                     :then
                     (log/infof "pfft, they'll press their luck and try again")
                     (save-results-to-database)
                     {:message "You won!"}

                     :else
                     (log/infof "hah, such a loser")
                     (disparage-their-honor-on-the-internet)
                     {:message "This is not a winning game piece, please try again!"}))


  (if (flip-coin)
    (do
      (log/infof "pfft, they'll press their luck and try again")
      (save-results-to-database)
      {:message "You won!"})
    (do
      (log/infof "hah, such a loser")
      (disparage-their-honor-on-the-internet)
      {:message "This is not a winning game piece, please try again!"}))


  )

;; compute at compile time
(comment

  (let [name       "Kyle"
        curr-year  2019
        birth-year 1971]
    (stmpl "Hi [name], if you were born in [birth-year] that makes you [(- curr-year birth-year)] years old!"))

  (tokenize "Hi [name], if you were born in [year] that makes you [(- curr-year year)] years old!" "[]")

  )

(defn tokenize [s d]
  (let [st (java.util.StringTokenizer. s d)]
    (loop [tokens    []
           ttype     :string]
      (cond
        (.hasMoreTokens st)
        (recur (conj tokens [ttype (.nextToken st)])
               (if (= :string ttype)
                 :code
                 :string))

        :otherwise
        tokens))))

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

(comment

  (macroexpand '(stmpl "Hi [name], if you were born in [year] that makes you [(- curr-year year)] years old!"))
  (let* [G__23771 (java.lang.StringBuilder.)]
    (.append G__23771 "Hi ")
    (.append G__23771 (str name))
    (.append G__23771 ", if you were born in ")
    (.append G__23771 (str year))
    (.append G__23771 " that makes you ")
    (.append G__23771 (str (- curr-year year)))
    (.append G__23771 " years old!")
    (.toString G__23771))

  (let [name       "Kyle"
        curr-year  (.. (java.time.LocalDateTime/now) getYear)
        birth-year 1971]
    (stmpl "Hi [name], if you were born in [birth-year] that makes you [(- curr-year birth-year)] years old!"))

  (.. (java.util.Date.) getYear)



  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; REORDER COMPUTATION

(comment
  (if (not (flip-coin))
    :was-false
    :was-true)

  (unless (flip-coin)
          :was-false
          :was-true)

  )

(defmacro unless [pred consequent otherwise]
  `(if (not ~pred)
     ~consequent
     ~otherwise))

;; these are contrived, though they show that we can re-order
;; computation!

;; what might it look like to have a cond where all the clauses
;; were evaluated in parallel?

;; first, lets define a helper, a function that takes some bounded,
;; random amount of time and also flips a coin to return true/false
(defn takes-random-time [t]
  (-> t rand long Thread/sleep)
  (zero? (-> 2 rand int)))

(comment

  ;; lets try this out a bit
  [(takes-random-time 100)
   (takes-random-time 100)
   (takes-random-time 100)
   (takes-random-time 100)
   (takes-random-time 100)]

  ;; cool, looks like it works as expected

  ;; first, let's start with a functioning example
  ;; using what's already available to us.  Here
  ;; I'm going going to use core.async and a channel
  ;; to grab the first of whichever one of these succeeds

  (let [c (chan 1)]
    (go (and (takes-random-time 100) (>! c :result1)))
    (go (and (takes-random-time 100) (>! c :result2)))
    (go (and (takes-random-time 100) (>! c :result3)))
    (go (and (takes-random-time 100) (>! c :result4)))
    (go (and (takes-random-time 100) (>! c :result5)))
    (let [[result from-where] (alts!! [c (async/timeout 200)])]
      (async/close! c)
      result))

  ;; so our basic shape will be:
  (defmacro parallel-cond [& forms]
    `(let [c (chan 1)]
       (go (and pred1 (>! c result1)))
       ...repeat...
       (let [[result from-where] (alts!! [c (async/timeout 200)])]
         (async/close! c)
         result)))

  (defmacro parallel-cond [& forms]
    `(let [c (chan 1)]
       ~@(map (fn [[pred expr]]
                `(go (and ~pred (>! c ~expr))))
              (partition 2 forms))
       (let [[result from-where] (alts!! [c (async/timeout 200)])]
         (async/close! c)
         result)))

  )


(defmacro parallel-cond [timeout & forms]
  (let [chan-vname (gensym)]
    `(let [~chan-vname (chan 1)]
       ~@(map (fn [[pred expr]]
                `(go (and ~pred (>! ~chan-vname ~expr))))
              (partition 2 forms))
       (let [[result# from-where#] (alts!! [~chan-vname (async/timeout ~timeout)])]
         (async/close! ~chan-vname)
         result#))))

(comment

  ;; let's see what that looks like:
  (macroexpand
   '(parallel-cond 500
                   (takes-random-time 500) :first-post
                   (takes-random-time 500) :second-post
                   (takes-random-time 500) :third-post
                   (takes-random-time 500) :fourth-post))

  ;; ok that looks about right (nb: I removed the namespaces to make this easier to read)
  (let* [G__15507 (chan 1)]
    (go (and (takes-random-time 500) (>! G__15507 :first-post)))
    (go (and (takes-random-time 500) (>! G__15507 :second-post)))
    (go (and (takes-random-time 500) (>! G__15507 :third-post)))
    (go (and (takes-random-time 500) (>! G__15507 :fourth-post)))
    (let [[result__15427__auto__ from-where__15428__auto__] (alts!! [G__15507 (async/timeout 500)])]
      (async/close! G__15507) result__15427__auto__))

  ;; lets try it out
  (parallel-cond
   150
   (takes-random-time 100) (do (log/info "first-post")  :first-post)
   (takes-random-time 100) (do (log/info "second-post") :second-post)
   (takes-random-time 100) (do (log/info "third-post")  :third-post)
   (takes-random-time 100) (do (log/info "fourth-post") :fourth-post)
   (takes-random-time 100) (do (log/info "fifth-post")  :fifth-post))

  )

(comment
  ;; what if we wanted a do form that executed one of its forms at
  ;; random and none of the others?

  ;; starting with a concrete example:

  ((->>
    [(fn [] :first)
     (fn [] :second)
     (fn [] :third)]
    shuffle
    first))


  ;; if we remove all of the acciental complexity from the above I think we'd end up at:

  (do-random
   :first
   :second
   :third)

  ;; the transformations are pretty close to our last example, the overall should look like:

  (defmacro do-random [& exprs]
    `((->>
       [...functions here...]
       shuffle
       first)))

  ;; though a bit less complex since we don't have to partition the macro's arguments:
  )

(defmacro do-random [& exprs]
  `((->>
     ~(mapv (fn [expr] `(fn [] ~expr)) exprs)
     shuffle
     first)))

(comment


  (do-random
   (do (log/infof "first")  :first)
   (do (log/infof "second") :second)
   (do (log/infof "third")  :third))

  )


;; less horrible examples:

(comment

  (with-transaction [db (checkout-db-connection)]
    (do-thing1)
    (do-thing2)
    (do-thing3))

  (defmacro with-transaction [[dbvar dbexpr] & body]
    `(let [~dbvar ~dbexpr]
       (try
         (.startTransaction ~dbvar)
         ~@body
         (.commit ~dbvar)
         (finally
           (.close ~dbvar)))))

  ;; that works but it's kinda ugly, I've come to feel that pairing up
  ;; a function that takes a clojure with an associated macro makes
  ;; situations like this simpler to use, plus it allows the function
  ;; to be used independently of hte macro:

  (defn with-transaction* [db body-fn]
    (try
      (do
        (.startTransaction db)
        (let [res (body-fn)]
          (.commit db)
          res))
      (finally
        (.close db))))

  (defmacro with-transaction [[dbvar dbexpr] & body]
    `(let [~dbvar ~dbexpr]
       (with-transaction ~dbvar (fn [] ~@body))))

  (with-transaction [db (checkout-connection db-pool)]
    (create-purchase-order)
    (update-inventory)
    (send-to-warehouse))

  )


(comment
  ;; pcond was one of the more beautiful and densely expressive macros
  ;; I saw for Common Lisp

  ;; it lifts bindings into the context of the predicates
  ;; supported regex binds as well as destructuring

  ;; :re
  ;; :pat
  (let [order {:sku         "shoes-blue-suede-size-11"
               :description "blue suede shoes"
               :color       "(rgb 00 00 FF)"
               :size        "large"
               :stock       11}]
    (pcond
     (and (:pat {:keys [sku stock]} order)
          (zero? stock))
     (do
       (restock-sku sku)
       (place-on-backorder order))

     (and (:pat {:keys [color]} order)
          (:re #"\(rgb (..) (..) (..)\)" color [red green blue])
          (> (Integer/parseInt blue 16) 200))
     (check-if-customer-alergic-to-blue)

     :otherwise
     (ship-order order)
     ))

  )

;; whoah, how does this have to work?
;; find all the bindings, we can search expressions for
;;  :pat and :re forms then create a cascading let I think ...

(defn pcond-expand-binding [binding]
  (cond
    (-> binding first (= :re))
    (let [[_ regex var bindings] binding]
      `[[~@bindings] (~'rest (~'re-find ~regex ~var))])

    (-> binding first (= :pat))
    (let [[_ bindings var] binding]
      `[~bindings ~var])

    :otherwise
    (throw (RuntimeException. (format "Error: unrecognized pcond binding form: %s" binding)))))

(defn pcond-gen-predicate-bindings [pred]
  (let [bindings (->>
                  (tree-seq sequential? identity pred)
                  (filter #(and (sequential? %)
                                (or
                                 (-> % first (= :re))
                                 (-> % first (= :pat)))))
                  vec)]
    (log/infof "find all the bindings in pred=%s; bindings=%s" pred bindings)
    (mapcat pcond-expand-binding bindings)))

(defn pcond-extract-bindings-from-destructuring-form [form]
  (log/infof "pcond-extract-bindings-from-destructuring-form: form=%s" form)
  (->>
   (tree-seq seqable? seq form)
   (filter symbol?)))


(defn pcond-rewrite-predicate [pred]
  (log/infof "pcond-rewrite-predicate: pred=%s" pred)
  (walk/walk
   (fn [elt]
     (log/infof "visiting: elt=%s" elt)
     (cond
       (and
        (seqable? elt)
        (-> elt first (= :re)))
       `(~'and ~@(nth elt 3))

       (and
        (seqable? elt)
        (-> elt first (= :pat)))
       `(~'and ~@(pcond-extract-bindings-from-destructuring-form (nth elt 1)))

       :otherwise
       elt))
   identity
   pred))

(comment

  (pcond-rewrite-predicate '(and (:pat {:keys [color]} order)
                                 (:re #"\(rgb (..) (..) (..)\)" color [red green blue])
                                 (> (Integer/parseInt blue 16) 200)))
  )

(defn pcond-expand-clauses [[[pred consequent] & clauses]]
  (log/infof "pcond-expand-clauses: pred=%s; consequent=%s" pred consequent)
  (if pred
    `(~'let [~@(pcond-gen-predicate-bindings pred)]
      (if ~(pcond-rewrite-predicate pred)
        ~consequent
        ~(pcond-expand-clauses clauses)))
    nil))

(comment
  (pcond-expand-clauses [['(and (:pat {:keys [color]} order)
                                (:re #"\(rgb (..) (..) (..)\)" color [red green blue])
                                (> (Integer/parseInt blue 16) 200)) :consequent]])


  (let [order {:color "(rgb 01 02 FF)"}]
    (let [{:keys [color]}  order
          [red green blue] (rest (re-find #"\(rgb (..) (..) (..)\)" color))]
      (def xx [color red green blue])
      (if (and (and color) (and red green blue) (> (Integer/parseInt blue 16) 200))
        :consequent
        nil)))

  (pcond-expand-clauses
   [['(and (:pat {:keys [sku stock]} order)
           (zero? stock))
     '(do
        (restock-sku sku)
        (place-on-backorder order))]])

  (let [order {:sku "sku" :stock 0}]
    (let [{:keys [sku stock]} order]
      (if (and (and sku stock) (zero? stock))
        '(do (restock-sku sku) (place-on-backorder order))
        nil)))

  )

(defmacro pcond [& forms]
  ;; must have an even number of forms
  (if-not (-> forms count even?)
    (throw (RuntimeException. (format "Error: must have an even number of forms for pcond!"))))
  (pcond-expand-clauses (partition 2 forms)))


(defn restock-sku [sku]
  (log/infof "RESTOCK sku=%s" sku))

(defn place-on-backorder [order]
  (log/infof "BACKORDER FOR CUSTOMER order=%s" order))

(defn check-if-customer-alergic-to-blue []
  (log/infof "VALIDATE ALERGIES"))

(defn ship-order [order]
  (log/infof "SHIP IT! order=%s" order))

(comment

  (let [order {:sku         "shoes-blue-suede-size-11"
               :description "blue suede shoes"
               :color       "(rgb 00 00 FF)"
               :size        "large"
               :stock       11}]
    (pcond
     (and (:pat {:keys [sku stock]} order)
          (zero? stock))
     (do
       (restock-sku sku)
       (place-on-backorder order))

     (and (:pat {:keys [color]} order)
          (:re #"\(rgb (..) (..) (..)\)" color [red green blue])
          (> (Integer/parseInt blue 16) 200))
     (check-if-customer-alergic-to-blue)

     :otherwise
     (ship-order order)))


  )

(defn process-order [order]
  (pcond
   (and (:pat {:keys [sku stock]} order)
        (zero? stock))
   (do
     (restock-sku sku)
     (place-on-backorder order)
     :backordered)

   (and (:pat {:keys [color]} order)
        (:re #"\(rgb (..) (..) (..)\)" color [red green blue])
        (> (Integer/parseInt blue 16) 200))
   (do
     (check-if-customer-alergic-to-blue)
     :allergy-check)

   :otherwise
   (do
     (ship-order order)
     :fulfilled)))

(comment


  (process-order {:sku         "shoes-blue-suede-size-11"
                  :description "blue suede shoes"
                  :color       "(rgb 00 00 FF)"
                  :size        "large"
                  :stock       11})
  ;; => :allergy-check

  (process-order {:sku         "shoes-red-suede-size-11"
                  :description "blue suede shoes"
                  :color       "(rgb FF 00 00)"
                  :size        "large"
                  :stock       3})
  ;; => :fulfilled


  (process-order {:sku         "shoes-green-suede-size-11"
                  :description "blue suede shoes"
                  :color       "(rgb 00 FF 00)"
                  :size        "large"
                  :stock       0})
  ;; => :backordered


  )


(comment
  (macroexpand '(-> 5 z y x))
  (x (y (z 5)))

  (->>
   ["sort" "this" "by" "length"]
   (map (fn [s] [(count s) s]))
   (sort-by first)
   (mapv second))

  (->>
   ["sort" "this" "by" "length"]
   (map #(list (count %) %))
   (sort-by first)
   (mapv second))

  )
