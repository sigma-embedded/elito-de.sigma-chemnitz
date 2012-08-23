PROVIDES = "virtual/elito-image"

EXTRA_IMAGEDEPENDS = "	\
	${MACHINE_TASK_PROVIDER}	\
"

IMAGE_INSTALL = "${MACHINE_TASK_PROVIDER}"
IMAGE_BOOT    = ""

IMAGE_PREPROCESS_COMMAND += "elito_cleanup_boot"

do_rootfs_prepend () {
	PATH=$PATH:/usr/local/sbin:/usr/sbin:/sbin
}

elito_cleanup_boot() {
    	rm -f ${IMAGE_ROOTFS}/boot/*
}

require elito-image.inc
inherit ubi arnoldboot elito-final
inherit elito-image
