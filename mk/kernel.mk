export CC = $(KERNEL_CC)
export LD = $(KERNEL_LD)
export INSTALL_MOD_PATH = ${IMAGE_ROOTFS}
export CROSS_COMPILE

ifneq ($(filter ${TARGET_ARCH},i386 i486 i586 i686),)
  export ARCH = x86
else
  export ARCH = $(TARGET_ARCH)
endif

include ${ELITO_TOPDIR}/mk/nfs-opt.mk

CFG_NFSOPTS = ,v3,tcp,nolock${CFG_NFSOPTS_EXTRA}
CFG_NFSROOT = ${_nfs_server}:${_nfs_root}

export DEFAULT_NFSROOT = ${CFG_NFSROOT}${CFG_NFSOPTS}

unexport

# there are various problems when using the gold linker; fallback to
# .bfd for now
KERNEL_LDSUFFIX = .bfd

TFTP_IMAGE ?= $(KERNEL_TFTP_IMAGE)
KBUILD_OUTPUT ?= $(if ${ELITO_EXTKBUILD_DISABLED},,${ELITO_BUILDSYS_TMPDIR}/work/${KBUILD_OUTPUT_DIR}/)
KBUILD_OUTPUT_DIR = kernel-$(firstword $(shell echo $(abspath .) | md5sum))
KBUILD_BOOT_DIR = ${KBUILD_OUTPUT}arch/${ARCH}/boot

_bad_env += CFLAGS CPPFLAGS CXXFLAGS LDFLAGS MACHINE _secwrap

_build_cmd = \
            $(_start) env UID=$$UID \
            $(MAKE) CC='$(KERNEL_CC)' LD='$(KERNEL_LD)' \
            KBUILD_OUTPUT=${KBUILD_OUTPUT} \
            DEPMOD='${STAGING_DIR_NATIVE}/usr/bin/depmod' \
            $(if $(TFTP_IMAGE),FLASH_FILENAME='$(TFTP_IMAGE)') \
            $(if $(KERNEL_SIZE),KCPPFLAGS+='-DKERNEL_MTD_SIZE=$(KERNEL_SIZE)') \
            $(if $(KERNEL_LOADADDR),LOADADDR='$(KERNEL_LOADADDR)') \

LOCALGOALS += _all_ exec shell printcmd printvars
XTRA_GOALS += modules modules_install _all

ifdef KERNEL_BOOT_VARIANT
LOCALGOALS += tftp tftp-m check-syntax

_kernel_image_types = bzImage zImage uImage
_kernel_image_files = $(addprefix ${KBUILD_BOOT_DIR}/,$(_kernel_image_types))
LOCALGOALS += $(_kernel_image_files)

override _k_all_target =

tftp-m:	_k_all_target=all modules
tftp-m:	tftp
	+$(_build_cmd) modules_install

check-syntax:
	+$(_build_cmd) -f flymake.mk

$(_kernel_image_files):${KBUILD_BOOT_DIR}/%:
	+$(_build_cmd) ${_k_all_target} $*

.PHONY:	$(_kernel_image_files)

include ${ELITO_TOPDIR}/mk/kernel_boot-${KERNEL_BOOT_VARIANT}.mk
endif

$(sort $(filter-out $(LOCALGOALS),${MAKECMDGOALS}) ${XTRA_GOALS}):	__force
	+$(_build_cmd) $@

printcmd:
	@echo make -C "`pwd`" -f $(firstword $(MAKEFILE_LIST)) $(if $(CFG),CFG='$(CFG)')

printvars:
	@echo KBUILD_OUTPUT='${KBUILD_OUTPUT}'

_all_:
	+$(_build_cmd)

exec:
	$(_secwrap) $(P)

shell:
	@env PS1='$(PS1) ' $(_start) $(SH) -

__force:

.PHONY:	exec _all_ __force

_SKIP_DEVELCOMP_RULES := 1
