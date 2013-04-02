BOOTLETS_DIR = ${STAGING_DIR_TARGET}/usr/lib/imx-bootlets

BAREBOX_SB_FILE = ${BOOTLETS_DIR}/uboot_ivt.bd
BAREBOX_KEY = -z
BAREBOX_BOOTARGS = 1000

BAREBOX_ELFTOSB_FLAGS = \
  ${BAREBOX_KEY} -f imx28 -p ${BOOTLETS_DIR} -c ${BAREBOX_SB_FILE} \
  -D BOOTARGS=$(BAREBOX_BOOTARGS)

BAREBOX_TFTP_FILE = ${TFTPBOOT_DIR}/${PROJECT_NAME}.sb
BAREBOX_SB_EXTRA = ${STAGING_DIR_TARGET}/usr/share/mach-$(MACHINE)/$(MACHINE).dtb

ifeq ($(CFG),barebox)
tftp:	tftp-sb
else ifeq ($(CFG),kernel)
tftp:	tftp-uboot
endif
