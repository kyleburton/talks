(ns scratchpad.macros
  (:require
   [clojure.tools.logging :as log]
   [clojure.walk          :as walk]))

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
  ;; using what's already available to us:

  (->>
   [(fn [] (if (takes-random-time 500) :first-post))
    (fn [] (if (takes-random-time 500) :second-post))
    (fn [] (if (takes-random-time 500) :third-post))
    (fn [] (if (takes-random-time 500) :fourth-post))
    (fn [] (if (takes-random-time 500) :fifth-post))]
   (pmap (fn [f] (f)))
   (filter identity)
   first)

  ;; nb: first-post will often be first b/c it begins
  ;; executing first

  ;; another limitation is that pmap processes it's sequence in
  ;; chunks, though this is fine for our purposes here

  ;; What's the accidental complexity in our code there?
  ;; * all those (fn [] ...) parts
  ;; * the pmap, filter and first expressions

  ;; You might be thinking that we can reduce some of this
  ;; by just factoring out the constant parts into a function
  ;; and you'd be right:

  (parallel-first-of
   (fn [] (if (takes-random-time 500) :first-post))
   (fn [] (if (takes-random-time 500) :second-post))
   (fn [] (if (takes-random-time 500) :third-post))
   (fn [] (if (takes-random-time 500) :fourth-post))
   (fn [] (if (takes-random-time 500) :fifth-post)))
  )

(defn parallel-first-of [& fns]
  (->>
   fns
   (pmap (fn [f] (f)))
   (filter identity)
   first))


(comment
  ;; indeed, this works:
  (parallel-first-of
   (fn [] (if (takes-random-time 500) :first-post))
   (fn [] (if (takes-random-time 500) :second-post))
   (fn [] (if (takes-random-time 500) :third-post))
   (fn [] (if (takes-random-time 500) :fourth-post))
   (fn [] (if (takes-random-time 500) :fifth-post)))

  ;; and in fact it's pretty minimal if we use clojures' #() reader
  ;; macro:

  (parallel-first-of
   #(if (takes-random-time 500) :first-post)
   #(if (takes-random-time 500) :second-post)
   #(if (takes-random-time 500) :third-post)
   #(if (takes-random-time 500) :fourth-post)
   #(if (takes-random-time 500) :fifth-post))

  ;; although, we've still got some accidental complexity in those
  ;; lambdas and some extra overhead in the additional function call

  ;; doens't quite look like a cond, my goal is to be able to write:

  (parallel-cond
   (takes-random-time 500) :first-post
   (takes-random-time 500) :second-post
   (takes-random-time 500) :third-post
   (takes-random-time 500) :fourth-post
   (takes-random-time 500) :fifth-post)

  ;; going back to our original, verbose, code:

  (->>
   [(fn [] (if (takes-random-time 500) :first-post))
    (fn [] (if (takes-random-time 500) :second-post))
    (fn [] (if (takes-random-time 500) :third-post))
    (fn [] (if (takes-random-time 500) :fourth-post))
    (fn [] (if (takes-random-time 500) :fifth-post))]
   (pmap (fn [f] (f)))
   (filter identity)
   first)

  ;; we if we start w/that and wrap it with a backquote, we're
  ;; most of the way there:

  (defmacro parallel-cond [& exprs]
    `(->>
      ...but-what-do-we-put-here?...
      (pmap (fn [f] (f)))
      (filter identity)
      first))

  ;; so we've got a sequence of: pred result, pred result, ...
  ;; and we need a vector of:    (fn [] (if pred result))
  ;; we can get a vector with mapv and we can return the code
  ;; for those functions with another backquote:

  )

(defmacro parallel-cond [& exprs]
  `(->>
    ~(mapv (fn [[expr res]] `(fn [] (if ~expr ~res))) (partition 2 exprs))
    (pmap (fn [f#] (f#)))
    (filter identity)
    first))


(comment

  ;; let's see what that looks like:
  (macroexpand
   '(parallel-cond
     (takes-random-time 500) :first-post
     (takes-random-time 500) :second-post
     (takes-random-time 500) :third-post
     (takes-random-time 500) :fourth-post))

  ;; ok I think that looks right (note: I removed all of the "clojure.core/" to make the output easeir on my eyes :)

  ;; (first
  ;;  (filter
  ;;   identity
  ;;   (pmap
  ;;    (fn [f__7498__auto__] (f__7498__auto__))
  ;;    [(fn [] (if (takes-random-time 500) :first-post))
  ;;     (fn [] (if (takes-random-time 500) :second-post))
  ;;     (fn [] (if (takes-random-time 500) :third-post))
  ;;     (fn [] (if (takes-random-time 500) :fourth-post))])))

  ;; lets try it out
  (parallel-cond
   (takes-random-time 500) (do (log/info "first-post")  :first-post)
   (takes-random-time 500) (do (log/info "second-post") :second-post)
   (takes-random-time 500) (do (log/info "third-post")  :third-post)
   (takes-random-time 500) (do (log/info "fourth-post") :fourth-post))

  )

(
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
