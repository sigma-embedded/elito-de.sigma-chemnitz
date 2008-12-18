PV		  = "0.3.2+git"
PR		  = "r0"

DEFAULT_PREFERENCE = -1

GIT_REPO  ?= "/home/ensc/src/mobm"
GIT_BRANCH = "devel"

include mobm320.inc
inherit gitdevel
