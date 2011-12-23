PROVIDES = "virtual/elito-image"

DEPENDS = "	\
	${MACHINE_TASK_PROVIDER}	\
	${IMAGE_DEPENDS}		\
"

IMAGE_LINGUAS = ""
IMAGE_INSTALL = "${MACHINE_TASK_PROVIDER}"
IMAGE_BOOT    = ""

export OVERRIDES
export DEPENDS
export IMAGE_DEPENDS

do_rootfs_prepend () {
	PATH=$PATH:/usr/local/sbin:/usr/sbin:/sbin
}

require elito-image.inc
inherit ubi arnoldboot elito-final
