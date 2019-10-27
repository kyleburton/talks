
Title:             The Patterns You Can't {See,Refactor}.

Abstract:

There are patterns in our software that sit right in the middle of our blind spot.  We have many powerful tools for abstraction and refactoring in our programming languages - though I believe Lisp Macros are a tool that allows us to abstract above limits that exist in most programming languages.  In the time we have together I hope to convince you that Lisp Macros represent an unique, powerful tool and that learning more about them will improve us as programmers.


Bio:

I'm Kyle, I've been involved with software engineering for about twenty five years at this point (2019).  I've worked in domains and industries ranging from travel to finance, to data integration and now video games (though it's really distributed services, I won't tell anyone if you don't).  I fancy myself an amateur chef.


IDEAS:
  TAV: 'You have talked about how in almost every language there is a ceiling'
       'macros are the only thing that lifts the ceiling of abstraction'

    Perhaps document some kind of hierarchy of abstractions?

     OBJECTS, BEHAVIOR + DATA,
        ENCAPSULATION, POLYMORPHISIM:       C++ / Java
     PROCEDURES, COMPOSITE DATA TYPES:      C / Procedural
     SEMANTIC NAMING:                       Assembly
     NONE:                                  Machine Code


      There are Patterns in your Software that you can't see
      because the syntatax of your language is a barrier.

      Macros are like being able to write a compiler that targets your
      _language_.  You can write this compiler in your language.


Beautiful macros:

 Clojure: .., ->, ->>, aif, with-open


h1. Overview

* Before I begin
* What is a pattern?
* Examples of those patterns?: internal and external DSLs;
  not doing anything at all
* What Ruby, Python, JavaScript, Java do about these patterns
* How Lisps approach these patterns

h1. The Mirror

* Reflection of the observer's mind; aka The Blub Paradox

[ When I first saw macros I did not understand them, it took me an
  embarassing long time to not just see them as an extension of the
  langauge features I already knew.  That is a key part of the message
  I am attempting to convey in the next 10 minutes.  To convince you
  that there is more to macros than the language features you already
  understand. ]

h1. What is a pattern?

* naming something gives us power over it: we can discuss it,
  manipulate it, share it
* pattern of operations: Procedure, Function
* pattern of design:     "Design Pattern"
* pattern of syntax:     Macro

[ As software developers we deal with patterns all the time.  We
  create classes, libraries and APIs to abstract away the common
  operations and designs we see in our software. In many of the
  languages we use functions, modules and libraries are our upper
  bound for abstraction. ]

h1. Accidental and Inherent Complexity

[ Inherent complexity cannot be eliminated.  It is inherent in the
  domain and must be part of any solution we can express.  Inherent
  complexity is unaviodable.  In a sense it is our lower bound for how
  we express a solution, how we express an implementation.  In
  mathematics an integer isn't 32 or 64 bits.  Integers are an
  infinite set.  That they are bounded and can over flow (or
  underflow) is accidental complexity of performing arithmatic in our
  software.  It's a practical and useful limitation, though it it's a
  leaky abstraction.  When our calculations cross these boundaries we
  must abort or shift to a significantly less efficient implementation
  (bignum). ]

[ Macros are a powerful tool for reigning in accidental complexity,
  allowing us to approach, more and more closely, minimally complex
  implementations. ]


h1. Super Powers: What makes Macros Different from other tools for Abstraction?

* Delay Execution:           if, unless, when
* Reorder Computation:       .., ->
* Introduce Bindings:        let, with-open
* Compute at Compile time

[ What makes macros different from interfaces, classes, procedures,
  functions and closures?  Macros can precompute or delay execution,
  they can generate code, change semantics by re-ordering computation,
  introduce bindings and generate code. ]

h1. What's a pattern: Operations

* for loop: sequence or mapping
* folding: (types of mappings operations) left, right
* data structure traversal: not just sequences, trees, nested maps
* Memoization: adding behavior to / modifying the behavior of already existing code
* Deferring execution

[ Languages that support first class closures or anonymous functions
  often use them to support these kinds of patterns.  Languages that
  do not, like Java, can still support them, though the accidental
  complexity tends to be high compared to languages that support them.

  Java also makes use of interfaces and annotations.  But these didn't
  always exist in the language itself.  Before the language gods
  decided to allow you to use anonymous functions your only recourse
  was anonymous abstract classes or one off implementations of
  interfaces.

  Macros allow you to implement any operation you find useful, whether
  or not the language designers had the forethought to recognize those
  operations. ]

h1. What's a pattern: repeated design

* GOF: Design Patterns, gave us common language for these patterns
* C2 Wiki: Are Design Patterns Missing Language Features?
* Paul Graham: Are Design Patterns a Language Smell?

