ENV = \
	-u CPPFLAGS -u CFLAGS -u LDFLAGS

OPTS = \
	MAKEFLAGS= \
	AR:='$(AR)' AS:='$(AS)' CC:='$(CC)' CPP:='$(CPP)' \
	LD:='$(LD)' NM:='$(NM)' STRIP:='$(STRIP)' \
	OBJCOPY:='$(OBJCOPY)' OBJDUMP:='$(OBJDUMP)' \
	RANLIB:='$(RANLIB)' HOSTCC:='$(BUILD_CC)' \
	HOSTCFLAGS:='$(BUILD_CFLAGS)' CROSS_COMPILE:='$(_CROSS)' \
	$(if ${_kernel_tftp_image},CFG_BOOTFILE:='$(basename $(_kernel_tftp_image))') \
	$(if ${_tftp_server},CFG_SERVERIP='${_tftp_server}') \
        CFG_NFSROOT='MK_STR(CONFIG_SERVERIP) ":${DESTDIR}"'

