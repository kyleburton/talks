(cl:in-package :pcond)

(defclass conditional ()
  ((dispatcher :reader dispatcher
	       :initarg :dispatcher)
   (vars :reader vars
	 :initarg :vars)
   (expander :reader expander
	   :initarg :expander)))

(defvar *conditionals* nil)

