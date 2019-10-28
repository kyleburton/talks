
Title:             The Patterns You Can't {See,Refactor}.  

Abstract: 

There are patterns in your software that sit right in the middle of your blind spot.  There are many powerful tools for abstraction and refactoring our programming languages - Lisp Macros are a tool that allows you to abstract above the limit that is exists in most programming languages.  In the precious 10 minutes we have together I hope to convince you that Lisp Macros represent an unique, powerful tool and that learning more about them will improve you as a programmer.


Bio:

Kyle is the head of technology at Relay Network, working to fix Business to
Consumer communication using Functional Programming and Agile methodologies as
secret weapons to build Relay's systems.  

Kyle has worked in domains ranging from e-commerce to web harvesting to large
scale data integration. He has a very understanding wife who puts up with his
technical vagaries and two adorable daughters (both named after cities). He
cooks a mean soufflé.
  
IDEAS:
  PSC: the threading macros .. and ->
       they are both code-walkers and demonstrate changing the s-expressions

  TAV: 'Writing Code to write your Code.'
  NC:  'A macro is just what you would have written anyway.'

  TAV:
       'You have talked about how in almost every language there is a ceiling'
       'macros are the only thing that lifts the ceiling of abstraction'

    Perhaps document some kind of hierarchy of abstractions?

     OBJECTS, BEHAVIOR + DATA,
        ENCAPSULATION, POLYMORPHISIM:       C++ / Java
     PROCEDURES, COMPOSITE DATA TYPES:      C / Procedural
     SEMANTIC NAMING:                       Assembly
     NONE:                                  Machine Code

This talk is for Philly RedSnake 2013.

NB: It must fit within the 10 minute lightning talk format.

Prospective title:

   The Patterns You Can't {See,Refactor}.

      There are Patterns in your Software that you can't see
      because the syntatax of your language is a barrier.

      Macros are like being able to write a compiler that targets your
      _language_.  You can write this compiler in your language.


Beautiful macros:

 Clojure: .., ->, ->>


h1. Overview

* Before I begin
* What is a pattern
* What are some examples of those patterns: internal and external
   DSLs; not doing anything at all
* What Ruby, Python, JavaScript, Java do about these patterns
* How Lisps approach these patterns

h1. The Mirror

* Reflection of the observer's mind; Blub Paradox

[ When I first saw macros I did not understand them, it took me a while to not
  just see them as an extension of the langauge features I already knew.  That
  is the message I will attempt to convey in the next 10 minutes.  To convince
  you that there is more to macros than the language features you already
  understand. ]

h1. What is a pattern?

* giving something a name gives you power: you can discuss it,
  manipulate it, share it
* pattern of repeated operations: Procedure, Function
* pattern of repeated design:     "Design Pattern"
* pattern of repeated syntax:     Macro

[ As software developers we deal with patterns all the time.  We create
  classes, libraries and APIs to abstract away the commonalities we see in our
  software. In many of the languages we use functions, modules and libraries
  are our upper bound for abstraction. ]

h1. Different

* Delay Execution: if, unless, when
* Reorder: .., ->
* Introduce Bindings: let, with-open

h1. What's a pattern: operations

* for loop: sequence or mapping
* folding: (types of mappings operations) left, right
* data structure traversal: not just sequences, trees, nested maps
* Memoization: adding behavior to / modifying the behavior of already existing code
* Deferring execution

[ Languages that support closures or anonymous functions often to use them to
  support these kinds of patterns.  Languages that do not, like Java, can still
  support them, though the accidental complexity tends to be pretty high compared
  to languages that support them.

  Java also makes use of interfaces and annotations.  But these didn't always
  exist in the language. ]

h1. What's a pattern: repeated design

* GOF: Design Patterns, gave us common language for these patterns
* C2 Wiki: Are Design Patterns Missing Language Features?
* Paul Graham: Are Design Patterns a Language Smell?

* Things that need to be constructed => factory pattern
* Highlander => Singleton
* Extract Behavior => Stragegy


h1. What's a pattern: DSLs

* Internal: within the syntax of the language:
 * XML in Scala
 * Regular Expressions in Ruby, Perl
 * Ruby is particularly good at this: Chef's resource declaration DSL

* External: outside the language, requires a compiler / parser:
 * Ruby: ERB, haml
 * JavaScript: John Resig's JavaScript Tempaltes, Underscore's Templates,
   Mustache JS
 * Java: JSP

h1. Things you can't refactor (without resorting to eval)

