_elito_skip := "${@elito_skip(d, None, 'nokernel')}"

DESCRIPTION      = "ELiTo Linux kernel"
SECTION          = "kernel"
LICENSE          = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

PROVIDES         = "virtual/elito-kernel"

_defconfig       = "${KERNEL_DEFCONFIG}"

OVERRIDE_KERNEL_CMDLINE	= "1"

KERNEL_IMAGE_EXTRASIZE ?= "1024"
KERNEL_IMAGE_MAXSIZE = "${@kernel_maxsize('KERNEL_SIZE',${KERNEL_IMAGE_EXTRASIZE}, d)}"

KERNEL_SOC_FAMILY ?= "${@(d.getVar('SOC_FAMILY', True) or "").split(':')[0]}"

MACHINE_KERNEL_REVISION ?= "${AUTOREV}"

SRCREV        = "${MACHINE_KERNEL_REVISION}"
_branch       = "${MACHINE_KERNEL_VERSION}/${KERNEL_BRANCH}"
KERNEL_REPO ??= "git+file://${ELITO_GIT_WS}/kernel.git"
KERNEL_REV  ??= "branch=${_branch}"
SRC_URI       = "${KERNEL_REPO};${KERNEL_REV}"
SRC_URI[vardepsexclude] += "KERNEL_REPO"

inherit kernel gitpkgv
include elito-kernel.inc
include elito-kernel_soc-${KERNEL_SOC_FAMILY}.inc

PV = "${MACHINE_KERNEL_VERSION}+gitr${SRCPV}"
PKGV = "${MACHINE_KERNEL_VERSION}+gitr${GITPKGV}"

RDEPENDS_kernel-image += "elito-filesystem"
RDEPENDS_kernel-dev += "elito-filesystem"
RDEPENDS_kernel-vmlinux += "elito-filesystem"

do_elito_kernel_checkout() {
	rmdir "${S}"
	mv ${WORKDIR}/git ${S}
}
do_unpack[postfuncs] += "do_elito_kernel_checkout"
