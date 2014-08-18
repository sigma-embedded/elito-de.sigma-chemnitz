SHELL = /bin/bash

export DESTDIR  = ${IMAGE_ROOTFS}
export _CROSS	= ${TARGET_PREFIX}
export _ARCH	= ${TARGET_ARCH}

export HISTFILE		= ${_tmpdir}/.lesshst
export LESSHISTFILE	= ${_tmpdir}/.bash_history
export GDBHISTFILE	= ${_tmpdir}/.gdb_history

SH		?= /bin/bash

_secwrap         = ${SECWRAP_CMD}
_nfs_root	?= $(DESTDIR)
_nfs_server	?= $(TFTP_SERVER)

PS1     = [\[\033[1;34m\]${PROJECT_NAME}\[\033[0;39m\]|\u@\h \W]\044$

_bad_env = MAKELEVEL MAKEFILES MAKEFILE_LIST
_start	= env $(addprefix -u ,$(_bad_env)) $(ENV) ELITO_NO_SECWRAP_CMD=1 $(_secwrap)

## export all variables by default
export

define __include_cfg
-include $${PROJECT_TOPDIR}/mk/common.mk

ifneq ($$(wildcard $${PROJECT_TOPDIR}/mk/$1.mk $${ELITO_TOPDIR}/mk/$1.mk),)
  -include $${ELITO_TOPDIR}/mk/$1.mk
  -include $${PROJECT_TOPDIR}/mk/$1.mk
else ifneq ($1,)
  $$(error "no setup for CFG=$1 found")
else
  OPTS ?=
  ENV  ?=
endif
endef					# __include_cfg

$(foreach c,${CFG},$(eval $(call __include_cfg,$c)))

$(MAKEFILE_LIST):
	true

ifeq ($(_SKIP_DEVELCOMP_RULES),)

LOCALGOALS += __call _all_ _redir_ exec shell pshell

$(filter-out $(LOCALGOALS),${MAKECMDGOALS}):	__force
	+$(_start) $(MAKE) -e MAKELEVEL:=0 MAKEFILES:= $(OPTS) $@

__call:
	+$(_start) env -u T $(MAKE) -e MAKELEVEL:=0 MAKEFILES:= $(OPTS) $T

_all_:
	+$(_start) $(MAKE) -e MAKELEVEL:=0 MAKEFILES:= $(OPTS)

_redir_:
	+$(_start) $(MAKE) -e MAKELEVEL:=0 MAKEFILES:= -f '$(_REDIR_MAKEFILE_)' $(_REDIR_OPTS_)

exec:
	$(_start) $(P)

shell:
	@env PS1='$(PS1) ' $(_start) $(SH) -

pshell:
	@${ELITO_TOPDIR}/scripts/run-pseudo "${PROJECT_TOPDIR}" /usr/bin/env ${FAKEROOTENV} PS1='$(PS1) ' $(SH) -

__force:
.PHONY:	__force __call _all_ _redir_ exec shell pshell

endif

unexport LOCALGOALS
unexport MAKEFILES
unexport MAKELEVEL
.DEFAULT_GOAL := _all_
