include ${ELITO_TOPDIR}/mk/nfs-opt.mk

LOCALGOALS =	start-daemon start-debug stop-daemon

UNFSD_IMAGE ?=		elito-image
UNFSD_PSEUDODIR ?=	${STAGING_DIR}/images.pseudo/${UNFSD_IMAGE}
UNFSD_DIR =	${ELITO_BUILDSYS_TMPDIR}/unfsd
UNFSD_OPTS = \
	-t \
	-i ${UNFSD_DIR}/unfsd.pid \
	-p \
	-e ${UNFSD_DIR}/exports \
	-n "${UNFSD_NFS_PORT}" \
	-m "${UNFSD_MOUNT_PORT}"

UNFSD =		unfsd

_pseudo = env \
	ELITO_PSEUDO_ENABLE=1 \
	PSEUDO_LOCALSTATEDIR='${UNFSD_PSEUDODIR}' \
\
	${ELITO_TOPDIR}/scripts/run-pseudo \
	"${PROJECT_TOPDIR}"

start-daemon:	${UNFSD_DIR}/exports
	${_pseudo} ${UNFSD} ${UNFSD_OPTS}

start-debug:	${UNFSD_DIR}/exports
	${_pseudo} ${UNFSD} ${UNFSD_OPTS} -d

${UNFSD_DIR}/exports:	| ${UNFSD_DIR}/.dirstamp
	@rm -f $@
	echo '${IMAGE_ROOTFS}	(ro,no_root_squash,no_all_squash,insecure)' > $@

%/.dirstamp:
	mkdir -p ${@D}
