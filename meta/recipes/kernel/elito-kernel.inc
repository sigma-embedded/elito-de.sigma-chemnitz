_elito_skip := "${@elito_skip(d, None, 'nokernel')}"

export ELITO_EXTKBUILD_DISABLED = "1"

inherit deploy

EXTRA_OEMAKE_prepend = "-f '${ELITO_MAKEFILE_DIR}/Makefile.kernel' \
  -C '${S}' \
  TFTP_IMAGE='${_tftp_image}' KALLSYMS_EXTRA_PASS=1 \
  KBUILD_OUTPUT=${B}/ \
"

B = "${WORKDIR}/build"

KERNEL_CMDLINE ??= "console=${KERNEL_CONSOLE} loglevel=4 earlyprintk"
KERNEL_CMDLINE_SOURCE ?= "bootloader"
KERNEL_DEPENDS ?= ""
MACHINE_HOSTNAME ?= "${MACHINE}"

INITRAMFS_SOURCE = "devices.txt"

DEPENDS[vardeps] += "KERNEL_IMAGETYPE"
DEPENDS += '\
	bc-native \
	dtc-native \
	coreutils-native \
	${KERNEL_DEPENDS} \
	${@bb.utils.contains("MACHINE_FEATURES","ce-bootme",\
                             "ce-bootme-native virtual/ce-preloader","",d)} \
	${@bb.utils.contains("MACHINE_FEATURES","arnoldboot",\
                             "arnoldboot-native","",d)} \
	${@base_conditional("KERNEL_IMAGETYPE","uImage",\
                            "virtual/u-boot-mkimage-native","",d)}'

KERNEL_CONFIG_ENABLE_OPTS[type] = "list"
KERNEL_CONFIG_DISABLE_OPTS[type] = "list"
KERNEL_EARLY_OPTIONS_FUNCS[type] = "list"
KERNEL_OPTIONS_FUNCS[type] = "list"

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

def _get_defconfig(cfg):
    if not cfg.startswith('file://'):
        cfg = None
    else:
        cfg = cfg[7:]
        if cfg == '':
            cfg = 'kernel_defconfig'

    return cfg

def _get_defconfig_filelist(d, cfg):
    res = []
    if cfg.startswith('file://'):
        fetcher = bb.fetch2.Fetch((cfg,), d)
        ud = fetcher.ud[cfg]
        paths = ud.method.localpaths(ud, d)
        res.extend(map(lambda f: "%s:%s" % (f, os.path.exists(f)), paths))
    return ' '.join(res)

def get_name_defconfig(cfg):
    tmp = _get_defconfig(cfg)
    if tmp:
        return "${WORKDIR}/%s" % tmp
    else:
        return cfg

do_fetch_defconfig[file-checksums] += "${@_get_defconfig_filelist(d, d.getVar('_defconfig', True))}"
do_fetch_defconfig[vardeps] += "_defconfig"
python do_fetch_defconfig() {
    cfg = d.getVar('_defconfig', True)
    if cfg.startswith('file://'):
        try:
            fetcher = bb.fetch2.Fetch((cfg,), d)
            fetcher.unpack(d.getVar('WORKDIR', True))
        except bb.fetch2.BBFetchException as e:
            raise bb.build.FuncFailed(e)
}
addtask fetch_defconfig before do_configure

kernel_options[vardeps] += "\
  DISTRO_FEATURES MACHINE_FEATURES KERNEL_CMDLINE \
  KERNEL_CMDLINE_SOURCE KERNEL_EARLY_OPTIONS_FUNCS \
  _configdebug-opts _configdisable-opts \
  IMAGE_INIT_MANAGER KERNEL_OPTIONS_FUNCS"
