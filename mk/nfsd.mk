ifeq (${IMAGE_ROOTFS},)
$(error "nfsd can not be used with --disable-nfsd")
endif

include ${ELITO_TOPDIR}/mk/nfs-opt.mk

LOCALGOALS =	start-daemon start-debug stop-daemon

UNFSD_IMAGE ?=		elito-image
UNFSD_PSEUDODIR ?=	${STAGING_DIR}/images.pseudo/${UNFSD_IMAGE}
UNFSD_DIR =		${ELITO_BUILDSYS_TMPDIR}/unfsd
UNFSD_PIDFILE =		${UNFSD_DIR}/unfsd.pid
UNFSD_OPTS = \
	-t \
	-i ${UNFSD_PIDFILE} \
	-p \
	-e ${UNFSD_DIR}/exports \
	-n "${UNFSD_NFS_PORT}" \
	-m "${UNFSD_MOUNT_PORT}"

UNFSD =		unfsd

UNFSD_EXPORTS ?=	${IMAGE_ROOTFS}

define unfsd_add_export
echo "$1 (ro,no_root_squash,no_all_squash,insecure)" >> $2

endef

_pseudo = env \
	ELITO_PSEUDO_ENABLE=1 \
	PSEUDO_LOCALSTATEDIR='${UNFSD_PSEUDODIR}' \
\
	${ELITO_TOPDIR}/scripts/run-pseudo \
	"${PROJECT_TOPDIR}"

start-daemon:	${UNFSD_DIR}/exports
	${_pseudo} ${UNFSD} ${UNFSD_OPTS}

stop-daemon:
	test ! -e "${UNFSD_PIDFILE}" || kill "`cat ${UNFSD_PIDFILE}`"

start-debug:	${UNFSD_DIR}/exports
	${_pseudo} ${UNFSD} ${UNFSD_OPTS} -d

${UNFSD_DIR}/exports:	| ${UNFSD_DIR}/.dirstamp
	@rm -f $@
	$(foreach e,${UNFSD_EXPORTS},$(call unfsd_add_export,$e,$@))

%/.dirstamp:
	mkdir -p ${@D}
