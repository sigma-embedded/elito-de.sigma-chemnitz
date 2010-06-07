DESCRIPTION = "Generates makefile in workspace directory"
HOMEPAGE = "http://elito.sigma-chemnitz.de"
PV = "0.3+${PROJECT_CONF_DATE}"
PR = "r2"
LICENSE = "GPLv3"

PACKAGES = ""
PACKAGE_ARCH = "all"

INHIBIT_DEFAULT_DEPS = "1"

DEVELCOMP_MAKEFILE  ?= "${ELITO_GIT_WS}/Makefile.${PROJECT_NAME}"

_export_vars = " \
	+AR		\
	+AS		\
	+BUILD_AR	\
	+BUILD_CC	\
	+BUILD_CCLD	\
	+BUILD_CFLAGS	\
	+BUILD_CPP	\
	+BUILD_CPPFLAGS	\
	+BUILD_CXX	\
	+BUILD_CXXFLAGS	\
	+BUILD_F77	\
	+BUILD_LD	\
	+BUILD_LDFLAGS	\
	+BUILD_RANLIB	\
	+BUILD_STRIP	\
	BUILD_SYS	\
	+CC		\
	+CCACHE_DIR	\
	+CCLD		\
	+CFLAGS		\
	+CPP		\
	+CPPFLAGS	\
	+CXX		\
	+CXXFLAGS	\
	+F77		\
	+LD		\
	+LDFLAGS	\
	+NM		\
	+OBJCOPY	\
	+OBJDUMP	\
	+PATH		\
	+PKG_CONFIG_DIR	\
	+PKG_CONFIG_DISABLE_UNINSTALLED \
	+PKG_CONFIG_PATH	\
	+PKG_CONFIG_SYSROOT_DIR	\
	QPEDIR		\
	+QTDIR		\
	+RANLIB		\
	SDK_CFLAGS	\
	SDK_CPPFLAGS	\
	SDK_CXXFLAGS	\
	SDK_LDFLAGS	\
	+STRIP		\
	+TARGET_CFLAGS	\
	+TARGET_CPPFLAGS	\
	+TARGET_CXXFLAGS	\
	+TARGET_LDFLAGS	\
	TARGET_SYS	\
\
	STAGING_ETCDIR_NATIVE	\
	STAGING_DIR_HOST \
	STAGING_DIR_NATIVE \
	STAGING_DIR_TARGET \
	TARGET_ARCH	\
	BUILD_ARCH	\
	HOST_ARCH	\
	HOST_SYS	\
	PACKAGE_ARCHS	\
	DEPLOY_DIR_IPK	\
	STAGING_DIR	\
"

python __anonymous () {
    vars = bb.data.getVar('_export_vars', d, 1).split()
    res  = map(lambda v:
               (lambda k,v: '%s%s = %s' % (['','export '][k[0] == '+'],
                                           [k, k[1:]][k[0] == '+'],
                                           v))(v,
                                               bb.data.getVar(v.lstrip('+'),
                                                              d, 1)),
               filter(lambda k: bb.data.getVar(k.lstrip('+'), d, 0)
                      != None, vars))

    bb.data.setVar('_export_vars_gen', '\n'.join(res), d)
}

do_configure[depends] = "gcc-cross:do_populate_sysroot"
do_configure() {
        set >&2
	gc=`${WHICH} ${CROSS_COMPILE}gcc`
	gc=${gc%%gcc}
	dn=`dirname "$gc"`
	gc=`basename "$gc"`

	rm -f "${DEVELCOMP_MAKEFILE}"
        cat << EOF | sed -e 's![[:space:]]*$!!' > "${DEVELCOMP_MAKEFILE}"
## --*- makefile -*--    ${PV}-${PR}
## This file was created by the 'elito-develcomp' recipe.  Any manual
## changes will get lost on next rebuild of this package.

${_export_vars_gen}

export DESTDIR  = ${IMAGE_ROOTFS}

export _CCACHE	= `${WHICH} ccache 2>/dev/null`
export _CROSS	= ${TARGET_PREFIX}
export _ARCH	= ${TARGET_ARCH}

_tmpdir		= ${TMPDIR}

_kernel_tftp_image ?= ${KERNEL_TFTP_IMAGE}
_tftp_server	?= ${TFTP_SERVER}

_nfs_root	?= \$(DESTDIR)
_nfs_server	?= \$(_tftp_server)

_secwrap        = ${SECWRAP_CMD}

SH     ?= /bin/bash
PS1     = [\\[\\033[1;34m\\]${PROJECT_NAME}\\[\\033[0;39m\\]|\\u@\\h \\W]\\044$
_start	= env -u MAKELEVEL \$(ENV) \$(_secwrap)

define __include_cfg
ifneq (\$\$(wildcard ${PROJECT_TOPDIR}/mk/common.mk),)
include ${PROJECT_TOPDIR}/mk/common.mk
endif

ifneq (\$\$(wildcard ${PROJECT_TOPDIR}/mk/\$1.mk),)
include ${PROJECT_TOPDIR}/mk/\$1.mk
else ifneq (\$\$(wildcard ${ELITO_TOPDIR}/mk/\$1.mk),)
include ${ELITO_TOPDIR}/mk/\$1.mk
else ifneq (\$1,)
\$\$(error "no setup for CFG=\$1 found")
else
OPTS ?=
ENV  ?=
endif

endef

\$(foreach c,\${CFG},\$(eval \$(call __include_cfg,\$c)))


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
