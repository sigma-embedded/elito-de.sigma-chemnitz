DESCRIPTION = "ELiTo Linux kernel makefile"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PR = "r1"

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "elito-makefile"
PROVIDES = "kernel-makefile-native"
PACKAGES = ""
PACKAGE_ARCH = "all"

KERNEL_MAKEFILE ?= "${ELITO_GIT_WS}/Makefile.kernel.${PROJECT_NAME}"

do_setup_makefile[dirs] = "${WORKDIR}/kernel-makefile"
do_setup_makefile() {
	rm -f "Makefile.kernel"
        cat << EOF | sed -e 's![[:space:]]*$!!' > "Makefile.kernel"
# --*- make -*--
override CFG := kernel
include ${TMPDIR}/Makefile.develcomp
EOF

        # make it read-only
        chmod a-w "Makefile.kernel"
}

SSTATETASKS += "do_setup_makefile"
do_setup_makefile[sstate-name] = "kernel-makefile"
do_setup_makefile[sstate-inputdirs] = "${WORKDIR}/kernel-makefile"
do_setup_makefile[sstate-outputdirs] = "${TMPDIR}"

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

addtask setup_makefile before do_populate_sysroot after do_configure

###########

do_create_link() {
        ${@base_conditional('DISTRO_TYPE','debug','','return 0', d)}

        rm -f "${KERNEL_MAKEFILE}"
        ln -sf "${TMPDIR}/Makefile.kernel" "${KERNEL_MAKEFILE}"
}

addtask create_link before do_populate_sysroot after do_setup_makefile
