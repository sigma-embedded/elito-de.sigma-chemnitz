# there are various problems when using the gold linker; fallback to
# .bfd for now
KERNEL_LDSUFFIX = .bfd

MAKE_E = ${MAKE}

_bad_env += CPPFLAGS CFLAGS LDFLAGS \
	HOSTCFLAGS HOSTCPPFLAGS HOSTLDFLAGS \
	ARCH CPU BOARD VENDOR SOC \
	HOSTARCH CROSS_COMPILE TOPDIR \
	VERSION PATCHLEVEL SUBLEVEL EXTRAVERSION \
	HOSTARCH HOSTOS SHELL \
	OBJTREE SRCTREE CDPATH SUBDIRS CPUDIR \
	obj

OPTS = \
	CROSS_COMPILE='$(_CROSS)' \
	AR='$(AR)' AS='$(AS)' CC='$(CC)' CPP='$(CPP)' \
	LD='$(KERNEL_LD)' NM='$(NM)' STRIP='$(STRIP)' \
	OBJCOPY='$(OBJCOPY)' OBJDUMP='$(OBJDUMP)' \
	RANLIB='$(RANLIB)' \
	HOSTCC='$(BUILD_CC) ${BUILD_CFLAGS} ${BUILD_LDFLAGS}' \
	HOSTSTRIP='$(BUILD_STRIP)' \
	$(if ${_kernel_tftp_image},CFG_BOOTFILE='$(notdir $(_kernel_tftp_image))') \
	$(if ${TFTP_SERVER},CFG_SERVERIP='${_tftp_server_ip}') \
	CFG_NFSROOT='"${_nfs_server}:${_nfs_root}"'

unexport CPPFLAGS HOSTCPPFLAGS HOSTCFLAGS
unexport
