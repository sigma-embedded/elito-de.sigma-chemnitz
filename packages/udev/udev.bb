DESCRIPTION = "udev is a daemon which dynamically creates and removes device nodes from \
/dev/, handles hotplug events and loads drivers at boot time. It replaces \
the hotplug package and requires a kernel not older than 2.6.12."
RPROVIDES_${PN} = "hotplug"

PV	= "130"
PR 	= "r1"

pd = "file:///srv/elito/toolchain/devel/elito-udev/"

sbindir = "/sbin"
libdir  = "/lib"
exec_prefix = ""

inherit autotools pkgconfig

PACKAGES =+ "${PN}-rules-base ${PN}-rules-modules ${PN}-rules-extra"
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
	touch ${D}${libdir}/udev/rules.d/.empty
}

FILES_${PN} = "\
	${sysconfdir}/udev/udev.conf	\
	${sysconfdir}/udev/rules.d	\
	${sbindir}/udevd		\
	${sbindir}/udevadm		\
	${libdir}/udev/rules.d/.empty	\
"
RRECOMMENDS_${PN} = "${PN}-rules-base"

FILES_${PN}-extra = "\
	${libdir}/udev/create_floppy_devices	\
	${libdir}/udev/collect			\
"

FILES_${PN}-scsi-id += "/etc/scsi_id.config"

FILES_${PN}-fstab-import = "\
	${libdir}/udev/fstab_import			\
	${libdir}/udev/rules.d/*fstab_import.rules	\
"

FILES_${PN}-lib       = "${libdir}/libudev.so.*"
FILES_${PN}-libvolume = "${libdir}/libvolume_id.so.*"
FILES_${PN}-firmware  = "${libdir}/udev/firmware.sh"
FILES_${PN}-rulegen   = "	\
	${libdir}/udev/write_*_rules		\
	${libdir}/udev/rule_generator.functions	\
"

FILES_${PN}-rules-base = "	\
	${libdir}/udev/rules.d/*udev-default.rules	\
	${libdir}/udev/rules.d/*udev-late.rules		\
"

FILES_${PN}-rules-modules    = "${libdir}/udev/rules.d/*-drivers.rules"
RDEPENDS_${PN}-rules-modules = "module-init-tools"

FILES_${PN}-rules-extra      = "${libdir}/udev/rules.d/*.rules"
FILES_${PN}-dev		    += "/usr/lib/*.so /usr/lib/pkgconfig/*.pc"


SRC_URI = "	\
	http://kernel.org/pub/linux/utils/kernel/hotplug/udev-${PV}.tar.gz \
	${pd}udev-130-settlerace.patch;patch=1	\
	${pd}udev-130-stop.patch;patch=1	\
"
