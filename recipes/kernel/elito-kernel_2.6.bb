DESCRIPTION      = "ELiTo Linux kernel"
SECTION          = "kernel"
LICENSE          = "GPL"

PR               = "r6"
KERNEL_TFTP_IMAGE ?= ""
PROVIDES         = "virtual/elito-kernel"

_tftp_image      = "${KERNEL_TFTP_IMAGE}"
_defconfig       = "${KERNEL_DEFCONFIG}"

OVERRIDE_KERNEL_CMDLINE	= 1

inherit kernel

include elito-kernel.inc
