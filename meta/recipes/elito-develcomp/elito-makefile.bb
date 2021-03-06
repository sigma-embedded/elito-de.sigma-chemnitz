DESCRIPTION = "ELiTo makefile"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PV = "0.1"

PACKAGE_ARCH = "${MACHINE_ARCH}"
# from kernel.bbclass
CROSS_COMPILE = "${TARGET_PREFIX}"

DEVELCOMP_MAKEFILE  ?= "${ELITO_GIT_WS}/${@makefile_name(d)}"

inherit kernel-arch elito-nfsroot gcc-binutils

DEPENDS = "gcc-cross-${TARGET_ARCH} libgcc ccache-native"
PACKAGES = ""
INHIBIT_DEFAULT_DEPS = "1"
PROVIDES = "elito-makefile-native"

OVERRIDES_append = ":pkgtype-${IMAGE_PKGTYPE}"

OPTIONAL_DEPENDENCIES = "\
  ${@bb.utils.contains('MACHINE_FEATURES', 'nokernel', '', 'kernel dtree nfsd', d)} \
  ${@bb.utils.contains('MACHINE_FEATURES', 'nommu', '', 'pkgmgmt', d)} \
  late \
"
OPTIONAL_DEPENDENCIES[type] = "list"

OPTIONAL_DEPENDS_kernel += "bc-native dtc-native coreutils-native \
                            lzop-native lz4-native xz-native kmod-native"
OPTIONAL_DEPENDS_dtree  += "elito-devicetree-tools-cross-${TARGET_ARCH}"
OPTIONAL_DEPENDS_late   += "coreutils-native"
OPTIONAL_DEPENDS_nfsd   += "unfs3-native pseudo-native"

OPTIONAL_DEPENDS_pkgmgmt = ""
OPTIONAL_DEPENDS_pkgmgmt_pkgtype-ipk = "opkg-utils-native opkg-native"

IMAGE_ROOTFS = ""
KERNEL_IMAGETYPE ??= ""

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
	ELITO_ROOTDIR	\
	ELITO_PROJECT_UUID \
	FAKEROOTDIRS	\
	FAKEROOTENV	\
	FAKEROOTNOENV	\
	+F77		\
	FLASH_ERASESIZE	\
	IMAGE_ROOTFS	\
	KERNEL_CC	\
	KERNEL_LD	\
	KERNEL_SIZE	\
	KERNEL_START_ADDRESS \
	KERNEL_BOOT_VARIANT \
	KERNEL_LOADADDR \
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
	STAGING_KERNEL_DIR \
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
    vars = d.getVar('_export_vars', True).split()
    res  = map(lambda v:
               (lambda k,v: '%s%s = ${%s}' % (['','export '][k[0] == '+'],
                                           v, v))
               (v, v.lstrip('+')),
               filter(lambda k: d.getVar(k.lstrip('+'), False) != None,
                      vars))

    d.setVar('_export_vars_gen', '\n'.join(res))

    sed = []
    idx = 0
    for (regex, groups) in [(r'STAGING_DIR_\(HOST\|NATIVE\|TARGET\)',
                             ('STAGING_DIR_NATIVE', 'STAGING_DIR_TARGET',
                              'STAGING_DIR_HOST')),
                            (r'STAGING_DIR', ('STAGING_DIR',)),
                            (r'CROSS_COMPILE', ('CROSS_COMPILE',))]:
        sed.append('\\!' +
                   (r'^\(export[[:space:]]\)\?[[:space:]]*%s[[:space:]]*=' % regex) +
                   '!b _l%u' % idx)

        sed.extend(map(lambda g: 's!${%s}!$\\{%s}!g' % (g, g), groups))

        sed.append(': _l%u' % idx)

        idx = idx+1

    sed.append('s!${TMPDIR}!$\\{ELITO_BUILDSYS_TMPDIR}!g')

    d.setVar('_sed_relocate',
             ' '.join(map(lambda x: '-e %s' %
                          elito_quote(x).replace('$\\{', "$''{"), sed)))
}

