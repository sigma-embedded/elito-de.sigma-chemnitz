DESCRIPTION	 = "ELiTo Linux kernel"
SECTION		 = "kernel"
LICENSE		 = "GPL"

PR		 = "r2"
KERNEL_TFTP_IMAGE ?= ""

_tftp_image	 = "${KERNEL_TFTP_IMAGE}"
_defconfig	 = "${KERNEL_DEFCONFIG}"

include elito-kernel.inc
inherit kernel
