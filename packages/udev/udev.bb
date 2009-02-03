DESCRIPTION = "udev is a daemon which dynamically creates and removes device nodes from \
/dev/, handles hotplug events and loads drivers at boot time. It replaces \
the hotplug package and requires a kernel not older than 2.6.12."
RPROVIDES_${PN} = "hotplug"
LICENSE = "GPLv2"

PV	= "137"
PR 	= "r0"

sbindir = "/sbin"
libdir  = "/lib"
exec_prefix = ""

rules_dir = "${libdir}/udev/rules.d"

inherit autotools pkgconfig

PACKAGES =+ "${PN}-rules-extra"
PACKAGES =+ "${PN}-rules-base ${PN}-rules-modules ${PN}-rules-alsa ${PN}-rules-ubi"
PACKAGES += "${PN}-lib ${PN}-libvolume ${PN}-fstab-import \
	     ${PN}-firmware ${PN}-rulegen ${PN}-extra"

python __anonymous() {
	import bb
	pn      = bb.data.getVar('PN', d, 1)
	libdir  = bb.data.getVar('libdir', d, 1)

	id_progs=("ata", "cdrom", "edd", "path", "scsi", "usb", "vol")
	id_pkgs = map(lambda x: '%s-%s-id' % (pn,x), id_progs)

	bb.data.setVar('PACKAGES',
			bb.data.getVar('PACKAGES', d, 1) + ' ' + ' '.join(id_pkgs),
			d)

	for i in id_progs:
		key = 'FILES_%s-%s-id' % (pn,i)
		val = bb.data.getVar(key, d, 1)
		bb.data.setVar(key, '%s %s/udev/%s_id' % (val,libdir,i), d)
}

do_install_append() {
	install -p -m 0644 rules/packages/40-alsa.rules ${D}${rules_dir}/
	install -p -m 0644 ${WORKDIR}/60-ubi.rules      ${D}${rules_dir}/
	touch ${D}${rules_dir}/.empty
}

do_stage() {
	set -x
	autotools_stage_all
	oe_libinstall -C extras/volume_id/lib -so libvolume_id ${STAGING_LIBDIR}
}

FILES_${PN} = "\
	${sysconfdir}/udev/udev.conf	\
	${sysconfdir}/udev/rules.d	\
	${sbindir}/udevd		\
	${sbindir}/udevadm		\
	${rules_dir}/.empty		\
"
RRECOMMENDS_${PN} = "${PN}-rules-base"
RPROVIDES_${PN}  += "udev-utils"

FILES_${PN}-extra = "\
	${libdir}/udev/create_floppy_devices	\
	${libdir}/udev/collect			\
"

FILES_${PN}-scsi-id += "/etc/scsi_id.config"

FILES_${PN}-fstab-import = "\
	${libdir}/udev/fstab_import			\
	${rules_dir}/*fstab_import.rules	\
"

FILES_${PN}-lib       = "${libdir}/libudev.so.*"
FILES_${PN}-libvolume = "${libdir}/libvolume_id.so.*"
FILES_${PN}-firmware  = "${libdir}/udev/firmware.sh"
FILES_${PN}-rulegen   = "	\
	${libdir}/udev/write_*_rules		\
	${libdir}/udev/rule_generator.functions	\
"

FILES_${PN}-rules-base = "	\
	${rules_dir}/*udev-default.rules	\
	${rules_dir}/*udev-late.rules		\
"

FILES_${PN}-rules-alsa       = "${rules_dir}/*alsa.rules"
RPROVIDES_${PN}-rules-alsa   = "virtual/snd-dev"

FILES_${PN}-rules-modules    = "${rules_dir}/*-drivers.rules"
RDEPENDS_${PN}-rules-modules = "module-init-tools"

FILES_${PN}-rules-ubi        = "${rules_dir}/*-ubi.rules"

FILES_${PN}-rules-extra      = "${rules_dir}/*.rules"
FILES_${PN}-dev		    += "/usr/lib/*.so /usr/lib/pkgconfig/*.pc"


SRC_URI = "	\
	http://kernel.org/pub/linux/utils/kernel/hotplug/udev-${PV}.tar.gz \
	file://udev-130-settlerace.patch;patch=1	\
	file://udev-137-stop.patch;patch=1		\
	file://60-ubi.rules			\
"
