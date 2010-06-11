_elito_skip := "${@elito_skip(d, None, 'nokernel')}"

PV = "${MACHINE_KERNEL_VERSION}+gitr${SRCPV}"

GIT_REPO = "${KERNEL_REPO}"
SRCREV   = "${AUTOREV}"

KERNEL_REPO ?= "${ELITO_GIT_WS}/kernel.git"
_branch      = "${MACHINE_KERNEL_VERSION}/${KERNEL_BRANCH}"

SRC_URI      = "git://${KERNEL_REPO};protocol=file;branch=${_branch}"
S            = "${WORKDIR}/git"

EXTRA_OEMAKE_append = " _secwrap="

KERNEL_DEPENDS ?= ""

DEPENDS += '\
	${KERNEL_DEPENDS} \
	${@base_contains("MACHINE_FEATURES","arnoldboot","arnoldboot-native","",d)} \
	${@base_conditional("KERNEL_IMAGETYPE","uImage","u-boot-utils-native","",d)}'

## Use this indirect assignment to allow recipes which are including
## this rules to define and use their own _gen_mf makefile.
##
## When _gen_mf is not set yet, use the public KERNEL_MAKEFILE variable.
KERNEL_MAKEFILE ?= "${ELITO_GIT_WS}/Makefile.kernel.${PROJECT_NAME}"
_gen_mf         ?= "${KERNEL_MAKEFILE}"

do_generate_makefile[depends] = "gcc-cross:do_populate_sysroot"
do_generate_makefile() {
	gc=`${WHICH} ${CROSS_COMPILE}gcc`
	gc=${gc%%gcc}
	dn=`dirname "$gc"`
	gc=`basename "$gc"`
	ccache=`${WHICH} ccache`

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

_flash_filename = ${_tftp_image}
_bad_env = CFLAGS CPPFLAGS CXXFLAGS LDFLAGS MACHINE

%:
	env \$(addprefix -u ,\$(_bad_env)) PATH=\$(PATH) \$(MAKE) CC='\$(_cc)' LD='\$(_ld)' ${@base_conditional("_tftp_image","","","FLASH_FILENAME='\$(_flash_filename)'",d)} MAKELEVEL=0 $(_secwrap) \$@

exec:
	$(_secwrap) \$(P)

unexport _secwrap
unexport MAKEFILES
unexport MAKELEVEL
.DEFAULT_GOAL := all
EOF

        # make it read-only
        chmod a-w "${_gen_mf}"
}

do_configure_prepend() {
	if ! test -e .config; then
		oe_runmake -f "${_gen_mf}" "${_defconfig}"
	fi

        changed=false
        if test -n "${OVERRIDE_KERNEL_CMDLINE}" -a -n "${KERNEL_CMDLINE}"; then
		sed -i -e '/^CONFIG_CMDLINE/d' .config
		cat <<EOF >> .config
CONFIG_CMDLINE="${KERNEL_CMDLINE}"
EOF

		changed=true
        fi

        if ${@base_conditional("DISTRO_TYPE", "release", "true", "false",d); then
		sed -i -e '/^CONFIG_DANGEROUS/d' .config
		cat <<EOF >> .config
# CONFIG_DANGEROUS is not set
EOF

		changed=true
        fi

	! $changed || oe_runmake -f "${_gen_mf}" oldconfig < /dev/null
}

do_uboot_mkimage() {
	set -x
        if ${@base_contains('MACHINE_FEATURES','arnoldboot','true','false',d)}; then
                for i in sdram gdb flash; do
                        oe_runmake -f "${_gen_mf}" arnoldboot-$i _flash_filename=${KERNEL_OUTPUT}-$i
                done
        elif test -w "${_tftp_image}"; then
                cat "arch/${ARCH}/boot/${KERNEL_IMAGETYPE}" > "${_tftp_image}"
        elif ! test -w "${_tftp_image}"; then
		oewarn "File '${_tftp_image}' not writable; skipping staging of kernel into TFTP root"
	fi
}

do_deploy_append() {
        if ${@base_contains('MACHINE_FEATURES','arnoldboot','true','false',d)}; then
                for i in sdram gdb flash; do
                        cd "${S}"
                        install -m 0644 ${KERNEL_OUTPUT}-$i \
				${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGE_BASE_NAME}-$i.bin
                        package_stagefile_shell ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGE_BASE_NAME}-$i.bin

			cd "${DEPLOY_DIR_IMAGE}"
			rm -f ${KERNEL_IMAGE_SYMLINK_NAME}-$i.bin
			ln -sf ${KERNEL_IMAGE_BASE_NAME}-$i.bin ${KERNEL_IMAGE_SYMLINK_NAME}-$i.bin
			package_stagefile_shell ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGE_SYMLINK_NAME}-$i.bin
		done
	fi
}

do_generate_makefile[dirs] = "${ELITO_GIT_WS}"
addtask generate_makefile before do_configure after do_unpack
