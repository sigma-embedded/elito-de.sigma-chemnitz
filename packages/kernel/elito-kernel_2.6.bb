DESCRIPTION	 = "ELiTo Linux kernel"
SECTION		 = "kernel"
LICENSE		 = "GPL"
PR		 = "r1"

DEPENDS		+= '${@base_conditional("KERNEL_IMAGETYPE","uImage","u-boot-utils-native","",d)}'
KERNEL_REPO	?= "${ELITO_GIT_MIRROR}/kernel.git"
KERNEL_TFTP_IMAGE ?= ""

S		 = "${WORKDIR}/linux-elito"
SRC_URI		 = "file://git.repo"

_branch          = "${MACHINE_KERNEL_VERSION}/${KERNEL_BRANCH}"

do_fetch() {
	set -x
	install -d ${S}
	cd ${S}

	if ! test -d ${S}/.git; then
		git init
		git remote add origin-elito "${KERNEL_REPO}"
		git config remote.origin-elito.fetch 'refs/heads/${_branch}:refs/remotes/origin-elito/${_branch}'
		git config remote.origin-elito.tagopt --no-tags

		git fetch origin-elito
		git checkout --track -b "${_branch}" "remotes/origin-elito/${_branch}"
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

do_stage_prepend() {
	test -z "${KERNEL_TFTP_IMAGE}" || \
		cat "${KERNEL_OUTPUT}" > "${KERNEL_TFTP_IMAGE}"
}

inherit kernel
