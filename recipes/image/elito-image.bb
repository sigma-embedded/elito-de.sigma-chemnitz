OVERRIDES .= ${@base_contains('MACHINE_FEATURES','mobm320',':fs-mobm320','',d)}
OVERRIDES .= ${@base_contains('MACHINE_FEATURES','u-boot',':fs-u-boot','',d)}
OVERRIDES .= ${@base_contains('MACHINE_FEATURES','ubifs',':fs-ubifs','',d)}
OVERRIDES .= ${@base_contains('MACHINE_FEATURES','jffs2',':fs-jffs2','',d)}
OVERRIDES .= ${@base_contains('MACHINE_FEATURES','arnoldboot',':fs-arnoldboot','',d)}

DEPENDS = "	\
	${MACHINE_TASK_PROVIDER}	\
	${IMAGE_DEPENDS}		\
"

IMAGE_LINGUAS = ""
IMAGE_INSTALL = "${MACHINE_TASK_PROVIDER}"

IMAGE_FSTYPES_append_fs-mobm320 = " bootenv"

IMAGE_DEPENDS_append_fs-u-boot	= " u-boot"
IMAGE_DEPENDS_append_fs-arnoldboot = " arnoldboot-native"

export OVERRIDES
export DEPENDS
export IMAGE_DEPENDS

do_rootfs_prepend () {
	PATH=$PATH:/usr/local/sbin:/usr/sbin:/sbin
}

inherit image ubi arnoldboot elito-final
