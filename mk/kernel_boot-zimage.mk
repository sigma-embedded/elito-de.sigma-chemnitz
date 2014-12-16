UBOOT_GOALS	 = tftp-zimage
LOCALGOALS	+= ${UBOOT_GOALS}

tftp-zimage:	${KBUILD_BOOT_DIR}/zImage
	cat $< >$(TFTP_IMAGE)

tftp:	tftp-zimage
