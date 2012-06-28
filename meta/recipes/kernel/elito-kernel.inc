_elito_skip := "${@elito_skip(d, None, 'nokernel')}"

PV = "${MACHINE_KERNEL_VERSION}+gitr${SRCPV}"
PKGV = "${MACHINE_KERNEL_VERSION}+gitr${GITPKGV}"

inherit gitpkgv deploy
BB_LOCALCOUNT_OVERRIDE = "1"

GIT_REPO = "${KERNEL_REPO}"
SRCREV   = "${AUTOREV}"

KERNEL_REPO ?= "${ELITO_GIT_WS}/kernel.git"
_branch      = "${MACHINE_KERNEL_VERSION}/${KERNEL_BRANCH}"

SRC_URI      = "git://${KERNEL_REPO};protocol=file;branch=refs/heads/${_branch}"
S            = "${WORKDIR}/git"

EXTRA_OEMAKE_prepend = "-f '${TMPDIR}/Makefile.kernel' _secwrap= TFTP_IMAGE='${_tftp_image}' KALLSYMS_EXTRA_PASS=1"

KERNEL_DEPENDS ?= ""

DEPENDS += '\
	kernel-makefile \
	${KERNEL_DEPENDS} \
	${@base_contains("MACHINE_FEATURES","ce-bootme","ce-bootme-native","",d)} \
	${@base_contains("MACHINE_FEATURES","arnoldboot","arnoldboot-native","",d)} \
	${@base_conditional("KERNEL_IMAGETYPE","uImage","virtual/u-boot-mkimage-native","",d)}'

_configdebug-opts='\
  SHIRQ OBJECTS OBJECTS_FREE OBJECTS_WORK OBJECTS_RCU_HEAD \
  OBJECTS_PERCPU_COUNTER OBJECTS_ENABLE_DEFAULT KMEMLEAK \
  PREEMPT RT_MUTEXES PI_LIST SPINLOCK MUTEXES LOCK_ALLOC \
  SPINLOCK_SLEEP VM WRITECOUNT MEMORY_INIT LIST SG LOCKING \
  LOCKDEP PAGEALLOC KOBJECT LOCKING_API_SELFTESTS \
  SHIRQ OBJECTS_TIMERS NOTIFIERS \
'

_configdebug-opts-re = \
  "\(CONFIG_DEBUG_\(${@'\\|'.join('${_configdebug-opts}'.split())}\)\)"

_configdisable-opts = ' \
  LOCKDEP PROVE_RCU SLUB_DEBUG_ON SCHED_DEBUG SLUB_STATS \
  PROVE_LOCKING LOCK_STAT \
'

_configdisable-opts-re = \
  "\(CONFIG_\(${@'\\|'.join('${_configdisable-opts}'.split())}\)\)"

# {{{ generate-initramfs
ELITO_LINUXRC_PROGRAM ??= "${STAGING_BINDIR}/elito-mmc-boot"
ELITO_LINUXRC_TASK ??= "elito-setup-tools"

do_generate_initramfs_source[depends] += "${ELITO_LINUXRC_TASK}:do_populate_sysroot"
do_generate_initramfs_source() {
	# copy our program into a temporary location and remove
	# debugging symbols
	install -D -p -m 0755 ${ELITO_LINUXRC_PROGRAM} ${S}/.initramfs/init
	${STRIP} --remove-section=.comment --remove-section=.note --preserve-dates ${S}/.initramfs/init

	# create devices.txt
	rm -f ${S}/devices.txt
	cat << EOF > ${S}/devices.txt
file /init ${S}/.initramfs/init	0755 0 0

dir  /mnt  0755 0 0
dir  /dev  0755 0 0
dir  /sys  0755 0 0
dir  /run  0755 0 0
dir  /proc 0755 0 0

nod  /dev/console 0644 0 0 c 5 1
EOF
}
addtask do_generate_initramfs_source after do_unpack
do_configure[depends] += "${@base_contains('DISTRO_FEATURES','initrd','${PN}:do_generate_initramfs_source','', d)}"
# }}} generate-initramfs

# {{{ do_configure
do_configure_extra_local() {
	:
}
export do_configure_extra_local

