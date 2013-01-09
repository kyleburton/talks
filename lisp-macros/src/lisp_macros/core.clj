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

