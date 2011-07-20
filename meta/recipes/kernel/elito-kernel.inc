_elito_skip := "${@elito_skip(d, None, 'nokernel')}"

PV = "${MACHINE_KERNEL_VERSION}+gitr${SRCPV}"
PKGV = "${MACHINE_KERNEL_VERSION}+gitr${GITPKGV}"

inherit gitpkgv deploy
BB_LOCALCOUNT_OVERRIDE = "1"

GIT_REPO = "${KERNEL_REPO}"
SRCREV   = "${AUTOREV}"

KERNEL_REPO ?= "${ELITO_GIT_WS}/kernel.git"
_branch      = "${MACHINE_KERNEL_VERSION}/${KERNEL_BRANCH}"

SRC_URI      = "git://${KERNEL_REPO};protocol=file;branch=${_branch}"
S            = "${WORKDIR}/git"

EXTRA_OEMAKE_prepend = "-f ${_gen_mf} _secwrap= "

KERNEL_DEPENDS ?= ""

do_fetch[depends] += "linux-libc-headers:do_fetch"

DEPENDS += '\
	${KERNEL_DEPENDS} \
	${@base_contains("MACHINE_FEATURES","ce-bootme","ce-bootme-native","",d)} \
	${@base_contains("MACHINE_FEATURES","arnoldboot","arnoldboot-native","",d)} \
	${@base_conditional("KERNEL_IMAGETYPE","uImage","virtual/u-boot-mkimage-native","",d)}'

## Use this indirect assignment to allow recipes which are including
## this rules to define and use their own _gen_mf makefile.
_gen_mf         ?= "${TMPDIR}/Makefile.kernel"

do_generate_makefile[depends] = "gcc-cross:do_populate_sysroot"
do_generate_makefile() {
	gc=`${WHICH} ${CROSS_COMPILE}gcc`
	gc=${gc%%gcc}
	dn=`dirname "$gc"`
	gc=`basename "$gc"`

	rm -f "${_gen_mf}"
	cat << EOF | sed -e 's![[:space:]]*$!!' > "${_gen_mf}"
## --*- makefile -*--
_cc = ${KERNEL_CC}
_ld = ${KERNEL_LD}
_secwrap = ${SECWRAP_CMD}

export PATH = ${PATH}
export CCACHE_DIR = ${CCACHE_DIR}
export CC = \$(_cc)
export LD = \$(_ld)
export CROSS_COMPILE = ${CROSS_COMPILE}
export INSTALL_MOD_PATH = ${IMAGE_ROOTFS}
export ARCH = ${ARCH}

KERNEL_SIZE = ${KERNEL_SIZE}
_flash_filename = ${_tftp_image}
_bad_env = CFLAGS CPPFLAGS CXXFLAGS LDFLAGS MACHINE

_build_cmd = \\
            +env \$(addprefix -u ,\$(_bad_env)) PATH=\$(PATH) \\
            \$(MAKE) MAKELEVEL=0 CC='\$(_cc)' LD='\$(_ld)' \\
            \$(if \$(_flash_filename),FLASH_FILENAME='\$(_flash_filename)') \\
            \$(if \$(KERNEL_SIZE),KCPPFLAGS+='-DKERNEL_MTD_SIZE=\$(KERNEL_SIZE)') \\
            $(_secwrap)

%:
	\$(_build_cmd) \$@

include/linux/version.h:
	\$(_build_cmd) \$@

exec:
	\$(_secwrap) \$(P)

unexport _secwrap
unexport MAKEFILES
unexport MAKELEVEL
.DEFAULT_GOAL := all
.PHONY:	include/linux/version.h exec
EOF

        # make it read-only
        chmod a-w "${_gen_mf}"
}

do_configure_prepend() {
	if ! test -e .config; then
		oe_runmake "${_defconfig}"
	fi

        changed=false
        if test -n "${OVERRIDE_KERNEL_CMDLINE}" -a -n "${KERNEL_CMDLINE}"; then
		sed -i -e '/^CONFIG_CMDLINE=/d' .config
		cat <<EOF >> .config
CONFIG_CMDLINE="${KERNEL_CMDLINE}"
EOF

		changed=true
        fi

        if ${@base_conditional("DISTRO_TYPE", "release", "true", "false",d)}; then
		sed -i -e '/^CONFIG_DANGEROUS=/d' .config
		cat <<EOF >> .config
# CONFIG_DANGEROUS is not set
EOF

		changed=true
        fi

	! $changed || oe_runmake oldconfig < /dev/null
}

do_compile_prepare() {
	oe_runmake prepare
}

do_install_prepend() {
	mkdir -p ${D}/lib/firmware
}

do_uboot_mkimage() {
	have_tftp_image=true
	tftp_image_name='arch/${ARCH}/boot/${KERNEL_IMAGETYPE}'

        if ${@base_contains('MACHINE_FEATURES','arnoldboot','true','false',d)}; then
                for i in sdram gdb flash; do
                        oe_runmake arnoldboot-$i _flash_filename=${KERNEL_OUTPUT}-$i
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

kernel_do_deploy() {
	install -m 0644 arch/${ARCH}/boot/${KERNEL_IMAGETYPE} ${DEPLOYDIR}/${KERNEL_IMAGE_BASE_NAME}.bin
	if (grep -q -i -e '^CONFIG_MODULES=y$' .config); then
		tar -cvzf ${DEPLOYDIR}/modules-${KERNEL_VERSION}-${PR}-${MACHINE}.tgz -C ${D} lib
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

do_generate_makefile[dirs] = "${ELITO_GIT_WS}"
addtask generate_makefile before do_configure after do_unpack

do_compile_prepare[dirs] = "${S}"
addtask compile_prepare before do_compile after do_configure
