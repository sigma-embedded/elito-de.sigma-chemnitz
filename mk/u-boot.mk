ENV = $(addprefix -u ,CPPFLAGS CFLAGS LDFLAGS \
	ARCH CPU BOARD VENDOR SOC \
	VERSION PATCHLEVEL SUBLEVEL EXTRAVERSION \
	HOSTARCH HOSTOS SHELL)

OPTS = \
	MAKEFLAGS= \
	AR:='$(AR)' AS:='$(AS)' CC:='$(CC)' CPP:='$(CPP)' \
	LD:='$(LD)' NM:='$(NM)' STRIP:='$(STRIP)' \
	OBJCOPY:='$(OBJCOPY)' OBJDUMP:='$(OBJDUMP)' \
	RANLIB:='$(RANLIB)' HOSTCC:='$(BUILD_CC)' \
	HOSTCFLAGS:='$(BUILD_CFLAGS)' CROSS_COMPILE:='$(_CROSS)' \
	$(if ${_kernel_tftp_image},CFG_BOOTFILE:='$(notdir $(_kernel_tftp_image))') \
	$(if ${_tftp_server},CFG_SERVERIP='${_tftp_server}') \
	CFG_NFSROOT='"${_nfs_server}:${_nfs_root}"'
