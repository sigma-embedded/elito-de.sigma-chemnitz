_elito_skip := "${@elito_skip(d, None, 'nokernel')}"

PV = "${MACHINE_KERNEL_VERSION}+gitr${SRCPV}"
PKGV = "${MACHINE_KERNEL_VERSION}+gitr${GITPKGV}"

export ELITO_EXTKBUILD_DISABLED = "1"

inherit gitpkgv deploy
BB_LOCALCOUNT_OVERRIDE = "1"

MACHINE_KERNEL_REVISION ?= "${AUTOREV}"

GIT_REPO = "${KERNEL_REPO}"
SRCREV   = "${MACHINE_KERNEL_REVISION}"

KERNEL_REPO ?= "${ELITO_GIT_WS}/kernel.git"
_branch      = "${MACHINE_KERNEL_VERSION}/${KERNEL_BRANCH}"

SRC_URI      = "git://${KERNEL_REPO};protocol=file;branch=${_branch}"
S            = "${WORKDIR}/git"

EXTRA_OEMAKE_prepend = "-f '${ELITO_MAKEFILE_DIR}/Makefile.kernel' \
  _secwrap= TFTP_IMAGE='${_tftp_image}' KALLSYMS_EXTRA_PASS=1 \
"

KERNEL_CMDLINE ??= "console=${KERNEL_CONSOLE} loglevel=4 earlyprintk"
KERNEL_CMDLINE_SOURCE ?= "bootloader"
KERNEL_DEPENDS ?= ""
MACHINE_HOSTNAME ?= "${MACHINE}"

INITRAMFS_SOURCE = "devices.txt"

DEPENDS[vardeps] += "KERNEL_IMAGETYPE"
DEPENDS += '\
	bc-native \
	${KERNEL_DEPENDS} \
	${@base_contains("MACHINE_FEATURES","ce-bootme","ce-bootme-native virtual/ce-preloader","",d)} \
	${@base_contains("MACHINE_FEATURES","arnoldboot","arnoldboot-native","",d)} \
	${@base_conditional("KERNEL_IMAGETYPE","uImage","virtual/u-boot-mkimage-native","",d)}'

_configdebug-opts='\
  SHIRQ OBJECTS OBJECTS_FREE OBJECTS_WORK OBJECTS_RCU_HEAD \
  OBJECTS_PERCPU_COUNTER OBJECTS_ENABLE_DEFAULT KMEMLEAK \
  PREEMPT RT_MUTEXES PI_LIST SPINLOCK MUTEXES LOCK_ALLOC \
  SPINLOCK_SLEEP VM WRITECOUNT MEMORY_INIT LIST SG LOCKING \
  LOCKDEP PAGEALLOC KOBJECT LOCKING_API_SELFTESTS \
  SHIRQ OBJECTS_TIMERS NOTIFIERS DEVRES UIDGID_STRICT_TYPE_CHECKS \
'

_configdisable-opts = ' \
  LOCKDEP PROVE_RCU SLUB_DEBUG_ON SCHED_DEBUG SLUB_STATS \
  PROVE_LOCKING LOCK_STAT USB_DEBUG \
'

__defconfig[vardeps] += "_defconfig"
__defconfig = "${@get_name_defconfig(d.getVar('_defconfig',True))}"

SRC_URI[vardeps] += "_defconfig"
SRC_URI += "${@get_src_uri_defconfig(d.getVar('_defconfig',True))}"

def _get_defconfig(cfg):
    if not cfg.startswith('file://'):
        cfg = None
    else:
        cfg = cfg[7:]
        if cfg == '':
            cfg = 'kernel_defconfig'

    return cfg

def get_name_defconfig(cfg):
    tmp = _get_defconfig(cfg)
    if tmp:
        return "../../../../%s" % tmp
    else:
        return cfg

