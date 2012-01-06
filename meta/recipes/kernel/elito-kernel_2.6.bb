_elito_skip := "${@elito_skip(d, None, 'nokernel')}"

DESCRIPTION      = "ELiTo Linux kernel"
SECTION          = "kernel"
LICENSE          = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

PR               = "r12"
KERNEL_TFTP_IMAGE ?= ""
PROVIDES         = "virtual/elito-kernel"

_tftp_image      = "${KERNEL_TFTP_IMAGE}"
_defconfig       = "${KERNEL_DEFCONFIG}"

OVERRIDE_KERNEL_CMDLINE	= 1

KERNEL_MAKEFILE ?= "${ELITO_GIT_WS}/Makefile.kernel.${PROJECT_NAME}"
KERNEL_IMAGE_EXTRASIZE ?= "1024"
KERNEL_IMAGE_MAXSIZE = "${@kernel_maxsize('KERNEL_SIZE',${KERNEL_IMAGE_EXTRASIZE}, d)}"

inherit kernel

do_generate_makefile_append() {
        ${@base_conditional('DISTRO_TYPE','debug','',': ', d)}\
        ln -sf "${_gen_mf}" "${KERNEL_MAKEFILE}"
}

include elito-kernel.inc

RDEPENDS_kernel-image += "elito-filesystem"
RDEPENDS_kernel-dev += "elito-filesystem"
RDEPENDS_kernel-vmlinux += "elito-filesystem"
