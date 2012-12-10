export CC = $(KERNEL_CC)
export LD = $(KERNEL_LD)
export ARCH = $(TARGET_ARCH)
export INSTALL_MOD_PATH = ${IMAGE_ROOTFS}
export CROSS_COMPILE

# there are various problems when using the gold linker; fallback to
# .bfd for now
KERNEL_LDSUFFIX = .bfd

TFTP_IMAGE ?= $(KERNEL_TFTP_IMAGE)

_bad_env += CFLAGS CPPFLAGS CXXFLAGS LDFLAGS MACHINE _secwrap

_build_cmd = \
            $(_start) \
            $(MAKE) CC='$(KERNEL_CC)' LD='$(KERNEL_LD)' \
            $(if $(TFTP_IMAGE),FLASH_FILENAME='$(TFTP_IMAGE)') \
            $(if $(KERNEL_SIZE),KCPPFLAGS+='-DKERNEL_MTD_SIZE=$(KERNEL_SIZE)') \

LOCALGOALS += _all_ exec shell printcmd
XTRA_GOALS += modules modules_install _all

ifdef KERNEL_BOOT_VARIANT
LOCALGOALS += tftp tftp-m

_kernel_image_types = bzImage zImage uImage
_kernel_image_files = $(addprefix arch/$(ARCH)/boot/,$(_kernel_image_types))
LOCALGOALS += $(_kernel_image_files)

override _k_all_target =

tftp-m:	_k_all_target=_all
tftp-m:	tftp
	+$(_build_cmd) modules_install

$(_kernel_image_files):arch/$(ARCH)/boot/%:
	+$(_build_cmd) ${_k_all_target} $*

.PHONY:	$(_kernel_image_files)

include ${ELITO_TOPDIR}/mk/kernel_boot-${KERNEL_BOOT_VARIANT}.mk
endif

$(sort $(filter-out $(LOCALGOALS),${MAKECMDGOALS}) ${XTRA_GOALS}):	__force
	+$(_build_cmd) $@

printcmd:
	@echo make -C "`pwd`" -f $(firstword $(MAKEFILE_LIST))

_all_:
	+$(_build_cmd)

exec:
	$(_secwrap) $(P)

shell:
	@env PS1='$(PS1) ' $(_start) $(SH) -

__force:

.PHONY:	exec _all_ __force

_SKIP_DEVELCOMP_RULES := 1
