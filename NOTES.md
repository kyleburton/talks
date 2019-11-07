2019-11-07T12:40 PST Thursday
  I keep going back to Emacs+CIDER+Clojure any time I need to prototype or
  expirement with anything.   There's a thought rolling around in my head
  related to this style of development and this tooling ... I think that
  setup represents the most powerful set of tooling I've experienced that
  allows me to best explore a problem domain, a solution domain and then
  an implementation domain.  The transition between these is so smooth
  for me that it's essentially continuous.
  what's a problem domain?
  - when attacking a new domain, my first steps are to invest in understanding it
  what's a solution domain?
  - now that we think we've got a handle on what we're attacking, how
    do we effectivley attack it?  what's our strategy for representing
    the inherent aspects of the problem domain, how do we model the
    key information that describes the problem, what will the operations
    be, what does our state machine and transitions look like?
  what's an implementation domain?
  - now that we think we know what we're attacking, how to represent
    it lucidly, what do we need to do to adapt it to our tech stack?
    what compromises are we making in concrete representation (apis,
    native data types, layers in the architecture)
  What makes this tooling particulalry good here?
  What makes other tooling not particulalry good here?

2019-10-29T18:38 PDT Tuesday
  Desmond & I were chatting about Scheme and Continuations ... I was sharing
  that I wasn't completely comfortable with my understanding and was looking
  for others that might understand them so I could talk through it w/them.
  Desmond immediately suggested I do a talk on Continuations ... as that's
  had a few hours to sink in, it may actually be possible, with some
  additional research/study I could likely talk through my own journey
  as well as what I've learned & seen wrt CPS and Continuations ...
     - that Sussmen & Steel embarked on Scheme to study CPS and Continuations
     - what CPS is and how it's related to tail recursion and tail call optimization
       https://en.wikipedia.org/wiki/Continuation-passing_style
     - that Chicken Scheme implements them (are there other scheme's)
     - "Reinverting the Inversion of control" - nb: I cannot find this in google any longer :(
     - "putting parenthesis around the spec"
       - Peter Siebel talking about this here: https://books.google.com/books?id=nneBa6-mWfgC&pg=PA443&lpg=PA443&dq=putting+parentheses+around+the+spec+common+lisp&source=bl&ots=gGyrEhRY1z&sig=ACfU3U0q0Q-5slS6QxDE_CzA8YCKH666Xw&hl=en&sa=X&ved=2ahUKEwjS19am7sLlAhXRvp4KHRpVDQAQ6AEwBHoECAkQAQ#v=onepage&q=putting%20parentheses%20around%20the%20spec%20common%20lisp&f=false
     - the 'callback' / promise pattern in JS can be seen as a manual transformation
       into CPS, knowing this can help us think about how to structure our callback
       code effectively

