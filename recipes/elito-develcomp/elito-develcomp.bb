DESCRIPTION = "Generates makefile in workspace directory"
PV = "0.0.2"
PR = "r0"
LICENSE = "GPLv3"

PACKAGES = ""
PACKAGE_ARCH = "all"

INHIBIT_DEFAULT_DEPS = "1"

DEVELCOMP_MAKEFILE  ?= "${ELITO_GIT_WS}/Makefile.${PROJECT_NAME}"

do_configure() {
	gc=`${WHICH} ${CROSS_COMPILE}gcc`
	gc=${gc%%gcc}
	dn=`dirname "$gc"`
	gc=`basename "$gc"`
	ccache=`${WHICH} ccache`

	rm -f "${DEVELCOMP_MAKEFILE}"
	cat << EOF | sed -e 's![[:space:]]*$!!' > "${DEVELCOMP_MAKEFILE}"
## --*- makefile -*--
export PATH = ${PATH}
export CCACHE_DIR = ${CCACHE_DIR}
export CC = ${CC}
export CPP = ${CPP}
export CXX = ${CXX}
export AS = ${AS}
export LD = ${LD}
export CFLAGS = ${CFLAGS}
export CXXFLAGS = ${CXXFLAGS}
export CPPFLAGS = ${CPPFLAGS}
export LDFLAGS = ${LDFLAGS}

%:
	\$(MAKE) -e MAKELEVEL=0 \$@

exec:
	\$(P)

unexport MAKEFILES
unexport MAKELEVEL
.DEFAULT_GOAL := all
EOF
}
