(defsystem :pcond
  :name "PCOND"
  :author "Eric Normand <ericwnormand@gmail.com>"
  :version "0.3"
  :maintainer "Eric Normand <ericwnormand@gmail.com>"
  :licence "Lesser Lisp General Public License (LLGPL)"
  :description "A pattern matching conditional macro."

  :components
  ((:file "package")
   (:file "backend" :depends-on ("package"))
   (:file "pcond" :depends-on ("package" "backend")))
  :depends-on (:cl-ppcre :cl-unification))

(defsystem :pcond-test
  :components

  ((:file "test-package")
   (:file "pcond-test" :depends-on ("test-package")))
  :depends-on (:pcond :lisp-unit))