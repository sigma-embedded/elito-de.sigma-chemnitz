DEPENDS += "kern-tools-native"

ARCH_DEFCONFIG ?= '${S}/arch/${ARCH}/configs/${KBUILD_DEFCONFIG}'

python() {
    defconfig = d.getVar('KBUILD_DEFCONFIG', True)
    if defconfig != None and defconfig.startswith('file://'):
        d.appendVar('SRC_URI', ' ' + defconfig)
}

def get_defconfig_path(d, cfg):
    if cfg != None and cfg.startswith('file://'):
        fetcher = bb.fetch2.Fetch((cfg,), d)
        ud = fetcher.ud[cfg]
        return '${WORKDIR}/' + ud.basepath
    else:
        return '${ARCH_DEFCONFIG}'

def find_cfgs(d):
    sources=src_patches(d, True)
    sources_list=[]
    for s in sources:
        if s.endswith('.cfg'):
            sources_list.append(s)

    return ' '.join(sources_list)

FETCHED_DEFCONFIG = "${@get_defconfig_path(d, d.getVar('KBUILD_DEFCONFIG', True))}"
KCONFIG_FRAGMENTS = "${@find_cfgs(d)} ${LOCAL_FRAGMENT}"
LOCAL_FRAGMENT    = "${B}/local-fragments.cfg"

run_merge_config() {
    env CFLAGS="${CFLAGS} ${TOOLCHAIN_OPTIONS}" ARCH='${ARCH}' \
        merge_config.sh -O '${B}' "$@"
}

do_prepare_local_fragment[dirs] = "${B}"
do_prepare_local_fragment() {
        cd '${B}'
        rm -f ${LOCAL_FRAGMENT}
        touch ${LOCAL_FRAGMENT}
}
addtask do_prepare_local_fragment before do_prepare_config

do_prepare_config[depends] = "kern-tools-native:do_populate_sysroot"
do_prepare_config[dirs] += "${S} ${B}"
do_prepare_config() {
    cd '${S}'
    rm -f ${B}/.config
    run_merge_config -n '${FETCHED_DEFCONFIG}' ${KCONFIG_FRAGMENTS}
}
addtask do_prepare_config before do_configure after do_patch

do_configure_append() {
    oe_runmake savedefconfig
}