def get_src_uri_defconfig(cfg):
    cfg = _get_defconfig(cfg)
    if cfg:
        cfg = 'file://%s' % cfg
    else:
        cfg = ""

    return cfg

kernel_early_options[vardeps] += "DISTRO_FEATURES KERNEL_CMDLINE \
  KERNEL_CMDLINE_SOURCE  KERNEL_EARLY_OPTIONS_FUNCS \
  _configdebug-opts _configdisable-opts"
def kernel_early_options(d):
    import elito.kernel_feature

    YES = elito.kernel_feature.YES
    OFF = elito.kernel_feature.OFF

    is_feature = lambda x: bb.utils.contains('DISTRO_FEATURES', x, True, False, d)
    have_initrd = (is_feature('initrd') or
                   ('cpio' in set(map(lambda x: x.split('.')[0],
                                      d.getVar('IMAGE_FSTYPES', True).split()))))
    have_earlyprintk = 'earlyprintk' in (d.getVar('KERNEL_CMDLINE', True) or '').split()

    o = [ [ d.getVar('DISTRO_TYPE') == 'release', 'DANGEROUS', False ],
          [ d.getVar('OVERRIDE_KERNEL_CMDLINE') != '' and d.getVar('KERNEL_CMDLINE') != '',
            ['CMDLINE', d.getVar('KERNEL_CMDLINE') ], 
            [ 'EARLY_PRINTK', [False, YES][have_earlyprintk] ],
          ],
          [ True,
            [ 'CMDLINE_FROM_BOOTLOADER', [False, YES][d.getVar('KERNEL_CMDLINE_SOURCE') == 'bootloader'] ],
            [ 'CMDLINE_EXTEND',          [False, YES][d.getVar('KERNEL_CMDLINE_SOURCE') == 'extend'] ],
            [ 'CMDLINE_FORCE',           [False, YES][d.getVar('KERNEL_CMDLINE_SOURCE') == 'force'] ] ],
          [ True,
            [ 'BLK_DEV_INITRD',      [False, YES][have_initrd] ],
            [ 'DEFAULT_HOSTNAME',    "${MACHINE_HOSTNAME}" ],
            [ 'NFS_FAKE_UID_NUMBER', os.getuid() ],
            [ 'INITRAMFS_ROOT_UID',  os.getuid() ],
            [ 'INITRAMFS_ROOT_GID',  os.getgid() ] ],
          [ is_feature('initrd'),
            [ 'INITRAMFS_COMPRESSION_XZ', YES ],
            [ 'RD_XZ', YES ],
            [ 'KERNEL_XZ', YES ],
            [ 'KERNEL_GZIP', False ],
            [ 'RD_GZIP', False ],
            [ 'INITRAMFS_SOURCE', '${INITRAMFS_SOURCE}' ] ],
          ]

    o.extend(map(lambda x: [True, 'DEBUG_' + x, False], d.getVar('_configdebug-opts').split()))
    o.extend(map(lambda x: [True, x, False], d.getVar('_configdisable-opts').split()))

    for f in (d.getVar('KERNEL_EARLY_OPTIONS_FUNCS', True) or '').split():
        o = eval("%s(d, o)" % f)

    return elito.kernel_feature.sed_cmd(o)

kernel_options[vardeps] += "DISTRO_FEATURES IMAGE_INIT_MANAGER KERNEL_OPTIONS_FUNCS"
def kernel_options(d):
    import elito.kernel_feature
    is_feature = lambda x: bb.utils.contains('DISTRO_FEATURES', x, True, False, d)

    o = [ [ True,
            [ 'IPV6',        is_feature('ipv6') ],
            [ 'MMC',         is_feature('mmc') ],
            [ 'USB',         is_feature('usbhost') ],
            [ 'USB_GADGET',  is_feature('usbclient') ],
            ],
          [ is_feature('headless'),
            [ 'FB',         False ] ],
          [ d.getVar('IMAGE_INIT_MANAGER') == 'systemd',
            [ 'AUTOFS4_FS', True ],
            [ 'CGROUPS',    True ] ],
          ]

    for f in (d.getVar('KERNEL_OPTIONS_FUNCS', True) or '').split():
        o = bb.build.exec_func(f, d, o)

    return elito.kernel_feature.sed_cmd(o)

