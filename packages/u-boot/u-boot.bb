DESCRIPTION	 = "The U-Boot bootloader"
SECTION		 = "bootloaders"
PRIORITY	 = "optional"
LICENSE		 = "GPL"
PROVIDES	 = "virtual/bootloader"
PACKAGE_ARCH	 = "${MACHINE_ARCH}"
PV		 = "2008.10"
PR		 = "r2"

EXTRA_OEMAKE	 = "CROSS_COMPILE=${TARGET_PREFIX}"

UBOOT_MACHINE	?= "${MACHINE}_config"
UBOOT_SYMLINK	?= "u-boot-${MACHINE}.bin"
UBOOT_REPO	?= "${ELITO_GIT_MIRROR}/u-boot.git"

S		 = "${WORKDIR}/git"
PACKAGES	 = "${PN}-dbg ${PN}-bin ${PN}"

FILES_${PN}	 = "/boot/u-boot"
FILES_${PN}-dbg	 = "/boot/.debug"
FILES_${PN}-bin  = "/boot/u-boot.bin"

DEPENDS		+= "git-native"
DEFAULT_PREFERENCE = "99"

_branch = "${PV}/${UBOOT_BRANCH}"

do_fetch() {
	set -x
	install -d ${S}
	cd ${S}

	if ! test -d ${S}/.git; then
		git init
		git remote add origin-elito "${UBOOT_REPO}"
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

do_configure() {
	oe_runmake ${UBOOT_MACHINE}
	oe_runmake include/autoconf.mk
	oe_runmake clean
}

do_compile() {
	unset LDFLAGS
	set -x
	oe_runmake
}

do_install() {
	install -D -p -m 0644 u-boot.bin ${D}/boot/u-boot.bin
	install -D -p -m 0755 u-boot     ${D}/boot/u-boot
}

do_deploy () {
	set -x
	gitrev=`git ls-remote . HEAD | sed '1s/^\(........\).*/\1/p;d'`
	uboot_image="u-boot-${MACHINE}-${PV}-${PR}-${gitrev}.bin"

	install -d ${DEPLOY_DIR_IMAGE}
	install ${S}/u-boot.bin ${DEPLOY_DIR_IMAGE}/${uboot_image}
	package_stagefile_shell ${DEPLOY_DIR_IMAGE}/${uboot_image}

	cd ${DEPLOY_DIR_IMAGE}
	rm -f ${UBOOT_SYMLINK}
	ln -sf ${uboot_image} ${UBOOT_SYMLINK}
	package_stagefile_shell ${DEPLOY_DIR_IMAGE}/${UBOOT_SYMLINK}
}
do_deploy[dirs] = "${S}"
addtask deploy before do_build after do_compile