def kernel_options(d):
    import elito.kernel_feature

    YES = elito.kernel_feature.YES
    OFF = elito.kernel_feature.OFF

    is_feature = lambda x: (bb.utils.contains('DISTRO_FEATURES', x, True, False, d) or
                            bb.utils.contains('MACHINE_FEATURES', x, True, False, d))

    have_initrd = (is_feature('initrd') or
                   ('cpio' in set(map(lambda x: x.split('.')[0],
                                      d.getVar('IMAGE_FSTYPES', True).split()))))
    have_earlyprintk = 'earlyprintk' in (d.getVar('KERNEL_CMDLINE', True) or '').split()

    o = [ [ d.getVar('OVERRIDE_KERNEL_CMDLINE', True) != '' and d.getVar('KERNEL_CMDLINE', True) != '',
            ['CMDLINE', d.getVar('KERNEL_CMDLINE', True) ],
            [ '+EARLY_PRINTK', have_earlyprintk ],
          ],
          [ True,
            [ 'DANGEROUS', False ],
            [ '+CMDLINE_FROM_BOOTLOADER', d.getVar('KERNEL_CMDLINE_SOURCE', True) == 'bootloader' ],
            [ '+CMDLINE_EXTEND',          d.getVar('KERNEL_CMDLINE_SOURCE', True) == 'extend' ],
            [ '+CMDLINE_FORCE',           d.getVar('KERNEL_CMDLINE_SOURCE', True) == 'force' ] ],
          [ True,
            [ '+BLK_DEV_INITRD',     have_initrd ],
            [ 'DEFAULT_HOSTNAME',    "${MACHINE_HOSTNAME}" ],
            [ 'NFS_FAKE_UID_NUMBER', os.getuid() ],
            [ 'INITRAMFS_ROOT_UID',  os.getuid() ],
            [ 'INITRAMFS_ROOT_GID',  os.getgid() ] ],
          [ is_feature('initrd'),
            [ 'INITRAMFS_COMPRESSION_XZ', YES ],
            [ 'RD_XZ', YES ],
            [ 'RD_GZIP', False ],
            [ 'INITRAMFS_SOURCE', '${INITRAMFS_SOURCE}' ] ],
          [ True,
            [ '+IPV6',        is_feature('ipv6') ],
            [ '+MMC',         is_feature('mmc') or is_feature('sdio') ],
            [ '+USB',         is_feature('usbhost') ],
            [ '+USB_GADGET',  is_feature('usbclient') ],
            ],
          [ is_feature('headless'),
            [ 'FB',         False ] ],
          [ d.getVar('IMAGE_INIT_MANAGER', True) == 'systemd',
            [ 'AUTOFS4_FS', True ],
            [ '+CGROUPS',    True ] ],
          ]

    o.extend(map(lambda x: [True, 'DEBUG_' + x, False], d.getVar('_configdebug-opts', True).split()))
    o.extend(map(lambda x: [True, x, False], d.getVar('_configdisable-opts', True).split()))

    for f in oe.data.typed_value('KERNEL_EARLY_OPTIONS_FUNCS', d):
        bb.warn("Executing legacy KERNEL_EARLY_OPTIONS_FUNCS '%s'" % f)
        eval("%s(d, o)" % f)

    o.extend(map(lambda x: [True, x, False],
                 oe.data.typed_value('KERNEL_CONFIG_DISABLE_OPTS', d)))

    o.extend(map(lambda x: [True, x, True],
                 oe.data.typed_value('KERNEL_CONFIG_ENABLE_OPTS', d)))

    for f in oe.data.typed_value('KERNEL_OPTIONS_FUNCS', d):
        eval("%s(d, o)" % f)

    return elito.kernel_feature.sed_cmd(o)

def find_cfgs(d):
    sources=src_patches(d, True)
    sources_list=[]
    for s in sources:
        if s.endswith('.cfg'):
            sources_list.append(s)

    return ' '.join(sources_list)

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
                     (d.getVarFlag("do_compile", "depends", False) or ""))

        d.setVarFlag("do_compile", "deps",
                     ["do_generate_initramfs_source"] +
                     (d.getVarFlag("do_compile", "deps", False) or []))
}
# }}} generate-initramfs

# {{{ do_configure
do_configure_extra_local() {
	:
}
export do_configure_extra_local

do_configure[depends] += "kernel-makefile:do_setup_makefile"
do_configure_prepend() {
	rm -f .config
	if "${@['false', 'true'][d.getVar('__defconfig', True).startswith('/')]}"; then
		install -p -m 0644 "${__defconfig}" .config
	else
		oe_runmake "${__defconfig}"
	fi

	cat << "EOF" >> .config
${@kernel_options(d)}
EOF

        cat ${@find_cfgs(d)} < /dev/null >> .config

	oe_runmake oldnoconfig

	do_configure_extra_local
	oe_runmake savedefconfig
}
# }}} do_configure

do_compile_prepare() {
	oe_runmake prepare
}

do_compile_append() {
	if "${@bb.utils.contains('INHERIT', 'buildhistory', 'true', 'false', d)}" && \
           "${@bb.utils.contains('BUILDHISTORY_FEATURES', 'image', 'true', 'false', d)}"; then
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
	rmdir ${D}/lib || :
	rm -f $kerneldir/scripts/kconfig/kxgettext.o
}

KERNEL_SKIP_TFTP_DEPLOY ?= "true"
KERNEL_SKIP_TFTP_DEPLOY[type] = "boolean"

do_uboot_mkimage[vardeps] += "KERNEL_SKIP_TFTP_DEPLOY"
do_uboot_mkimage() {
	have_tftp_image=${@['false', 'true'][not oe.data.typed_value('KERNEL_SKIP_TFTP_DEPLOY', d)]}

	tftp_image_name='arch/${ARCH}/boot/${KERNEL_IMAGETYPE}'

        if ${@bb.utils.contains('MACHINE_FEATURES','arnoldboot','true','false',d)}; then
                for i in sdram gdb flash; do
                        oe_runmake arnoldboot-$i TFTP_IMAGE=${KERNEL_OUTPUT}-$i
                done
		have_tftp_image=false
        elif ${@bb.utils.contains('MACHINE_FEATURES','ce-bootme','true','false',d)}; then
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

do_deploy_append() {
	# TODO: this is broken...
        if ${@bb.utils.contains('MACHINE_FEATURES','arnoldboot','true','false',d)}; then
                for i in sdram gdb flash; do
                        cd "${B}"
                        install -m 0644 ${KERNEL_OUTPUT}-$i \
				${DEPLOYDIR}/${KERNEL_IMAGE_BASE_NAME}-$i.bin

			cd "${DEPLOYDIR}"
			rm -f ${KERNEL_IMAGE_SYMLINK_NAME}-$i.bin
			ln -sf ${KERNEL_IMAGE_BASE_NAME}-$i.bin ${KERNEL_IMAGE_SYMLINK_NAME}-$i.bin
		done
        elif ${@bb.utils.contains('MACHINE_FEATURES','ce-bootme','true','false',d)}; then
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
    mtdsize = d.getVar(mtdsize_var, True)
    if mtdsize:
        return "%s" % (int(mtdsize) - xtra)
    else:
        return ""

addtask compile_prepare before do_compile after do_configure
