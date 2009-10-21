ENV = \
	-u CPPFLAGS -u CFLAGS -u LDFLAGS

OPTS = \
	AR:='$(AR)' AS:='$(AS)' CC:='$(CC)' CPP:='$(CPP)' \
	LD:='$(LD)' NM:='$(NM)' STRIP:='$(STRIP)' \
	OBJCOPY:='$(OBJCOPY)' OBJDUMP:='$(OBJDUMP)' \
	RANLIB:='$(RANLIB)' HOSTCC:='$(BUILD_CC)' \
	HOSTCFLAGS:='$(BUILD_CFLAGS)' CROSS_COMPILE:='$(_CROSS)'
