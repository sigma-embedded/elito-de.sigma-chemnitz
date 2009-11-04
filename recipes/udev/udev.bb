DESCRIPTION = "udev is a daemon which dynamically creates and removes device nodes from \
/dev/, handles hotplug events and loads drivers at boot time. It replaces \
the hotplug package and requires a kernel not older than 2.6.12."
RPROVIDES_${PN} = "hotplug"
LICENSE = "GPLv2"
DEPENDS = "acl virtual/libusb0 usbutils glib-2.0 gperf-native"

PV	= "146"
PR	= "r2"

SRC_URI = "	\
	http://kernel.org/pub/linux/utils/kernel/hotplug/udev-${PV}.tar.gz \
	file://udev-145-settlerace.patch;patch=1	\
	file://udev-145-stop.patch;patch=1		\
	file://udev-145-cross.patch;patch=1		\
	file://60-ubi.rules			\
"

sbindir    = "/sbin"
rootlibdir = "/lib"
libexecdir = "${rootlibdir}/udev"
rules_dir  = "${libexecdir}/rules.d"

EXTRA_OECONF = "\
	--enable-static \
	--with-pci-ids-path=/usr/share/pci.ids \
	--with-rootlibdir=${rootlibdir}"

inherit autotools_stage pkgconfig

PACKAGES =+ "${PN}-rules-extra"
PACKAGES =+ "${PN}-rules-base ${PN}-rules-modules ${PN}-rules-alsa ${PN}-rules-ubi \
             ${PN}-firmware ${PN}-keymaps"
PACKAGES += "${PN}-lib ${PN}-libgudev ${PN}-fstab-import \
             ${PN}-rulegen ${PN}-extra"

PACKAGES_DYNAMIC += "$PN-*-id"

python populate_packages_prepend() {
	import bb
	pn      = bb.data.getVar('PN', d, 1)
	libdir  = bb.data.getVar('libexecdir', d, 1)
	pkgs    = bb.data.getVar('PACKAGES', d, 1).split()

	id_progs=("ata", "cdrom", "edd", "path", "scsi", "usb", "v4l")
	pkgs.extend(map(lambda x: '%s-%s-id' % (pn,x), id_progs))

	bb.data.setVar('PACKAGES', ' '.join(pkgs), d);

	for i in id_progs:
		key = 'FILES_%s-%s-id' % (pn,i)
		val = bb.data.getVar(key, d, 1)
		bb.data.setVar(key, '%s %s/%s_id' % (val,libdir,i), d)
}

do_install_append() {
	install -p -m 0644 rules/packages/40-alsa.rules ${D}${rules_dir}/
	install -p -m 0644 ${WORKDIR}/60-ubi.rules      ${D}${rules_dir}/

	touch ${D}${rules_dir}/.empty
}

do_stage_append() {
        rm ${STAGING_LIBDIR}/libudev.la ${STAGING_LIBDIR}/libgudev-1.0.la
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

FILES_${PN}-keymaps = "\
	${libexecdir}/keymap \
	${libexecdir}/keymaps"

FILES_${PN}-extra = "\
	${libexecdir}/create_floppy_devices	\
	${libexecdir}/collect			\
"

FILES_${PN}-scsi-id += "/etc/scsi_id.config"

FILES_${PN}-fstab-import = "\
	${libexecdir}/fstab_import			\
	${rules_dir}/*fstab_import.rules	\
"

FILES_${PN}-usb-id   += "${libexecdir}/usb-db"

FILES_${PN}-lib       = "${rootlibdir}/libudev.so.*"
FILES_${PN}-libgudev  = "${libdir}/libgudev-*.so.*"

FILES_${PN}-firmware  = " \
	${libexecdir}/firmware.sh \
	${rules_dir}/*firmware.rules"

FILES_${PN}-rulegen   = "	\
	${libexecdir}/write_*_rules		\
	${libexecdir}/rule_generator.functions	\
"

FILES_${PN}-rules-base = "	\
	${rules_dir}/*udev-default.rules	\
	${rules_dir}/*udev-late.rules		\
"

FILES_${PN}-rules-alsa       = "${rules_dir}/*alsa.rules"

FILES_${PN}-rules-modules    = "${rules_dir}/*-drivers.rules"
RDEPENDS_${PN}-rules-modules = "module-init-tools"

FILES_${PN}-rules-ubi        = "${rules_dir}/*-ubi.rules"

FILES_${PN}-rules-extra      = "${rules_dir}/*.rules"
FILES_${PN}-dev             += "/usr/lib/*.so /usr/lib/pkgconfig/*.pc"
