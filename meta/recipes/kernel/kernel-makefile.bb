DESCRIPTION = "ELiTo Linux kernel makefile"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PR = "r1"

INHIBIT_DEFAULT_DEPS = "1"
PROVIDES = "kernel-makefile-native"
PACKAGES = ""
PACKAGE_ARCH = "all"

KERNEL_MAKEFILE ?= "${ELITO_GIT_WS}/Makefile.kernel.${PROJECT_NAME}"

do_setup_makefile[depends] += "elito-makefile:do_setup_makefile"

do_setup_makefile[dirs] = "${WORKDIR}/kernel-makefile"
do_setup_makefile() {
	rm -f "Makefile.kernel"
        cat << EOF | sed -e 's![[:space:]]*$!!' > "Makefile.kernel"
# --*- make -*--
override CFG := kernel
include ${ELITO_MAKEFILE_DIR}/Makefile.develcomp
EOF

        # make it read-only
        chmod a-w "Makefile.kernel"
}

SSTATETASKS += "do_setup_makefile"
do_setup_makefile[sstate-name] = "kernel-makefile"
do_setup_makefile[sstate-inputdirs] = "${WORKDIR}/kernel-makefile"
do_setup_makefile[sstate-outputdirs] = "${ELITO_MAKEFILE_DIR}"

addtask do_setup_makefile after do_configure
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

do_create_link[vardeps] += "DISTRO_TYPE"
do_create_link() {
        ${@base_conditional('DISTRO_TYPE','debug','','return 0', d)}

        rm -f "${KERNEL_MAKEFILE}"
        ln -sf "${ELITO_MAKEFILE_DIR}/Makefile.kernel" "${KERNEL_MAKEFILE}"
}

addtask do_create_link after do_setup_makefile
do_populate_sysroot[depends] += "${PN}:do_create_link"
