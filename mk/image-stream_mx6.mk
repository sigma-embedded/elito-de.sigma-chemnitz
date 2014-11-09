IMAGE_STREAM_PRESCRIPT ?=	${PROJECT_TOPDIR}/files/rescue-prescript
IMAGE_STREAM_POSTSCRIPT ?=	${PROJECT_TOPDIR}/files/rescue-postscript
IMAGE_STREAM_BOOTSTREAM0 ?=	${IMAGEDIR}/barebox-$(MACHINE).img
IMAGE_STREAM_KERNEL ?=		${IMAGEDIR}/zImage-$(MACHINE).bin
IMAGE_STREAM_ROOTIMG ?=		${IMAGEDIR}/elito-image-$(MACHINE).ext4

SKIP_PRESCRIPT =
SKIP_POSTSCRIPT =

SKIP_BOOTSTREAM0 =
SKIP_KERNEL =
SKIP_ROOTIMG =

SKIP_ANDROID_SYSTEM = t
SKIP_ANDROID_DATA = t
SKIP_ANDROID_CACHE = t

IMAGE_STREAM_deps = \
	${IMAGE_STREAM_BOOTSTREAM0} \
	${IMAGE_STREAM_KERNEL} \
	${IMAGE_STREAM_ROOTIMG}

BOOTSTREAM ?= $(call genopts,0x21100,BOOTSTREAM0) \

ARGS = \
  $(call genopts,0x20000,PRESCRIPT,t) \
  $(BOOTSTREAM) \
  $(call genopts,0x21002,KERNEL) \
  $(call genopts,0x22000,ROOTIMG) \
  $(call genopts,0x23000,ANDROID_SYSTEM) \
  $(call genopts,0x23001,ANDROID_DATA) \
  $(call genopts,0x23002,ANDROID_CACHE) \
  $(call genopts,0x20001,POSTSCRIPT,t) \
