PROVIDES = "virtual/elito-image"

EXTRA_IMAGEDEPENDS = "	\
	${MACHINE_TASK_PROVIDER}	\
"

IMAGE_INSTALL = "${MACHINE_TASK_PROVIDER}"
IMAGE_BOOT    = ""

do_rootfs_prepend () {
	PATH=$PATH:/usr/local/sbin:/usr/sbin:/sbin
}

require elito-image.inc
inherit ubi arnoldboot elito-final
inherit elito-image
