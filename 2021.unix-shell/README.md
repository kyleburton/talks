# Overview

Folks I've worked with recently (2021) requested I do a bash workshop.  Why have they requested this?  What lead them to do so, what triggered it?

* they saw short iteration loops
 * "one button" to iterate
* observed me composing multiple commands, building up a pipeline by parts to solve problems
* observed me using the shell history in unexpected (for them) ways
* observed me editing the cli in unexpected (for them) ways
* hey, you say zsh (or fish or oil) are better shells?  Awesome!  I'm not familair w/them, I am w/bash so that's what this workshop is about :-D
* everything in this talk is in the git repo, go ahead and clone it!

I think, overall I'm hoping to convey the mindset: "unix is the IDE", repeatability, and composition.  I thik I got early signals of these things from a few resources "unix as literature", "in the beginning was the command line"

So, who's the audience?  I think this is folks who know that there is a shell and know a few basics and are looking to see how other folks use their shells as part of their core workflow.

"cultivate your impatience"

Lets talk about it at a high level first:

* my IDE begins with shells that I compose into workspaces, one per "project"
* GNU Screen, or Tmux if you prefer (I happen to know screen)
* codifying the pattern: named-screen

redirection, capturing intermediate output into a script & then tidying it up before running
provides a record of what you'd done, "generating code"

repetition, while, for, seq, awaiting something

seeing the state in your shell & terminal and some ways to think about this

* pwd
* jobs, foreground and background
* your history & how to help make it more valuable

When to alias vs function vs standalone shell script.

local vs remote

* ssh
* curl / wget
* aws cli
* database cli's (postgres & mysql)

Terminal based editors (oh boy)

* I happen to have some competency w/Vim and w/Emacs so that's what I'll talk about
* interacting w/the shell from these editors
* interacting w/these editros from the shell


Integrating w/your other tools, heading up towards the GUI

* osx: 'open'
* windows: 'start'
* your editors: subl

Customization

* this is a trade off, the more bespoke you are, the harder it is for others to work with you don't become the crazy hermit / recluse with a unique vernacular
* do this to help you remember things
 * lets take a look at my .gitconfig
 * the kinds of things Kyle put into his 'kb' command (which violates the bespokeness)
* reduce the number of lookups you need to do, the number of hops
 * rclone
 * rssh

command line completion

* a bit crufty, though it's simpler than you might at first think

Resources

* Learning the Bash Shell
* Linux in a Nutshell
* The Bash Cookbook
* [The Linux System Administrators Guide](https://tldp.org/LDP/sag/html/index.html)
