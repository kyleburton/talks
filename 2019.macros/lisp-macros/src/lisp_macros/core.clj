(ns lisp-macros.core)

(defn pos-val? [n]
  (> n 0))

;; => (def pos-val? (fn ([n] (> n 0))))

;; Clojure's ".." macro
(.. (java.util.Calendar/getInstance)
    getTimeZone
    getRawOffset)

;; Java:
;;   java.util.Calendar.getInstance()
;;     .getTimeZone()
;;     .getRawOffset()

;; note which has fewer parenthesis




(defmacro xwith-open [bindings & body]
  `(try
    (let ~bindings
      ~@body)
    (catch Exception ex
      (try
       (doseq [closeable ~(vec (map first (partition 2 bindings)))]
         (if (not (nil? closeable))
           (.close closeable)))
       (throw ex)
       (catch Exception ex2
         (.initCaused ex2 ex)
         (throw ex2))))))

(comment

  (xwith-open [input (open-input "input.txt")
               output (open-output "output.txt")]
    (.println output "hey, this happened")
    (raise "Sorry honey, something went BOOM."))



  )








(aprog1
    )

(let [value (do-calc)]
  (save-to-db some-name db-calc)
  value)

(aprog1
    (do-calc)
  (save-to-db some-name it))


(let [it (do-calc)]
  (save-to-db some-name it)
  it)

(defmacro aprog1 [first & body]
  `(let [~'it ~first]
     ~@body
     ~'it))


(defmacro xprog1 [sym first & body]
  `(let [~sym ~first]
     ~@body
     ~sym))

(xprog1 xx (do-calc)
  (save-to-db .. xx))

(defmacro xlet [& forms]
  (let [[bindings _ body]
        (partition-by #(= '---- %1) forms)]
    `(let ~(vec bindings)
       ~@body)))

(xlet
 x 1
 y 2
 ----
 (+ x y))


(let [if (some-bool)]
  (if it
    ...))

(aif (some-bool)
     ())






