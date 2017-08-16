_elito_skip := "${@elito_skip(d, None, 'nokernel')}"

export ELITO_EXTKBUILD_DISABLED = "1"

inherit deploy

EXTRA_OEMAKE_prepend = " \
  -C '${S}' \
  KALLSYMS_EXTRA_PASS=1 \
  KBUILD_OUTPUT=${B}/ \
"

B = "${WORKDIR}/build"

KERNEL_CMDLINE ??= "console=${KERNEL_CONSOLE} loglevel=4 earlyprintk"
KERNEL_CMDLINE_SOURCE ?= "bootloader"
KERNEL_DEPENDS ?= ""
MACHINE_HOSTNAME ?= "${MACHINE}"

DEPENDS[vardeps] += "KERNEL_IMAGETYPE"
DEPENDS += '\
	bc-native \
	dtc-native \
	lzop-native \
	lz4-native \
	xz-native \
	gzip-native \
	coreutils-native \
	${KERNEL_DEPENDS} \
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
            [ 'DEFAULT_HOSTNAME',    "${MACHINE_HOSTNAME}" ],
            [ 'NFS_FAKE_UID_NUMBER', os.getuid() ] ],
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

# {{{ do_configure
do_configure_extra_local() {
	:
}
export do_configure_extra_local

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

KERNEL_MODULES_NAME ?= "modules-${KERNEL_VERSION}-${PR}-${MACHINE}.tgz"

def kernel_maxsize(mtdsize_var, xtra, d):
    mtdsize = d.getVar(mtdsize_var, True)
    if mtdsize:
        return "%s" % (int(mtdsize) - xtra)
    else:
        return ""

addtask compile_prepare before do_compile after do_configure
