PV = "${MACHINE_KERNEL_VERSION}+gitr${SRCPV}"

GIT_REPO = "${KERNEL_REPO}"
SRCREV   = "${AUTOREV}"

KERNEL_REPO ?= "${ELITO_GIT_WS}/kernel.git"
_branch      = "${MACHINE_KERNEL_VERSION}/${KERNEL_BRANCH}"

SRC_URI      = "git://${KERNEL_REPO};protocol=file;branch=${_branch}"
S            = "${WORKDIR}/git"

KERNEL_DEPENDS ?= ""

DEPENDS += '\
	${KERNEL_DEPENDS} \
	${@base_conditional("KERNEL_IMAGETYPE","uImage","u-boot-utils-native","",d)}'

_genmf   = "${ELITO_GIT_WS}/Makefile.kernel.${PROJECT_NAME}"
do_configure_prepend() {
	gc=`${WHICH} ${CROSS_COMPILE}gcc`
	gc=${gc%%gcc}
	dn=`dirname "$gc"`
	gc=`basename "$gc"`
	ccache=`${WHICH} ccache`

	rm -f "${_genmf}"
	cat << EOF | sed -e 's![[:space:]]*$!!' > "${_genmf}"
## --*- makefile -*--
_cc = ${KERNEL_CC}
_ld = ${KERNEL_LD}

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
	env \$(addprefix -u ,\$(_bad_env)) PATH=\$(PATH) \$(MAKE) CC='\$(_cc)' LD='\$(_ld)' ${@base_conditional("_tftp_image","","","FLASH_FILENAME='\$(_flash_filename)'",d)} MAKELEVEL=0 \$@

exec:
	\$(P)

unexport MAKEFILES
unexport MAKELEVEL
.DEFAULT_GOAL := all
EOF

	if ! test -e .config; then
		oe_runmake -f "${_genmf}" "${_defconfig}"
	fi
}

do_stage_prepend() {
	set -x
	if test -n "${_tftp_image}"; then
		if ! test -w "${_tftp_image}"; then
			oewarn "File '${_tftp_image}' not writable; skipping staging of kernel into TFTP root"
		else
			cat "arch/${ARCH}/boot/${KERNEL_IMAGETYPE}" > "${_tftp_image}"
		fi
	fi
}
