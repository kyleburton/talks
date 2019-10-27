Title:             The Patterns You Can't {See,Refactor}.

Abstract:

There are patterns in our software that sit right in the middle of our blind spot.  We have many powerful tools for abstraction and refactoring in our programming languages - though I believe Lisp Macros are a tool that allows us to abstract above limits that exist in most programming languages.  In the time we have together I hope to convince you that Lisp Macros represent an unique, powerful tool and that learning more about them will improve us as programmers.


Bio: `whois kyle.burton`

I'm Kyle Burton, I've been doing software development professionally for about twenty five years at this point (1994-2019). I've worked in doamins ranging from the travel industry, finanace, to data integration and now video games, though it's really distributed services.  (I won't tell anyone if you don't).  I also fancy myself an amateur chef.

I was drawn to the siren call of lisp somewhere around the year 2001 after reading Paul Graham's "Beating the Averages".  In that essay he talked about how he felt Lisp gave his company a competetive advantage.  He made some pretty bold claims in that essay.  It inspired me to start down my own Road To Lisp.

I started studying Common Lisp about that time and then was fortuante enough to be able to get away with using JScheme at my day job, then Clojure somewhere around late 2007.  I was able to work primarily in Clojure for almost a decade, and still use it whenever I can.


Overall flow for the talk?

* What's a pattern? and what do I mean by blind spot?
  * Whorf-Sapir hypothesis and programming lanaguages
  * What's in our blind spot? What can't we refactor?
  * Phil Karlton: "There are only [three] hard things in Computer Science: cache invalidation, naming things and off by one errors"
  * Blub Paradox

* Common Tools for Abstraction?
  * functions  - repeated operations
  * Structures - relate information/data
  * Objects    - relate data and the operations on it
                 allowing for extension (or re-use, at
                 least in theory, perhaps less so in practice)
  * Packages and Modules
  * Libraries
  * Services and APIs (no operations, data interchange only)

* What are Macros? / How are they different?
  * defer execution
  * reorder operations
  * introduce bindings
  * compute at compile time

  * these powers allow us to factor out reduce accidental complexity

  * drawback: macros don't compose.  They don't exist at runtime. They
    already modified your code at compile time.

  * reader macros vs syntax macros

* Examples of Lisp Macros
  * prog1 aka Clojure's doto
  * unless
  * anaphoric macros
* Hygene in macros that introduce bindings
* An Aside about Scheme's syntax Macros
  * JRM's Primer
  * define-syntax and an fsm
* Examples of Clojure Macros
  * or, and, when, when-not, delay, if-let
  * time
  * with-open
  * ..
  * ->, ->> and as->
  * with-out-str

* How to write a macro?
  * coding by wishful thinking
  * start with working code
  * backquote it
  * refactor away the accidental complexity
    by generating the repetetive parts


# What's a pattern?

  definition.  I like definitions ... I a big fan of meaning
     both from a "do we really think this means the same thing?" perspective
     and from a "why are we doing this" perspective

   def: pattern (n)
     1. a repeated decorative design
     2. a model or design used as a guide in needlework and other crafts.
     3. an example for others to follow.

   ... hrm ... decorative design, well maybe in how the team writes its
       comments and documentation, though hopefully we're not shaping
       our code quite in this way
     ((maybe show the 99 bottls of beer in lisp that looks like beer bottles))
   ... coding isn't needlework per se, though I do like thinking of it as a craft,
       as a skill we practice and invest in to maintain and increase our capabilities
   ... oh an example for others to follow, this is a good one, I think
       this is often where we start - when we arrive at an exiting codebase
       we often try to follow it's conventions so that our changes fit into
       the strucutre that's already there.
       consistency and why it's important to me
   ... even though written code is a static thing, I think the verb's definition
       is a better fit, as code does stuff, it's an action right?

   def: pattern (v)
     1. decorate with a recurring design.
     2. give a regular or intelligible form to.
        give something a form based on that of (something else).

    ... there we go, a "form based on something else"


  show exmaples from the real world
  talk about a JIG (not the dance)
  design patterns
  copy and pasting <- important one

  repetition

  accidental vs inherent complexity
    part of the problem domain or part of the solution domain?
    this is where DSLs come in and often where we get really enthusiastic
      about them, they often allow us to remove accidental complexity

      accidental complexity reduces useability for folks useing our solutions
         see also real-world forms that need to be filled out


concepts / threads / ideas to follow up on:



References:

* "Beating The Averages": http://www.paulgraham.com/avg.html
* "Road to Lisp": https://www.reddit.com/r/lisp/comments/7pye1g/where_did_the_road_to_lisp_survey_go/
