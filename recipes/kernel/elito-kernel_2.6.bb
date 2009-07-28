DESCRIPTION	 = "ELiTo Linux kernel"
SECTION		 = "kernel"
LICENSE		 = "GPL"

PR		 = "r2"
#KERNEL_IMAGE_BASE_NAME = "${KERNEL_IMAGETYPE}-${KERNEL_RELEASE}-${PR_PKG}-${MACHINE}"
KERNEL_TFTP_IMAGE ?= ""

_branch          = "${MACHINE_KERNEL_VERSION}/${KERNEL_BRANCH}"
_tftp_image	 = "${KERNEL_TFTP_IMAGE}"
_defconfig	 = "${KERNEL_DEFCONFIG}"

include elito-kernel.inc
inherit kernel
