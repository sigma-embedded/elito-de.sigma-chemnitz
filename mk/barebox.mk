LOCALGOALS += barebox.sb tftp-sb tftp-bin tftp
XTRA_GOALS += barebox

override KERNEL_BOOT_VARIANT :=
include ${ELITO_TOPDIR}/mk/kernel.mk

ELFTOSB = freescale-elftosb

barebox.sb:	barebox $(BAREBOX_SB_EXTRA)
	$(_start) ${ELFTOSB} ${BAREBOX_ELFTOSB_FLAGS} -o $@ $^

tftp-sb:	barebox.sb
	cat $< >${BAREBOX_TFTP_FILE}

tftp-bin:	barebox
	cat $< >$(BAREBOX_TFTP_FILE)

.PHONY:	barebox barebox.sb tftp-sb tftp-bin

