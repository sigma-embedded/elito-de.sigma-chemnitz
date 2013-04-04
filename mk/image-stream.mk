####

_check_skip = $(SKIP_$1)$(if $2,$(if $(wildcard ${IMAGE_$1}),,t,),)
genopts = $(if $(call _check_skip,$2,$3),,-h $1${SIGNATURE_OPTS}!${IMAGE_$2})

-include ${ELITO_TOPDIR}/mk/image-stream_${SOC_FAMILY}.mk
-include ${ELITO_TOPDIR}/mk/image-stream_${MACHINE}.mk
-include ${PROJECT_TOPDIR}/mk/image-stream.local

SIGNATURE_OPTS ?= ,sha1,zlib
LOCALGOALS =	image-stream


image-stream:
	$(_secwrap) elito-stream-encode ${ARGS} > $(O)
