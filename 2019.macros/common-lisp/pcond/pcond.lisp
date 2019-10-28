(cl:in-package :pcond)

;; TODO
;;   try out the auto-documentation functions at weitz.de

(defmacro pcond (&rest exprs)
  "PCOND is a conditional macro that does pattern matching
as well as traditional conditionals.  It acts like a regular
COND statement, but it can bind variables
inside of each conditional statement.  I think an example will
explain it best:
(pcond
 ((evenp 11) ;; normal conditional
  'should-not-return-this)
 ((:re \"ab(c)\" \"abc\" (letter-c)) ;; regular expression
       
  letter-c) ;; variable was bound in conditional (matching groups)
 ((:pat (?x . ?rest) (list 1 2 3)) ;; unifying conditional
  (nconc ?rest (list ?x)))) ;; variables bound in conditional

And a more practical example:
(defun my-map (fun list)
  (pcond
   ((:pat nil list) ;; equivalent to (null list)
    nil)
   ((:pat (?first . ?rest) list)
    (cons (funcall fun ?first)
	  (my-map fun ?rest)))))"
  (recur exprs))

(defun add-conditional (dispatcher vars expand)
  "Add conditional allows PCOND to be expanded with new matching logic.
It takes three arguments:
DISPATCHER is a function of one argument that returns non-nil when
a test (its argument) can be processed by this conditional.
VARS is a function of one argument that returns the variables that could
be bound as a result of success of the conditional
EXPAND is a function that returns a form to be evaluated which both returns
       non-nil when the conditional matches and binds the values of the variables"
  (push (make-instance 'conditional :dispatcher dispatcher
		       :vars vars :expander expand)
	*conditionals*))

(defun test (expr)
  "The test of the expression"
  (first expr))

(defun body (expr)
  "The body of the expression"
  (rest expr))

(defun recur (exprs)
  "Build the conditional tree recursively."
  (when exprs
    (let* ((first (first exprs))
	   (rest (rest exprs))
	   (vars (remove-duplicates (vars-in (test first))))
	   (ret (gensym)))
      `(let ((,ret
	      (let ,vars
		(when ,(expand (test first))
		  (lambda ()
		    ,@(body first))))))
	 (if ,ret
	     (funcall ,ret)
	     ,(recur rest))))))

(defun find-conditional (test)
  "Use this to find the conditional that will match a test."
  (find-if 
   #'(lambda (c)
       (funcall (dispatcher c)
		test))
   *conditionals*))

(defun expand (test)
  "Use this to macro-expand a test into code."
  (funcall (expander (find-conditional test))
	   test))


(defun vars-in (test)
  "Call this to get a list (with duplicates) of all of the variables
potentially bound in the conditional"
  (funcall (vars (find-conditional test))
	   test))

(defun varsym-p (v)
  "Is the symbol V a variable for a Unification conditional?
Default is if it starts with #\?"
  (and (symbolp v) (eq (char (symbol-name v) 0) #\?)))

(defun pat-vars (exp)
  "Use this as the VARS function for Unification conditional
In a Unification pattern, any value V in the tree of either expression
where (VARSYM-P V) => non-nil is a variable."
  (cond
    ((null exp)
     nil)
    ((varsym-p exp)
     (list exp))
    ((consp exp)
     (nconc (pat-vars (first exp))
	    (pat-vars (rest exp))))
    ((vectorp exp)
     (loop for v across exp
	   nconc (pat-vars v)))))

(defun re-vars (frm)
  "Use this as the VARS function for Regular Expression Conditionals."
  (cond
    ((null frm)
     nil)
    ((listp (first frm))
     (append (rest (first frm))
	     (re-vars (rest frm))))
    (t
     (cons (first frm)
	   (re-vars (rest frm))))))

(defun normalize-re-vars (vars)
  "Use this to normalize the variables for Regular Expression Conditionals."
  (loop for v in vars
	if (consp v)
	nconc (loop for x in (rest v)
		    collect (list (first v) x))
	else
	collect (list '(function identity) v)))



;; At base, the conditional system only supports COND-style constructs
;; The following two expressions add the Regular Expressions and
;; Unification matchers.

;; environment setup

(defun setup-environment ()
  (setf *conditionals* nil)

  ;; COND-like Matcher
  (add-conditional
   (constantly t)  ;; match all tests
   (lambda (expr)  ;; 
     (when (consp expr)
       (mapcan #'vars-in (rest expr))))
					    
   (lambda (test)
     (if (consp test)
	 (cons (first test)
	       (mapcar #'expand (rest test)))
	 test)))
  
  ;; Regular Expression Matcher
  (add-conditional 
   (lambda (expr)
     (and (consp expr)
	  (eq :re (first expr))))
   (lambda (expr)
     (re-vars (fourth expr)))
   (lambda (test)
     `(multiple-value-bind (match values)
       (scan-to-strings ,(second test)
	,(third test))
       (when match
	 ,@(loop for (fun var) in (normalize-re-vars (fourth test))
		 for vi upfrom 0
		 collect `(setf ,var (funcall ,fun (elt values ,vi))))
	 match))))

  ;; Unification matcher
  (add-conditional 
   (lambda (expr)
     (and (consp expr)
	  (eq :pat (first expr))))
   (lambda (expr)
     (nconc (pat-vars (second expr)) (pat-vars (third expr))))
   (lambda (test)
     `(ignore-errors
       (let ((match (unify:unify ',(second test) ,(third test))))
	 ,@ (loop for var in (nconc (pat-vars (second test))
				    (pat-vars (third test)))
		  collect `(setf ,var (unify:find-variable-value ',var match)))
	 match)))))

(setup-environment)