# {{{ generate-initramfs
ELITO_LINUXRC_PROGRAM ??= "${STAGING_BINDIR}/elito-mmc-boot"
ELITO_LINUXRC_TASK ??= "elito-setup-tools"

do_generate_initramfs_source[depends] += "${ELITO_LINUXRC_TASK}:do_populate_sysroot"
do_generate_initramfs_source() {
	# copy our program into a temporary location and remove
	# debugging symbols
	install -D -p -m 0755 ${ELITO_LINUXRC_PROGRAM} ${B}/.initramfs/init
	${STRIP} --remove-section=.comment --remove-section=.note --preserve-dates ${B}/.initramfs/init

	# create devices.txt
	rm -f ${B}/devices.txt
	cat << EOF > ${B}/devices.txt
file /init ${B}/.initramfs/init	0755 0 0

dir  /mnt  0755 0 0
dir  /dev  0755 0 0
dir  /sys  0755 0 0
dir  /run  0755 0 0
dir  /proc 0755 0 0

nod  /dev/console 0644 0 0 c 5 1
EOF
}
addtask do_generate_initramfs_source after do_unpack

python () {
    if bb.utils.contains('DISTRO_FEATURES','initrd', True, False, d):
        d.setVarFlag("do_compile", "depends",
                     d.expand('${PN}:do_generate_initramfs_source ') +
                     (d.getVarFlag("do_compile", "depends") or ""))

        d.setVarFlag("do_compile", "deps",
                     ["do_generate_initramfs_source"] +
                     (d.getVarFlag("do_compile", "deps") or []))
}
# }}} generate-initramfs

# {{{ do_configure
do_configure_extra_local() {
	:
}
export do_configure_extra_local

do_configure[depends] += "kernel-makefile:do_setup_makefile"

do_configure_prepend() {
	if ! test -e .config; then
		oe_runmake "${__defconfig}"
	fi

	cp -a .config .config-orig

sed -f - .config-orig > .config << "EOF"
${@kernel_early_options(d)}
EOF

	cp -a .config .config-early_
	oe_runmake oldconfig < /dev/null
	cp -a .config .config-early

sed -f - .config-early > .config << "EOF"
${@kernel_options(d)}
EOF

	do_configure_extra_local

	cmp .config .config-orig || oe_runmake oldconfig < /dev/null

	oe_runmake savedefconfig

	if ! diff -u .config-early .config; then
		bbwarn "Default kernel configuration does not fulfill image/distro requirements; configuration was adjusted but should be rechecked"
	fi
}
# }}} do_configure

do_compile_prepare() {
	oe_runmake prepare
}

do_compile_append() {
	if "${@base_contains('INHERIT', 'buildhistory', 'true', 'false', d)}" && \
	   "${@base_contains('BUILDHISTORY_FEATURES', 'image', 'true', 'false', d)}"; then
		install -D -p -m 0644 .config   ${BUILDHISTORY_DIR_IMAGE}/kernel-config
		install -D -p -m 0644 defconfig ${BUILDHISTORY_DIR_IMAGE}/kernel-defconfig
	fi
}

do_install_prepend() {
	mkdir -p ${D}/lib/firmware
}

do_install_append() {
	# ignore errors; firmware directory might be non-empty
	rmdir ${D}/lib/firmware || :
	rm -f $kerneldir/scripts/kconfig/kxgettext.o
}

KERNEL_SKIP_TFTP_DEPLOY ?= "true"
KERNEL_SKIP_TFTP_DEPLOY[type] = "boolean"

