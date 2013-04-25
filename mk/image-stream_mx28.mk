IMAGE_PRESCRIPT ?=	${PROJECT_TOPDIR}/files/rescue-prescript
IMAGE_POSTSCRIPT ?=	${PROJECT_TOPDIR}/files/rescue-postscript
IMAGE_BOOTSTREAM0 ?=	${IMAGEDIR}/u-boot-$(MACHINE).sb
IMAGE_BOOTSTREAM1 ?=	${IMAGEDIR}/rescue-zImage-$(MACHINE).sb
IMAGE_KERNEL ?=		${IMAGEDIR}/uImage-$(MACHINE).bin
IMAGE_ROOTIMG ?=	${IMAGEDIR}/elito-image-$(MACHINE).ext4

SKIP_PRESCRIPT =
SKIP_POSTSCRIPT =

SKIP_BOOTSTREAM0 =
SKIP_BOOTSTREAM1 =
SKIP_KERNEL =
SKIP_ROOTIMG =

SKIP_ANDROID_SYSTEM = t
SKIP_ANDROID_DATA = t
SKIP_ANDROID_CACHE = t

IMAGE_STREAM_deps = \
	${IMAGE_BOOTSTREAM0} \
	${IMAGE_BOOTSTREAM1} \
	${IMAGE_KERNEL} \
	${IMAGE_ROOTIMG}

ARGS = \
  $(call genopts,0x20000,PRESCRIPT,t) \
  $(call genopts,0x21000,BOOTSTREAM0) \
  $(call genopts,0x21001,BOOTSTREAM1) \
  $(call genopts,0x21002,KERNEL) \
  $(call genopts,0x22000,ROOTIMG) \
  $(call genopts,0x20001,POSTSCRIPT,t) \