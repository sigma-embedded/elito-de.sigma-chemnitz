DESCRIPTION = "ELiTo makefile"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PR = "r7"

PACKAGE_ARCH = "${MACHINE_ARCH}"

DEVELCOMP_MAKEFILE  ?= "${ELITO_GIT_WS}/Makefile.${PROJECT_NAME}"
KERNEL_LDSUFFIX = "\$(KERNEL_LDSUFFIX)"

inherit kernel

_DEPENDS = "gcc-cross ccache-native"
PACKAGES = ""
INHIBIT_DEFAULT_DEPS = "1"
PROVIDES = "elito-makefile-native"

python __anonymous() {
    d.setVar("DEPENDS", "${_DEPENDS}")
}

_export_vars = " \
	+AR		\
	ARCH		\
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
	CROSS_COMPILE	\
	+CXX		\
	+CXXFLAGS	\
	ELITO_TOPDIR	\
	FAKEROOTDIRS	\
	FAKEROOTENV	\
	FAKEROOTNOENV	\
	+F77		\
	FLASH_ERASESIZE	\
	IMAGE_ROOTFS	\
	KERNEL_CC	\
	KERNEL_LD	\
	KERNEL_TFTP_IMAGE \
	KERNEL_SIZE	\
	KERNEL_START_ADDRESS \
	KERNEL_BOOT_VARIANT \
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
	MACHINE		\
	PROJECT_NAME	\
	PROJECT_TOPDIR	\
	QPEDIR		\
	+QTDIR		\
	+RANLIB		\
	SECWRAP_CMD	\
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
	STAGING_DIR	\
	STAGING_LIBDIR	\
	STAGING_DIR_HOST \
	STAGING_DIR_NATIVE \
	STAGING_DIR_TARGET \
	STAGING_BINDIR_TOOLCHAIN \
	TARGET_ARCH	\
	TARGET_PREFIX	\
	TFTP_SERVER	\
	TFTPBOOT_DIR	\
	BUILD_ARCH	\
	HOST_ARCH	\
	HOST_SYS	\
	PACKAGE_ARCHS	\
	DEPLOY_DIR_IPK	\
	DEPLOY_DIR_IMAGE \
"

python __anonymous () {
    vars = bb.data.getVar('_export_vars', d, 1).split()
    res  = map(lambda v:
               (lambda k,v: '%s%s = %s' % (['','export '][k[0] == '+'],
                                           [k, k[1:]][k[0] == '+'],
                                           v))
               (v, bb.data.getVar(v.lstrip('+'), d, 1)),
               filter(lambda k: bb.data.getVar(k.lstrip('+'), d, 0) != None,
                      vars))

    bb.data.setVar('_export_vars_gen', '\n'.join(res), d)
}

do_setup_makefile[dirs] = "${WORKDIR}/setup-makefile"
do_setup_makefile[nostamp] = "1"
do_setup_makefile() {
        set >&2

	rm -f "Makefile.develcomp" "make"
        cat << EOF | sed -e 's![[:space:]]*$!!' > "Makefile.develcomp"
## --*- makefile -*--    ${PV}-${PR}
## This file was created by the 'elito-develcomp' recipe.  Any manual
## changes will get lost on next rebuild of this package.

${_export_vars_gen}

export _CCACHE	= ${CCACHE}
_tmpdir		= ${TMPDIR}

include ${ELITO_TOPDIR}/mk/_develcomp.mk
EOF

	cat << "EOF" > "make"
#! /bin/sh
exec ${ELITO_TOPDIR}/scripts/make-redir '${TMPDIR}/Makefile.develcomp' "$@"
EOF

        # fix perms and make it read-only
	chmod a-w "Makefile.develcomp"
	chmod a-w,a+rx "make"
}

SSTATETASKS += "do_setup_makefile"
do_setup_makefile[sstate-name] = "setup-makefile"
do_setup_makefile[sstate-inputdirs] = "${WORKDIR}/setup-makefile"
do_setup_makefile[sstate-outputdirs] = "${TMPDIR}"

do_sizecheck[deps] := ""
do_compile_kernelmodules[deps] := ""

do_sizecheck[noexec] = "1"
do_compile_kernelmodules[noexec] = "1"
do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"
do_compile_kernelmodules[noexec] = "1"
do_savedefconfig[noexec] = "1"
do_sizecheck[noexec] = "1"
do_deploy[noexec] = "1"
do_uboot_mkimage[noexec] = "1"
do_configure[noexec] = "1"
do_package[noexec] = "1"
do_packagedata[noexec] = "1"
do_package_write_ipk[noexec] = "1"

addtask do_setup_makefile before do_populate_sysroot do_build after do_configure

###########

do_create_link() {
        ${@base_conditional('DISTRO_TYPE','debug','','return 0', d)}

        rm -f "${DEVELCOMP_MAKEFILE}"
        ln -sf "${TMPDIR}/Makefile.develcomp" "${DEVELCOMP_MAKEFILE}"
}

addtask do_create_link before do_populate_sysroot after do_setup_makefile
