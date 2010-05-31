ENV = \
	-u CPPFLAGS -u CFLAGS -u LDFLAGS

OPTS = \
	MAKEFLAGS= \
	AR:='$(AR)' AS:='$(AS)' CC:='$(CC)' CPP:='$(CPP)' \
	LD:='$(LD)' NM:='$(NM)' STRIP:='$(STRIP)' \
	OBJCOPY:='$(OBJCOPY)' OBJDUMP:='$(OBJDUMP)' \
	RANLIB:='$(RANLIB)' HOSTCC:='$(BUILD_CC)' \
	HOSTCFLAGS:='$(BUILD_CFLAGS)' CROSS_COMPILE:='$(_CROSS)' \
	$(if ${_kernel_tftp_image},CONFIG_BOOTFILE:='$(notdir $(_kernel_tftp_image))') \
	$(if ${_tftp_server},CONFIG_SERVERIP='${_tftp_server}') \
        CONFIG_NFSROOT='"${_nfs_server}:${_nfs_root}"'
