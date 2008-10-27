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
	gc=`type -p ${CROSS_COMPILE}gcc`
	gc=${gc%%gcc}
	dn=`dirname "$gc"`
	gc=`basename "$gc"`
	ccache=`type -p ccache`
	cat << EOF > x
%:
	env PATH=\$\$PATH:$dn CCACHE_DIR=${CCACHE_DIR} \$(MAKE) -e CC='ccache ${CROSS_COMPILE}gcc' LD=${CROSS_COMPILE}ld $@
EOF

	sed -i	\
		-e 's!^\(CROSS_COMPILE[[:space:]]*?\?=[[:space:]]*\).*!\1'"$gc!" 	\
		-e 's!^x\(\(HOST\)\?CC[[:space:]]*?\?=[[:space:]]*\)\([^/]\)!\1'"$ccache \3!"	\
		-e '/^CROSS_COMPILE/a\'							\
		-e 'export CCACHE_DIR = ${CCACHE_DIR}\'					\
		-e 'PATH := $(PATH):'"$dn\\"						\
		-e 'export PATH'							\
		Makefile
	git commit -m 'LOCAL: buildsystem customizations' Makefile

	if ! test -e .config; then
		oe_runmake "${KERNEL_DEFCONFIG}"
	fi
}	

inherit kernel
