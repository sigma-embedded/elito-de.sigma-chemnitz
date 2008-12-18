KERNEL_REPO ?= "${ELITO_GIT_MIRROR}/kernel.git"
DEPENDS     += '${@base_conditional("KERNEL_IMAGETYPE","uImage","u-boot-utils-native","",d)}'

PV	=  "${MACHINE_KERNEL_VERSION}"
#PV_PKG	= "${KERNEL_RELEASE}"
PR_PKG	= "${PR}${@base_conditional('METADATA_REVISION','','','+${METADATA_REVISION}',d)}"

S	 = "${WORKDIR}/linux-${PV}"
SRC_URI ?= "file://git.repo"

GIT_REPO   = "${KERNEL_REPO}"
GIT_BRANCH = "${_branch}"

inherit gitdevel

do_configure_prepend() {
	gc=`type -p ${CROSS_COMPILE}gcc`
	gc=${gc%%gcc}
	dn=`dirname "$gc"`
	gc=`basename "$gc"`
	ccache=`type -p ccache`

	cat << EOF > mf
_cc = ${KERNEL_CC}
_ld = ${KERNEL_LD}

export PATH = ${PATH}
export CCACHE_DIR = ${CCACHE_DIR}
export CC = \$(_cc)
export LD = \$(_ld)
export CROSS_COMPILE = ${CROSS_COMPILE}
export INSTALL_MOD_PATH = ${IMAGE_ROOTFS}

_flash_filename = ${_tftp_image}
_bad_env = CFLAGS CPPFLAGS CXXFLAGS LDFLAGS MACHINE

%:
	env \$(addprefix -u ,\$(_bad_env)) PATH=\$(PATH) \$(MAKE) CC='\$(_cc)' LD='\$(_ld)' ${@base_conditional("_tftp_image","","","FLASH_FILENAME='\$(_flash_filename)'",d)} MAKELEVEL=0 \$@

unexport MAKEFILES
unexport MAKELEVEL
.DEFAULT_GOAL := all
EOF

	if ! test -e .config; then
		oe_runmake -f mf "${_defconfig}"
	fi
}

do_stage_prepend() {
	test -z "${_tftp_image}" || \
		cat "${_tftp_image}" > "${_tftp_image}"
}

