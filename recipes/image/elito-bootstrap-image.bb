OVERRIDES .= ${@base_contains('MACHINE_FEATURES','mobm320',':mobm320','',d)}
OVERRIDES .= ${@base_contains('MACHINE_FEATURES','u-boot',':u-boot','',d)}

DEPENDS = "	\
	${MACHINE_TASK_PROVIDER}	\
	${IMAGE_DEPENDS}		\
"

IMAGE_LINGUAS = ""
IMAGE_INSTALL = "${MACHINE_TASK_PROVIDER}"

IMAGE_FSTYPES_append_mobm320	 = " bootenv"
IMAGE_DEPENDS_append_u-boot	 = " u-boot"

export OVERRIDES
export DEPENDS
export IMAGE_DEPENDS

inherit image ubi elito-final
