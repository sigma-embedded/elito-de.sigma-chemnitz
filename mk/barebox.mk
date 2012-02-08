LOCALGOALS += barebox.sb tftp-sb tftp
XTRA_GOALS += barebox

override CFG := kernel
override KERNEL_BOOT_VARIANT :=
include ${ELITO_TOPDIR}/mk/kernel.mk

ELFTOSB = freescale-elftosb

barebox.sb:	barebox
	$(_start) ${ELFTOSB} ${BAREBOX_ELFTOSB_FLAGS} -o $@ $<

tftp-sb:	barebox.sb
	cat $< >${BAREBOX_TFTP_FILE}

.PHONY:	barebox barebox.sb tftp-sb