do_uboot_mkimage[vardeps] += "KERNEL_SKIP_TFTP_DEPLOY"
do_uboot_mkimage() {
	have_tftp_image=${@['false', 'true'][not oe.data.typed_value('KERNEL_SKIP_TFTP_DEPLOY', d)]}

	tftp_image_name='arch/${ARCH}/boot/${KERNEL_IMAGETYPE}'

        if ${@base_contains('MACHINE_FEATURES','arnoldboot','true','false',d)}; then
                for i in sdram gdb flash; do
                        oe_runmake arnoldboot-$i TFTP_IMAGE=${KERNEL_OUTPUT}-$i
                done
		have_tftp_image=false
        elif ${@base_contains('MACHINE_FEATURES','ce-bootme','true','false',d)}; then
		oe_runmake ceImage
		have_tftp_image=false
	fi

	if ! $have_tftp_image; then
		:			# noop
        elif test -w "${_tftp_image}"; then
                cat "$tftp_image_name" > "${_tftp_image}"
        else
		bbwarn "File '${_tftp_image}' not writable; skipping staging of kernel into TFTP root"
	fi
}
addtask uboot_mkimage before do_install after do_compile_kernelmodules

KERNEL_MODULES_NAME ?= "modules-${KERNEL_VERSION}-${PR}-${MACHINE}.tgz"

kernel_do_deploy() {
	if (grep -q -i -e '^CONFIG_MODULES=y$' .config); then
		tar -cvzf ${DEPLOYDIR}/${KERNEL_MODULES_NAME} -C ${D} lib
	fi

        for data in \
		'arch/${ARCH}/boot/${KERNEL_IMAGETYPE} bin' \
		'.config config' \
		'vmlinux vmlinux'; do
		set -- $data

		install -m 0644 "$1" ${DEPLOYDIR}/${KERNEL_IMAGE_BASE_NAME}.$2

		rm -f ${DEPLOYDIR}/${KERNEL_IMAGE_SYMLINK_NAME}.$2
		ln -s ${KERNEL_IMAGE_BASE_NAME}.$2 ${DEPLOYDIR}/${KERNEL_IMAGE_SYMLINK_NAME}.$2
	done
}

do_deploy_append() {
        if ${@base_contains('MACHINE_FEATURES','arnoldboot','true','false',d)}; then
                for i in sdram gdb flash; do
                        cd "${B}"
                        install -m 0644 ${KERNEL_OUTPUT}-$i \
				${DEPLOYDIR}/${KERNEL_IMAGE_BASE_NAME}-$i.bin

			cd "${DEPLOYDIR}"
			rm -f ${KERNEL_IMAGE_SYMLINK_NAME}-$i.bin
			ln -sf ${KERNEL_IMAGE_BASE_NAME}-$i.bin ${KERNEL_IMAGE_SYMLINK_NAME}-$i.bin
		done
        elif ${@base_contains('MACHINE_FEATURES','ce-bootme','true','false',d)}; then
		cd "${B}"
		install -p -m 0644 arch/${ARCH}/boot/ceImage \
			${DEPLOYDIR}/${KERNEL_IMAGE_BASE_NAME}.ce

		cd "${DEPLOYDIR}"
		rm -f ${KERNEL_IMAGE_SYMLINK_NAME}.ce
		ln -sf ${KERNEL_IMAGE_BASE_NAME}.ce ${KERNEL_IMAGE_SYMLINK_NAME}.ce

		# REMOVE ME after 2013-07-01
		# remove old stylish named file
		rm -f ${KERNEL_IMAGE_SYMLINK_NAME}-ce.bin
	fi
}

def kernel_maxsize(mtdsize_var, xtra, d):
    mtdsize = bb.data.getVar(mtdsize_var, d, True)
    if mtdsize:
        return "%s" % (int(mtdsize) - xtra)
    else:
        return ""

addtask compile_prepare before do_compile after do_configure