* Things that need to be constructed => factory pattern
* Highlander                         => Singleton (there can be only one)
* Extract Behavior                   => Stragegy

h1. What's a pattern: DSLs

* Internal: within the syntax of the language:
 * XML in Scala
 * Regular Expressions in Ruby, Perl
 * Ruby is particularly good at this: Chef's resource declaration DSL

* External: outside the language, requires a compiler / parser:
 * Ruby: ERB, haml
 * JavaScript: John Resig's JavaScript Tempaltes, Underscore's Templates,
   Mustache JS
 * Java: JSPs
 * Python's Jinja templating system

h1. Things you can't refactor (without resorting to eval)

* Ruby:       class, module, def, begin, rescue
* Python:     class, def, try, except, if, for
* Java:       class, public, private, extends, throws, try, catch, package
* JavaScript: function, class

[ Even with all of this, there are still things that you can not factor out of
  your language.  If you're using a HLL like Python or Ruby, these are probably
  regulated things that rqeuire keywords.

  A typical one for languages w/o closures is that you can't create new control
  structures.  If your language doesn't have `unless`, you can't make it.  Eg:
  Java, C, etc.

  In langauges that support closures you can, but you can't avoid typing
  the keywords for a closure: 'def', 'function', or 'do'.

h1. [more] Things you can't refactor

* Introduction of new variable bindings
* New control structures, espeically ones that interact with
  your languages exception handling mechanisims.

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
  defer the execution of the body - you could wrap it in a closure, though that causes accidental
  complexity to slip back in with the additional syntax of wrapping the code in a function.

  It's become common for modern langauges to introduce _more_ syntax
  to allow simpler use of closures or anonymous functions, eg in
  JavaScript ()=>{} and Java's Lambda expressions.

  This cleanly solves for many of the use-cases where you'd like to
  defer execution.

  ]

h1. Example: Clojure's reader #() macro

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


================================================================================

```
2019-10-02T09:49 PDT Wednesday

considering giving this again, extending it, these are additional
thoughts / ideas as I read through the notes again.

   "The Mirror"

     I personally experienced this when I first started studying lisp,
     though I think I've observed this repeatedly when engineers
     approach languages that are new to them.  I repeatedly failed to
     see what was different or speical about what I was being exposed
     to.  Thinking back I felt this as well when I was first exposed
     to OO.

     I think this is an expected part of human nature, of how we seek
     to understand what we're experiencing: we use our previous
     experience and our "model of the world" to try to understand the
     new things.  We don't have the new vocabulary yet, so we use what
     we've got.

        [ the aprophical exmaple of people describing an elephant? ]

     bootstrapping that understanding is a huge challenge ... but one
     thing that software enginees are sometimes great at is helping
     each other understand new ideas ...

     On one hand, this is valuable as it allows us to contextualize
     new experiences, new concepts -- see if they match a pattern we
     already understand.  On the other hand, this gets in the way of
     us understanding those new patterns.  If we think what we're
     looking at matches something we already know, we no stop
     attempting to understand what's different or new about it.

     This is a huge challenge for my own learning, as it's not
     something I am easily conscious of when it's happening.

     Related phrases: "the beginner's mind", "an open mind"
       "truley remarkable, the mind of a child is" - yoda :)
          this is talking about the blind spots that our
          patterns and assumptions cause us, which are in
          the way of us understanding what we're seeing
          for what it is vs what our preconceived notions
          make us think it is

   So, what do I mean by "The Mirror"?  Well if you're an experienced
   software engineer, you know a language and have built and delivered
   software in that language, you've likley got patterns and idioms
   that you can apply to solving problems and you're likley clever
   enough to be able to do that for any problem you are presented
   with.    For me, when I approach new langauges, b/c of my
   preconceived notions, they often reflect back to me the
   idioms and approaches I already know, it takes me intentional
   effort to reject seeing the OO patterns I'm accustomed to
   when I look at Rust.

   "What's a pattern: operations"
      ... make a thing, prepare it and hand it back
          this happens at Carl's Jr, McDonalds
          in OO languages a name was chosen for this pattern: constructor
          but its tied to the semantics of how you declare your classes
            and since it became necessary to use this pattern
            elsewhere though not coupled to the construction of a
            different class, a new name had to be chosen: factory
            it's the same 'shape' of actions: make a thing, prepare it
            and hand it back.

            in Common Lisp this is PROG2, Clojure uses 'doto'
            although it's pretty straightforward to just use a 'let'
            form for doing this as well.

            instead of being coupled to the definition or initialization of a
            class, it's instead named for the actions or order of operations
            being taken.  IMO this ends up being more composable.


```