* Ruby:       class, module, def, begin, rescue
* Python:     class, def, try, except
* Java:       class, public, private, extends, throws, try, catch
* JavaScript: function

[ Even with all of this, there are still things that you can not factor out of
  your language.  If you're using a HLL like Python or Ruby, these are probably
  regulated things that rqeuire keywords.

  A typical one for languages w/o closures is that you can't create new control
  structures.  If your language doesn't have `unless`, you can't make it.  Eg:
  Java, C, etc.

  In langauges that support closures you can, but you can't avoid typing
  the keywords for a closure: 'def', 'function', or 'do'.

h1. Things you can't refactor

* introduction of new variable bindings
* new control structures that interact with your systems exception handling
  mechanisims.

[ These are perhaps the last bastion for homoiconic languages that support
  macros.  Being able to introduce scope, bindings and interact with exception
  handling are all possible with Lisp's macros.

  For Ruby-ists, imagine being able to create Cucumber as an internal DSL. ]

h1. Lisp's Macros

* Internal: part of the language
* Not the same as 'eval'
* Take place as part of the 'compliation' phase
* Expansions of code
* Can also _rewrite_ the code

[ Macros are expanded just before a compiler operates on the code, replacing
  themselves in situ.  Expansions, like creating an 'unless' that becomes an if
  statement are not all that is possible.  Macros are implemented themselves in
  Lisp, they get to operate on the AST (abstract syntax tree) and can thus
  manipulate the code itself: eliminating branches, re-ordering code, introducing
  bindings or optimizing them away.

  Slava Akhmechet wrote macros that transform imperative lisp code into
  Continuation Passing Style in order to implement delimited continuations in
  Common Lisp as part of the Weblocks framework. ]

h1. Example: unless

  (defmacro unless [predicate & body]
    `(if (not ~predciate)
       @~body))

  (unless (test-something)
    (println "it happened"))

  =>

  (if (not (test-something))
    (println "it happened"))

[ If you try to do this with just a function, you will have to come up with a way to
  deferr the execution of the body - you could wrap it in a closure, though that brings
  in the additional syntax of wrapping the code in a function. ]

h1. Example: Clojure's #() macro

  If you want a function that does: a + (b * (c / a)):

  (fn [a b c]
    (+ a (* b (/ c a)))

  #(+ %1 (* %2 (/ %3 %1)))


h1. Example: Clojure's with-open

  (with-open [input (open-a-file "some-file.txt")]
    (.println input "hey, this is to the file.")
    (raise "Sorry honey, something went BOOM"))

  =>

  (defmacro with-open [bindings & body]
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


--------------------------------------------------------------------------------


Additional notes and material.

Examples from lisp:
  Simpler:

     unless, when
     anaphoric macros: aif, aprog1
     the "with" macros

  Moderate:
     cut, pcut, rcut or Clojure's #() macro.

  Complex:

     pcond
     state machine DSLs
     delimited continuations

The same goes for swtich, cond, case, etc.  These are patterns that _can_ be
simulated with closures or blocks.  But doing so causes accidental compelxity.

Internal vs External DSLs.  SQL is a DSL.  It is an external DSL.  Pro C/C++ is
Oracle's pre-processor for C and C++ code that allows for easily embedded SQL,
effectivley layering a DSL on top of C and C++.  The code was no longer C nor
C++, but the DSL, which had to be run through a pre-processor before it could
be compiled down to C or C++ respectively.

CoffeeScript vs JavaScript
  This is an examle of hitting the barrier and not being able to escape it
  without abandoning your langauge's syntax.

  CoffeeScript can not be implemented _in_ JavaScript without a ton of
  accidental complexity.

  CoffeeScript is an _external_ DSL for JavaScript.  As such it requires either
  a compiler (translates CoffeeScript to JavaScript) or an emulator.

XML's [over] use in Java
  This is an examle of hitting the barrier and not being able to escape it
  without abandoning your langauge's syntax.  XML squirted out to the side
  under the idea that it would _reduce_ compelxtiy.  We can all see how well
  that worked out.

    Configuration
    Dependency Injection
    Aspect Oriented Programming

Annotations in Java
  Before 1.5, Java developers were using special comments in the code to do AOP and DI.
  As a Java developer you could not add an Annotation like feature into your language.


Examles of Lisp Macros.

unless:

  (defmacro unless [predicate & body]
    `(if (not ~predciate)
       @~body))

  (unless (test-something)
    (println "it happened"))

  =>

  (if (not (test-something))
    (println "it happened"))


when:

  ;; if can only have 2 expressions, a consequent and an otherwise, make a form
  ;; that allows multiple in the consequent block

  (defmacro when [predicate & body]
    `(if ~predicate
       (do
         ~@body)))

  (when (test-something)
    (println "it happened")
    (println "I also did another thing"))

  =>

  (if (test-something)
    (do
      (println "it happened")
      (println "I also did another thing")))


Clojure's #() macro:

  If you want a function that does: a + (b * (c / a)):

  (fn [a b c]
    (+ a (* b (/ c a)))

  #(+ %1 (* %2 (/ %3 %1)))

`with`, for simpliified or automatic resource management:

  (with-open [input (open-a-file "some-file.txt")]
    (.println input "hey, this is to the file.")
    (raise "Sorry honey, something went BOOM"))

  ;; NB: you can do this using just closures:

  (defn with-open [make-resource the-fn]
    (try
      (let [[inp (resource)]
        (the-fn inp))
      (catch Exception ex
        (try
          (if (not (nil? input))
            (.close input))
          (throw ex)
          (catch Exception ex2
            (.initCaused ex2 ex)
            (throw ex2)))))

  ;; but then it has to look like this (not the use of `fn`, which also effects
  ;; the errors that your compiler and runtime will report when errors happen):

  (with-open
    (fn [] (open-a-file "some-file.txt"))
    (fn [input]
      (.println input "hey, this is to the file.")
      (raise "Sorry honey, something went BOOM")))

  ;; as a macro:

  (defmacro with-open [bindings & body]
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

  ;; =>

  (try
   (let [input (open-input "input.txt")
               output (open-output "output.txt")]
     (.println output "hey, this happened")
     (raise "Sorry honey, something went BOOM."))
   (catch
       java.lang.Exception
     ex
     (try
      (doseq [closeable [input output]]
        (if (not (nil? closeable)) (.close closeable)))
      (throw ex)
      (catch
          java.lang.Exception
        ex2
        (.initCaused ex2 ex)
        (throw ex2)))))

Note that we not only elimiated the accidental complexity of the
lambda forms, but we also extended it to manage an arbitrary number of
resources thtat might need to be closed.

Caveats to macros like this:

  * the first resource that fails to close will cause the others to
     not be closed.  You can solve this by moving the try-catch, but
    most languages don't support the idea of 'many exceptions
    occurred' only 1 so error recovery is ill-defined.

  * macros are not composeable in the way that higher-order functions
    are.

References:

  http://stackoverflow.com/questions/267862/what-makes-lisp-macros-so-special
  http://hipster.home.xs4all.nl/lib/scheme/gauche/define-syntax-primer.txt
  http://www.gigamonkeys.com/book/
    http://www.gigamonkeys.com/book/macros-defining-your-own.html



--------------------------------------------------------------------------------

10 Minutes

10 Slides

Title.   The Patterns You Can't {See,Refactor}.
1. Reflection of the observer's mind; Blub Paradox
2. Macros Are: A way to write code that produces Lisp.
3. Patterns You Can't Refactor
4. Defer Execution; Introduce Bindings
5. Anatomy of a Macro
6. Simple: Clojure: .. and ->
7. Complex: Common Lisp: pcond
8. Complex: Scheme: FSM
9. Danger: Macros Change Semantics
10. Thank you
References.
* Reflection of the observer's mind; Blub Paradox

1. Reflection of the observer's mind; Blub Paradox

[ When I first saw macros I did not understand them, it took me a while to not
  just see them as an extension of the langauge features I already knew.  That
  is the message I will attempt to convey in the next 10 minutes.  To convince
  you that there is more to macros than the language features you already
  know. ]

2. Macros Are: A way to write code that produces Lisp.

[ Lisp's macros allow you to write code that writes code.  Macros do
  this without resorting to concatenating strings and calling eval.
  Lisp's homoiconicity facilitates this wihout undue complexity. ]

3. Patterns You Can't Refactor

4. Defer Execution; Introduce Bindings

[ Macros can defer execution of code, giving you the same syntatic
  power as 'if', 'when' and the like.  They allow you to introduce
  new flow control constructs that interact with your language's
  exception handling system. ]

5. Anatomy of a Macro

   (defmacro unless [predicate & body]
     `(if (not ~predicate)
        (do
          ~@body)))

   (unless ()
     (launch-missles))

6. Simple: Clojure: .. and ->

7. Complex: Common Lisp: pcond

8. Complex: Scheme: FSM

9. Danger: Macros Change Semantics

10. Thank you

