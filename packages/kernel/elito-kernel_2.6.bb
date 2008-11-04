DESCRIPTION	 = "ELiTo Linux kernel"
SECTION		 = "kernel"
LICENSE		 = "GPL"
PR		 = "r1"

DEPENDS		+= '${@base_conditional("KERNEL_IMAGETYPE","uImage","u-boot-utils-native","",d)}'
KERNEL_REPO	?= "${ELITO_GIT_MIRROR}/kernel.git"

S		 = "${WORKDIR}/linux-elito"
SRC_URI		 = "file://git.repo"

_branch          = "${MACHINE_KERNEL_VERSION}/${KERNEL_BRANCH}"

do_fetch() {
	set -x

	if ! test -d ${S}/.git; then
		git clone -nqls -o origin-elito "${KERNEL_REPO}" "${S}"
		cd ${S}
		git branch --track "${_branch}" "remotes/origin-elito/${_branch}"
		git reset -q --hard "${_branch}"
		git checkout "${_branch}"
		git branch -D master || :
	else
		cd ${S}
		git fetch origin-elito
		git pull
	fi
}

do_configure_prepend() {
	gc=`type -p ${CROSS_COMPILE}gcc`
	gc=${gc%%gcc}
	dn=`dirname "$gc"`
	gc=`basename "$gc"`
	ccache=`type -p ccache`

	cat << EOF > mf
%:
	env PATH=\$\$PATH:$dn CCACHE_DIR=${CCACHE_DIR} \$(MAKE) CC='ccache ${CROSS_COMPILE}gcc' LD=${CROSS_COMPILE}ld CROSS_COMPILE=${CROSS_COMPILE} MAKELEVEL=0 \$@

unexport MAKEFILES
unexport MAKELEVEL
.DEFAULT_GOAL := all
EOF

	if ! test -e .config; then
		oe_runmake "${KERNEL_DEFCONFIG}"
	fi
}

inherit kernel
