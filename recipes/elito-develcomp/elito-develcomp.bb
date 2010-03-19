DESCRIPTION = "Generates makefile in workspace directory"
PV = "0.3"
PR = "r2"
LICENSE = "GPLv3"

PACKAGES = ""
PACKAGE_ARCH = "all"

INHIBIT_DEFAULT_DEPS = "1"

DEVELCOMP_MAKEFILE  ?= "${ELITO_GIT_WS}/Makefile.${PROJECT_NAME}"

_export_vars = " \
	AR		\
	BUILD_AR	\
	BUILD_CC	\
	BUILD_CCLD	\
	BUILD_CFLAGS	\
	BUILD_CPP	\
	BUILD_CPPFLAGS	\
	BUILD_CXX	\
	BUILD_CXXFLAGS	\
	BUILD_F77	\
	BUILD_LD	\
	BUILD_LDFLAGS	\
	BUILD_RANLIB	\
	BUILD_STRIP	\
	CC		\
	CCACHE_DIR	\
	CCLD		\
	CFLAGS		\
	CPP		\
	CPPFLAGS	\
	CXX		\
	CXXFLAGS	\
	F77		\
	LD		\
	LDFLAGS		\
	NM		\
	OBJCOPY		\
	OBJDUMP		\
	PATH		\
	PKG_CONFIG_DIR	\
	PKG_CONFIG_DISABLE_UNINSTALLED \
	PKG_CONFIG_PATH	\
	PKG_CONFIG_SYSROOT_DIR	\
	QPEDIR		\
	QTDIR		\
	RANLIB		\
	SDK_CFLAGS	\
	SDK_CPPFLAGS	\
	SDK_CXXFLAGS	\
	SDK_LDFLAGS	\
	STRIP		\
	TARGET_CFLAGS	\
	TARGET_CPPFLAGS	\
	TARGET_CXXFLAGS	\
	TARGET_LDFLAGS	\
\
	STAGING_ETCDIR_NATIVE	\
	TARGET_ARCH	\
	BUILD_ARCH	\
	HOST_ARCH	\
	PACKAGE_ARCHS	\
	DEPLOY_DIR_IPK	\
	STAGING_DIR	\
"

python __anonymous () {
    vars = bb.data.getVar('_export_vars', d, 1).split()
    res  = map(lambda v: 'export %s = %s' % (v, bb.data.getVar(v, d, 1)),
               filter(lambda k: bb.data.getVar(k, d, 0) != None, vars))

    bb.data.setVar('_export_vars_gen', '\n'.join(res), d)
}

do_configure[depends] = "gcc-cross:do_populate_staging"
do_configure() {
        set >&2
	gc=`${WHICH} ${CROSS_COMPILE}gcc`
	gc=${gc%%gcc}
	dn=`dirname "$gc"`
	gc=`basename "$gc"`

	rm -f "${DEVELCOMP_MAKEFILE}"
        cat << EOF | sed -e 's![[:space:]]*$!!' > "${DEVELCOMP_MAKEFILE}"
## --*- makefile -*--

${_export_vars_gen}

export DESTDIR  = ${IMAGE_ROOTFS}

export _CCACHE	= `${WHICH} ccache 2>/dev/null`
export _CROSS	= ${TARGET_PREFIX}
export _ARCH	= ${TARGET_ARCH}

_secwrap        = ${SECWRAP_CMD}

SH     ?= /bin/bash
PS1     = [\\[\\033[1;34m\\]${PROJECT_NAME}\\[\\033[0;39m\\]|\\u@\\h \\W]\\044$
_start	= env -u MAKELEVEL \$(ENV) \$(_secwrap)

_project_cfg = ${PROJECT_TOPDIR}/mk/\$(CFG).mk
_elito_cfg =   ${ELITO_TOPDIR}/mk/\$(CFG).mk

ifneq (\$(wildcard ${PROJECT_TOPDIR}/mk/common.mk),)
include ${PROJECT_TOPDIR}/mk/common.mk
endif

ifneq (\$(wildcard ${PROJECT_TOPDIR}/mk/\$(CFG).mk),)
include ${PROJECT_TOPDIR}/mk/\$(CFG).mk
else ifneq (\$(wildcard ${ELITO_TOPDIR}/mk/\$(CFG).mk),)
include ${ELITO_TOPDIR}/mk/\$(CFG).mk
else
OPTS ?=
ENV  ?=
endif

ifeq (\$(_SKIP_DEVELCOMP_RULES),)

%:
	\$(_start) \$(MAKE) -e MAKELEVEL:=0 MAKEFILES:= \$(OPTS) \$@

_all_:
	\$(_start) \$(MAKE) -e MAKELEVEL:=0 MAKEFILES:= \$(OPTS)

exec:
	\$(_start) \$(P)

shell:
	@env PS1='\$(PS1) ' \$(_start) \$(SH) -

endif

unexport MAKEFILES
unexport MAKELEVEL
.DEFAULT_GOAL := _all_
EOF

        # make it read-only
	chmod a-w "${DEVELCOMP_MAKEFILE}"
}
