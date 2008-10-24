DESCRIPTION	 = "ELiTo Linux kernel"
SECTION		 = "kernel"
LICENSE		 = "GPL"
PR		 = "r0"

DEPENDS		+= '${@base_conditional("KERNEL_IMAGETYPE","uImage","u-boot-utils-native","",d)}'
KERNEL_REPO	?= "${ELITO_GIT_BASE}/kernel.git"

S		 = "${WORKDIR}/linux-elito"
SRC_URI		 = "file://git.repo"

do_fetch() {
	set -x

	if ! test -d ${S}/.git; then
		git clone -nqls -o origin-elito "${KERNEL_REPO}" "${S}"
		cd ${S}
		git checkout -q --track -b master "remotes/origin-elito/${MACHINE_KERNEL_VERSION}/${KERNEL_BRANCH}"
	else
		cd ${S}
		git fetch origin-elito
		git pull
	fi
}

do_configure_prepend() {
	if ! test -e .config; then
		oe_runmake "${KERNEL_DEFCONFIG}"
	fi
}	

inherit kernel