do_setup_makefile[vardeps] += "ELITO_PROJECT_UUID"
do_setup_makefile[dirs] = "${WORKDIR}/setup-makefile"
do_setup_makefile() {
        set >&2

	rm -f "Makefile.develcomp" "make"
        sed ${_sed_relocate} -e 's![[:space:]]*$!!' << EOF > "Makefile.develcomp"
## --*- makefile -*--    ${PV}-${PR}
## This file was created by the 'elito-develcomp' recipe.  Any manual
## changes will get lost on next rebuild of this package.

${_export_vars_gen}

export _CCACHE	= ${CCACHE}
_tmpdir		= ${TMPDIR}

_FQDN =		\$(shell hostname -f 2>/dev/null)
_DOMAIN =	\$(shell hostname -d 2>/dev/null)

-include \$(PROJECT_TOPDIR)/Makefile.local
-include \$(PROJECT_TOPDIR)/Makefile.local.\$(_DOMAIN)
-include \$(PROJECT_TOPDIR)/Makefile.local.\$(_FQDN)
EOF

cat << "EOF" >>"Makefile.develcomp"

ELITO_BUILDSYS_TMPDIR	= ${TMPDIR}

include ${ELITO_TOPDIR}/mk/_develcomp.mk
EOF

	cat << "EOF" > "make"
#! /bin/sh
exec ${ELITO_TOPDIR}/scripts/make-redir '${ELITO_MAKEFILE_DIR}/Makefile.develcomp' "$@"
EOF

        # fix perms and make it read-only
	chmod a-w "Makefile.develcomp"
	chmod a-w,a+rx "make"
}

_sysroot_base = "${TMPDIR}/work-shared/${MACHINE}/elito-makefile"
RECIPE_SYSROOT  = "${_sysroot_base}/recipe-sysroot"
RECIPE_SYSROOT_NATIVE  = "${_sysroot_base}/recipe-sysroot-native"

SSTATETASKS += "do_setup_makefile"
do_setup_makefile[sstate-name] = "setup-makefile"
do_setup_makefile[sstate-inputdirs] = "${WORKDIR}/setup-makefile"
do_setup_makefile[sstate-outputdirs] = "${ELITO_MAKEFILE_DIR}"

addtask do_setup_makefile after do_configure before do_build
do_populate_sysroot[depends] += "${PN}:do_setup_makefile"

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"
do_package[noexec] = "1"
do_packagedata[noexec] = "1"
do_package_write_ipk[noexec] = "1"

###########

do_clean[cleandirs] = "${_sysroot_base}"

do_create_link() {
        if ln -r -s x ${B}/ln-r-test 2>/dev/null; then
                LN_R='ln -r -s'
        else
                LN_R='ln -s'
        fi

        rm -f "${DEVELCOMP_MAKEFILE}"
        $LN_R "${ELITO_MAKEFILE_DIR}/Makefile.develcomp" "${DEVELCOMP_MAKEFILE}"
}
addtask do_create_link after do_setup_makefile

do_prepare_all_sysroots[nostamp] = "1"
do_prepare_all_sysroots() {
	:
}
addtask do_prepare_all_sysroots before do_build

do_populate_sysroot[depends] += "${PN}:do_create_link"

def makefile_name(d):
    mc = d.getVar('BB_CURRENT_MC', False)
    if mc == 'default':
        return 'Makefile.${PROJECT_NAME}'
    else:
        return 'Makefile_%s.${PROJECT_NAME}' % mc

def generate_deps(d, var):
    return ' '.join(map(lambda x: '%s:do_populate_sysroot' % x, \
                    d.getVar(var, False).split()))

python () {
    FN = "    bb.build.exec_func('extend_recipe_sysroot', d)"

    for v in sorted(set(oe.data.typed_value("OPTIONAL_DEPENDENCIES", d))):
        tsk = "do_prepare_recipe_sysroot_%s" % (v.lower(),)
        var = "OPTIONAL_DEPENDS_%s" % (v.lower(),)

        d.setVar(tsk, FN)
        d.setVarFlag(tsk, 'func', 1)
        d.setVarFlag(tsk, 'python', 1)
        d.setVarFlag(tsk, "filename", "autogenerated")
        d.setVarFlag(tsk, "lineno", 1)
        #d.setVarFlag(tsk, "nostamp", 1)

        d.appendVarFlag(tsk, 'vardeps', var)
        d.appendVarFlag(tsk, 'depends', "${@generate_deps(d, '%s')}" % var)

        bb.build.addtask(tsk, "do_prepare_all_sysroots",
                         "do_prepare_recipe_sysroot", d)
}

RM_WORK_EXCLUDE = "${PN}"
