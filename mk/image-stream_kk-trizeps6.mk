IMAGE_PRESCRIPT  ?= ${PROJECT_TOPDIR}/files/rescue-prescript
IMAGE_POSTSCRIPT ?= ${PROJECT_TOPDIR}/files/rescue-postscript
IMAGE_AUTOBOOT   ?= ${IMAGEDIR}/autoboot.bat
IMAGE_BOOTBIN    ?= ${IMAGEDIR}/boot.bin

SKIP_PRESCRIPT =
SKIP_POSTSCRIPT =

SKIP_BOOTBIN =
SKIP_AUTOBOOT =
SKIP_KERNEL =
SKIP_RESCUE =
SKIP_ROOTFS =

SKIP_ANDROID_SYSTEM = t
SKIP_ANDROID_DATA = t
SKIP_ANDROID_CACHE = t

ARGS = \
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
