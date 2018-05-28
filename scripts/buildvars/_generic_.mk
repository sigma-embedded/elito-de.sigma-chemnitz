export PATH := ${BUILDVAR_PATH}:${PATH}

ifneq (${BUILDVAR_INHIBIT_PKGCONFIG},)
  export PKG_CONFIG_DIR = ${BUILDVAR_PKG_CONFIG_DIR}
  export PKG_CONFIG_LIBDIR = ${BUILDVAR_PKG_CONFIG_LIBDIR}
  export PKG_CONFIG_PATH = ${BUILDVAR_PKG_CONFIG_PATH}
  export PKG_CONFIG_SYSROOT_DIR = ${BUILDVAR_PKG_CONFIG_SYSROOT_DIR}
endif  # BUILDVAR_INHIBIT_PKGCONFIG

SHELL_TARGET ?= shell
EXEC_TARGET ?= exec
SHELL_SHELL ?= bash

SHELL_PS1 ?= [\[\033[1;34m\]${BUILDVAR_PN}\[\033[0;39m\]|\u@\h \W]\044\040

.DEFAULT_GOAL = all

${SHELL_TARGET}: export PS1=${SHELL_PS1}
${SHELL_TARGET}: FORCE
	${SHELL_SHELL}

${EXEC_TARGET}: FORCE
	$C

.generic-help:	FORCE
	@echo "   shell  ... enters an interactive shell"
	@echo "   exec   ... executes $$C within cross environment"

FORCE:
.PHONY:	FORCE
