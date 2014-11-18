IMAGE_STREAM_PRESCRIPT  ?= ${PROJECT_TOPDIR}/files/rescue-prescript
IMAGE_STREAM_POSTSCRIPT ?= ${PROJECT_TOPDIR}/files/rescue-postscript
IMAGE_STREAM_AUTOBOOT   ?= ${IMAGEDIR}/autoboot.bat
IMAGE_STREAM_BOOTBIN    ?= ${IMAGEDIR}/boot.bin

SKIP_STREAM_PRESCRIPT =
SKIP_STREAM_POSTSCRIPT =

SKIP_STREAM_BOOTBIN =
SKIP_STREAM_AUTOBOOT =
SKIP_STREAM_KERNEL =
SKIP_STREAM_RESCUE =
SKIP_STREAM_ROOTFS =

SKIP_STREAM_ANDROID_SYSTEM = t
SKIP_STREAM_ANDROID_DATA = t
SKIP_STREAM_ANDROID_CACHE = t

IMAGE_STREAM_ARGS = \
  $(call genopts,0x10000,PRESCRIPT) \
  $(call genopts,0x11000,AUTOBOOT) \
  $(call genopts,0x11001,BOOTBIN) \
  $(call genopts,0x11010,KERNEL) \
  $(call genopts,0x11011,RESCUE) \
  $(call genopts,0x12000,ROOTFS) \
  $(call genopts,0x13000,ANDROID_SYSTEM) \
  $(call genopts,0x13001,ANDROID_DATA) \
  $(call genopts,0x13002,ANDROID_CACHE) \
  $(call genopts,0x10001,POSTSCRIPT) \
