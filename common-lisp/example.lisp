;; C-u M-x slime
;; Type 'sbcl' and press return

;; http://www.quicklisp.org/
;;   curl -O http://beta.quicklisp.org/quicklisp.lisp
;;   M-x slime-load-file quicklisp.lisp
(ql:quickload "cl-unification")
(ql:quickload "cl-ppcre")
(ql:quickload "pcond")


(defun ptest (names)
  (pcond:pcond
   ((and (:pat ((?first ?last) . ?rest)
               names)
         (:re "(.+)o" ?first (before-o)))
    (format t "before-o: ~a~&" before-o)
    (ptest ?rest))))

(ptest '(("George" "Washington")
         ("John" "Adams")))

(pcond:pcond
 ((and (:re "ab(\\d*)" "ab12" ((#'parse-integer num)))
       (some #'(lambda (x)
                 (and (:pat (?x ?y . ?rest) x)
                      (= ?y num)))
             '((a 1) (b 2 h) (c 12 j) (f 12))))
  ?x))