do_configure_prepend() {
	if ! test -e .config; then
		oe_runmake "${_defconfig}"
	fi

	cp -a .config .config-ref

	sed -e 's!^\(${_configdebug-opts-re}\|${_configdisable-opts-re}\)=.*!# \1 is not set!' .config > .config-nodebug
	if ! cmp .config .config-nodebug; then
		cat .config-nodebug > .config
	fi
	#rm -f .config-nodebug

        if test -n "${OVERRIDE_KERNEL_CMDLINE}" -a -n "${KERNEL_CMDLINE}"; then
		sed -i -e '/^CONFIG_CMDLINE=/d' .config
		cat <<EOF >> .config
CONFIG_CMDLINE="${KERNEL_CMDLINE}"
EOF
        fi

	if ${@base_contains('DISTRO_FEATURES','initrd','true','false',d)}; then
	        sed -i \
		    -e '/^\(# \)\?CONFIG_INITRAMFS_SOURCE=/d' \
		    -e 's/^# \(CONFIG_BLK_DEV_INITRD\) is not set/\1=y/' \		
		.config

		cat <<EOF >> .config
CONFIG_INITRAMFS_SOURCE="devices.txt"
EOF
	fi

        if ${@base_conditional("DISTRO_TYPE", "release", "true", "false",d)}; then
		sed -i -e '/^CONFIG_DANGEROUS=/d' .config
		cat <<EOF >> .config
# CONFIG_DANGEROUS is not set
EOF
        fi

	cp -a .config .config-ref1

	if ${@base_contains('DISTRO_FEATURES','ipv6','true','false',d)}; then
		sed -i -e 's/^# CONFIG_IPV6 is not set/CONFIG_IPV6=m/' .config
	fi

	if ${@base_contains('IMAGE_INIT_MANAGER','systemd','true','false',d)}; then
		sed -i \
		    -e 's/^# \(CONFIG_AUTOFS4_FS\|CONFIG_CGROUPS\) is not set/\1=m/' \
		    -e 's/^# \(CONFIG_SECURITYFS\) is not set/\1=y/' \
		    .config
	fi

	do_configure_extra_local

	if ! diff -u .config-ref1 .config; then
		bbwarn "Default kernel configuration does not fulfill image/distro requirements; configuration was adjusted but should be rechecked"
	fi

	cmp .config .config-ref || oe_runmake oldconfig < /dev/null
}
# }}} do_configure

do_compile_prepare() {
	oe_runmake prepare
}

do_install_prepend() {
	mkdir -p ${D}/lib/firmware
}

do_install_append() {
	find ${D}/lib/modules -type f -name '*.ko' -print0 | xargs -0r chmod a+x
	# ignore errors; firmware directory might be non-empty
	rmdir ${D}/lib/firmware || :
}

do_uboot_mkimage() {
	have_tftp_image=true
	tftp_image_name='arch/${ARCH}/boot/${KERNEL_IMAGETYPE}'

        if ${@base_contains('MACHINE_FEATURES','arnoldboot','true','false',d)}; then
                for i in sdram gdb flash; do
                        oe_runmake arnoldboot-$i TFTP_IMAGE=${KERNEL_OUTPUT}-$i
                done
		have_tftp_image=false
        elif ${@base_contains('MACHINE_FEATURES','ce-bootme','true','false',d)}; then
		oe_runmake ce-boot
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
addtask uboot_mkimage before do_install after do_compile

KERNEL_MODULES_NAME ?= "modules-${KERNEL_VERSION}-${PR}-${MACHINE}.tgz"

kernel_do_deploy() {
	install -m 0644 arch/${ARCH}/boot/${KERNEL_IMAGETYPE} ${DEPLOYDIR}/${KERNEL_IMAGE_BASE_NAME}.bin
	if (grep -q -i -e '^CONFIG_MODULES=y$' .config); then
		tar -cvzf ${DEPLOYDIR}/${KERNEL_MODULES_NAME} -C ${D} lib
	fi

	cd ${DEPLOYDIR}
	rm -f ${KERNEL_IMAGE_SYMLINK_NAME}.bin
	ln -sf ${KERNEL_IMAGE_BASE_NAME}.bin ${KERNEL_IMAGE_SYMLINK_NAME}.bin
	cd -
}

do_deploy_append() {
        if ${@base_contains('MACHINE_FEATURES','arnoldboot','true','false',d)}; then
                for i in sdram gdb flash; do
                        cd "${S}"
                        install -m 0644 ${KERNEL_OUTPUT}-$i \
				${DEPLOYDIR}/${KERNEL_IMAGE_BASE_NAME}-$i.bin

			cd "${DEPLOYDIR}"
			rm -f ${KERNEL_IMAGE_SYMLINK_NAME}-$i.bin
			ln -sf ${KERNEL_IMAGE_BASE_NAME}-$i.bin ${KERNEL_IMAGE_SYMLINK_NAME}-$i.bin
		done
        elif ${@base_contains('MACHINE_FEATURES','ce-bootme','true','false',d)}; then
		cd "${S}"
		install -p -m 0644 arch/${ARCH}/boot/ceImage \
			${DEPLOYDIR}/${KERNEL_IMAGE_BASE_NAME}-ce.bin

		cd "${DEPLOYDIR}"
		rm -f ${KERNEL_IMAGE_SYMLINK_NAME}-ce.bin
		ln -sf ${KERNEL_IMAGE_BASE_NAME}-ce.bin ${KERNEL_IMAGE_SYMLINK_NAME}-ce.bin
	fi
}

def kernel_maxsize(mtdsize_var, xtra, d):
	mtdsize = bb.data.getVar(mtdsize_var, d, True)
        if mtdsize:
		return "%s" % (int(mtdsize) - xtra)
        else:
		return ""

do_compile_prepare[dirs] = "${S}"
addtask compile_prepare before do_compile after do_configure

FILES_${PN}-dbg += "\
  /lib/modules/*/*/.debug \
  /lib/modules/*/*/*/.debug \
  /lib/modules/*/*/*/*/.debug \
  /lib/modules/*/*/*/*/*/.debug \
  /lib/modules/*/*/*/*/*/*/.debug \
  /lib/modules/*/*/*/*/*/*/*/.debug \
  /lib/modules/*/*/*/*/*/*/*/*/.debug \
"
