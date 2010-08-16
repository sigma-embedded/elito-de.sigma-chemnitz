_elito_skip := "${@elito_skip(d, None, 'nokernel')}"

DESCRIPTION      = "ELiTo Linux kernel"
SECTION          = "kernel"
LICENSE          = "GPL"

PR               = "r9"
KERNEL_TFTP_IMAGE ?= ""
PROVIDES         = "virtual/elito-kernel"

_tftp_image      = "${KERNEL_TFTP_IMAGE}"
_defconfig       = "${KERNEL_DEFCONFIG}"

OVERRIDE_KERNEL_CMDLINE	= 1

KERNEL_IMAGE_EXTRASIZE ?= "1024"
KERNEL_IMAGE_MAXSIZE = "${@kernel_maxsize('KERNEL_SIZE',${KERNEL_IMAGE_EXTRASIZE}, d)}"

inherit kernel

include elito-kernel.inc
