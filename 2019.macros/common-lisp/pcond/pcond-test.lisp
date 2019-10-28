(cl:in-package :pcond-test)

(remove-all-tests)

(define-test cond
  (assert-eq 'right
	     (pcond
	      (nil 'wrong)
	      (t 'right))))

(define-test cond2
  (assert-eq 'right
	     (pcond
	      (nil 'wrong)
	      ((:re "ab(c)" "abc")
	       'right)
	      (t nil))))

(define-test cond3
  (assert-eq 'right
	     (pcond
	      (nil 'wrong)
	      ((and t (:re "ab(c)" "abc"))
	       'right)
	      (t nil))))

(define-test cond4
  (assert-eq 'right
	     (pcond
	      (nil 'nil-wrong)
	      ((:re "ab(c)" "abc" (c))
	       (when (equal "c" c)
		 'right))
	      (t 't-wrong))))

(define-test cond5
  (assert-eq 'right
	     (pcond
	      (nil 'nil-wrong)
	      ((and t (:re "ab(c)" "abc" (c)))
	       (when (equal "c" c)
		 'right))
	      (t 't-wrong))))

(define-test cond6
  (assert-eq 'right
	     (pcond
	      (nil 'nil-wrong)
	      ((and t (:re "ab(d+)" "abddddd" (d)) (:re "ab(c)" "abc" (c)))
	       (when (and (equal "c" c) (= 5 (length d)))
		 'right))
	      (t 't-wrong))))

(define-test cond7
  (assert-eq 'right
	     (pcond
	      (nil 'nil-wrong)
	      ((and t (:re "ab(\\w+)" "ababc" (d)) (:re "ab(c)" d (c)))
	       (when (and (equal "c" c))
		 'right))
	      (t 't-wrong))))

(define-test cond8
  (assert-eq 'right
	     (pcond
	      (nil 'nil-wrong)
	      ((and t (:re "(\\d+)" "123" ((#'parse-integer d))))
	       (when (= 123 d)
		 'right))
	      (t 't-wrong))))

(define-test cond9
  (assert-eq 'right
	     (pcond
	      (nil 'nil-wrong)
	      ((:pat ?x 1)
	       (when (= ?x 1)
		 'right))
	      (t 't-wrong))))

(define-test cond10
  (assert-eq 'right
	     (pcond
	      (nil 'nil-wrong)
	      ((:pat (?x) '(1))
	       (when (= ?x 1)
		 'right))
	      (t 't-wrong))))

(define-test cond11
  (assert-eq 'right
	     (pcond
	      (nil 'nil-wrong)
	      ((:pat (?x ?y) '(1 2))
	       (when (and (= ?x 1) (= ?y 2))
		 'right))
	      (t 't-wrong))))

(define-test cond12
  (assert-eq 'right
	     (pcond
	      (nil 'nil-wrong)
	      ((:pat (?x . ?xs) '( 1))
	       (when (= ?x 1)
		 'right))
	      (t 't-wrong))))

(define-test cond13
  (assert-eq 'right
	     (pcond
	      (nil 'nil-wrong)
	      ((and (:pat (?x . ?xs) '( 1)) (eq ?xs 'p))
	       'wrong)
	      ((and (:pat (?first ?last)
			  '("Eric" "Normand"))
		    (:re "^Nor" ?last))
	       'right)
	      
	      (t 't-wrong))))

(define-test cond14
  (assert-eq 'right
	     (pcond
	      (nil 'nil-wrong)
	      ((and (some #'(lambda (x)
			  (and (:pat ?x x)
			       (evenp ?x)))
		      '(1 2 3))
		    (= ?x 2))
	       'right))))

(define-test dont-set-variables-for-other-tests
  (pcond
   ((and (:re "(a)" "a" (a)) (print a)
	 (equal a "b"))
    (assert-false 'should-not-get-here)
    )
   (t
    (assert-error 'unbound-variable a))))

(define-test you-can-put-vars-in-matcher
  (pcond
   ((:pat (a ?b) '(?a b))
    (assert-equal 'a ?a)
    (assert-equal 'b ?b))
   (t (assert-false 'shouldnt-get-here))))

(defun test-pcond ()
  (run-all-tests :pcond-test))