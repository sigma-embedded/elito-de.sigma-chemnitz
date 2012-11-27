####

genopts = $(if $(SKIP_$2),,-h $1${SIGNATURE_OPTS}!${IMAGE_$2})

-include ${ELITO_TOPDIR}/mk/image-stream_${MACHINE}.mk
-include ${PROJECT_TOPDIR}/mk/image-stream.local

SIGNATURE_OPTS ?= ,sha1
LOCALGOALS =	image-stream


image-stream:
	$(_secwrap) elito-stream-encode ${ARGS} > $(O)
