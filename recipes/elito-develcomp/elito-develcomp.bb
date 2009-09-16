DESCRIPTION = "Generates makefile in workspace directory"
PV = "0.1"
PR = "r0"
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
"

python __anonymous () {
    vars = bb.data.getVar('_export_vars', d, 1).split()
    res  = map(lambda v: 'export %s = %s' % (v, bb.data.getVar(v, d, 1)),
               filter(lambda k: bb.data.getVar(k, d, 0) != None, vars))

    bb.data.setVar('_export_vars_gen', '\n'.join(res), d)
}

do_configure() {
        set >&2
	gc=`${WHICH} ${CROSS_COMPILE}gcc`
	gc=${gc%%gcc}
	dn=`dirname "$gc"`
	gc=`basename "$gc"`
	ccache=`${WHICH} ccache`

	rm -f "${DEVELCOMP_MAKEFILE}"
        cat << EOF | sed -e 's![[:space:]]*$!!' > "${DEVELCOMP_MAKEFILE}"
## --*- makefile -*--

${_export_vars_gen}

SH     ?= /bin/bash
_start	= env -u MAKELEVEL

%:
	\$(MAKE) -e MAKELEVEL=0 \$@

exec:
	\$(_start) \$(P)

shell:
	\$(_start) \$(SH) -

unexport MAKEFILES
unexport MAKELEVEL
.DEFAULT_GOAL := all
EOF
}
