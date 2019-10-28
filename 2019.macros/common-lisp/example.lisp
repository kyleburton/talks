;; C-u M-x slime
;; Type 'sbcl' and press return

;; http://www.quicklisp.org/
;;   curl -O http://beta.quicklisp.org/quicklisp.lisp
;;   M-x slime-load-file quicklisp.lisp
;;   (quicklisp-quickstart:install)
(ql:quickload "cl-unification")
(ql:quickload "cl-ppcre")
;; now unarchive pcond  into ~/quicklisp/local-projects
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




(LET ((#:G1273
       (LET (NUM ?X ?Y ?REST)
         (WHEN
             (AND
              (MULTIPLE-VALUE-BIND (PCOND::MATCH VALUES)
                  (CL-PPCRE:SCAN-TO-STRINGS "ab(\\d*)" "ab12")
                (WHEN PCOND::MATCH
                  (SETF NUM (FUNCALL #'PARSE-INTEGER (ELT VALUES 0)))
                  PCOND::MATCH))
              (SOME
               #'(LAMBDA (X)
                   (AND
                    (IGNORE-ERRORS
                      (LET ((PCOND::MATCH
                                (IT.UNIMIB.DISCO.MA.CL.EXT.DACF.UNIFICATION:UNIFY
                                 '(?X ?Y . ?REST) X)))
                        (SETF ?X
                              (IT.UNIMIB.DISCO.MA.CL.EXT.DACF.UNIFICATION:FIND-VARIABLE-VALUE
                               '?X PCOND::MATCH))
                        (SETF ?Y
                              (IT.UNIMIB.DISCO.MA.CL.EXT.DACF.UNIFICATION:FIND-VARIABLE-VALUE
                               '?Y PCOND::MATCH))
                        (SETF ?REST
                              (IT.UNIMIB.DISCO.MA.CL.EXT.DACF.UNIFICATION:FIND-VARIABLE-VALUE
                               '?REST PCOND::MATCH))
                        PCOND::MATCH))
                    (= ?Y NUM)))
               '((A 1) (B 2 H) (C 12 J) (F 12))))
           (LAMBDA () ?X)))))
  (IF #:G1273
      (FUNCALL #:G1273)
      NIL))









(pcond:pcond
   ((and (:pat ((?first ?last) . ?rest)
               '(("George" "Washington")
         ("John" "Adams")))
         (:re "(.+)o" ?first (before-o)))
    (format t "before-o: ~a~&" before-o)
    (ptest ?rest)))


(LET ((#:G1274
       (LET (?FIRST ?LAST ?REST BEFORE-O)
         (WHEN
             (AND
              (IGNORE-ERRORS
                (LET ((PCOND::MATCH
                          (IT.UNIMIB.DISCO.MA.CL.EXT.DACF.UNIFICATION:UNIFY
                           '((?FIRST ?LAST) . ?REST)
                           '(("George" "Washington") ("John" "Adams")))))
                  (SETF ?FIRST
                        (IT.UNIMIB.DISCO.MA.CL.EXT.DACF.UNIFICATION:FIND-VARIABLE-VALUE
                         '?FIRST PCOND::MATCH))
                  (SETF ?LAST
                        (IT.UNIMIB.DISCO.MA.CL.EXT.DACF.UNIFICATION:FIND-VARIABLE-VALUE
                         '?LAST PCOND::MATCH))
                  (SETF ?REST
                        (IT.UNIMIB.DISCO.MA.CL.EXT.DACF.UNIFICATION:FIND-VARIABLE-VALUE
                         '?REST PCOND::MATCH))
                  PCOND::MATCH))
              (MULTIPLE-VALUE-BIND (PCOND::MATCH VALUES)
                  (CL-PPCRE:SCAN-TO-STRINGS "(.+)o" ?FIRST)
                (WHEN PCOND::MATCH
                  (SETF BEFORE-O (FUNCALL #'IDENTITY (ELT VALUES 0)))
                  PCOND::MATCH)))
           (LAMBDA () (FORMAT T "before-o: ~a~&" BEFORE-O) (PTEST ?REST))))))
  (IF #:G1274
      (FUNCALL #:G1274)
      NIL))