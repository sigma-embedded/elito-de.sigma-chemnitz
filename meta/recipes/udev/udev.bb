DESCRIPTION = "udev is a daemon which dynamically creates and removes device nodes from \
/dev/, handles hotplug events and loads drivers at boot time. It replaces \
the hotplug package and requires a kernel not older than 2.6.12."
RPROVIDES_${PN} = "hotplug"
DEPENDS = "acl virtual/libusb0 usbutils glib-2.0 gperf-native"
LICENSE = "GPLv2+ & LGPLv2.1+"
LICENSE_${PN} = "GPLv2+"
LICENSE_libudev = "LGPLv2.1+"
LICENSE_libgudev = "LGPLv2.1+"

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://libudev/COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://extras/gudev/COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343"

PV	= "173"
PR	= "r0"

SRC_URI = "	\
	http://kernel.org/pub/linux/utils/kernel/hotplug/udev-${PV}.tar.bz2 \
	file://udev-145-settlerace.patch	\
	file://udev-164-stop.patch		\
	file://udev-158-target-input.patch	\
	file://nov4l.patch			\
	file://60-ubi.rules			\
"

SRC_URI[md5sum] = "91a88a359b60bbd074b024883cc0dbde"
SRC_URI[sha256sum] = "70a18315a12f8fc1131f7da5b4dae3606988b69d5c08f96f443b84b8486caaaf"

_sbindir    = "/sbin"
_libexecdir = "${base_libdir}/udev"
rules_dir   = "${_libexecdir}/rules.d"

EXTRA_OECONF = "\
	--enable-static \
	--disable-introspection \
	--with-pci-ids-path=/usr/share/misc \
	--with-rootlibdir=${base_libdir} \
	--libexecdir=${_libexecdir} \
	--sbindir=${_sbindir} \
	--with-systemdsystemunitdir=${base_libdir}/systemd/system/ \
	ac_cv_file__usr_share_pci_ids=no \
	ac_cv_file__usr_share_hwdata_pci_ids=no \
	ac_cv_file__usr_share_misc_pci_ids=yes \
"

inherit autotools pkgconfig

PACKAGES =+ "${PN}-rules-extra"
PACKAGES =+ "${PN}-rules-base ${PN}-rules-modules ${PN}-rules-persistent ${PN}-rules-ubi \
             ${PN}-mtd ${PN}-firmware ${PN}-keyboard ${PN}-sound ${PN}-keymaps"
PACKAGES += "${PN}-consolekit ${PN}-systemd libudev libgudev ${PN}-fstab-import \
             ${PN}-rulegen ${PN}-extra ${PN}-pci-db"

PACKAGES_DYNAMIC += "udev-.*-id"

python populate_packages_prepend() {
	import bb
	pn      = bb.data.getVar('PN', d, 1)
	libdir  = bb.data.getVar('_libexecdir', d, 1)
	pkgs    = bb.data.getVar('PACKAGES', d, 1).split()

	id_progs=("ata", "cdrom", "edd", "path", "scsi", "usb", "input")
	pkgs.extend(map(lambda x: '%s-%s-id' % (pn,x), id_progs))

	bb.data.setVar('PACKAGES', ' '.join(pkgs), d);

	for i in id_progs:
		key = 'FILES_%s-%s-id' % (pn,i)
		val = bb.data.getVar(key, d, 1)
		bb.data.setVar(key, '%s %s/%s_id' % (val,libdir,i), d)
}

do_install_append() {
	install -p -m 0644 ${WORKDIR}/60-ubi.rules      ${D}${rules_dir}/

        rm -f ${D}${libdir}/*.la
	touch ${D}${rules_dir}/.empty

	# disable udev-cache sysv script on systemd installs
	ln -sf /dev/null ${D}/${base_libdir}/systemd/system/udev-cache.service
}

FILES_${PN} = "\
	${_libexecdir}/devices		\
	${sysconfdir}/udev/udev.conf	\
	${sysconfdir}/udev/rules.d	\
	${_sbindir}/udevd		\
	${_sbindir}/udevadm		\
	${rules_dir}/.empty		\
"
RRECOMMENDS_${PN} = "${PN}-rules-base"
RPROVIDES_${PN}  += "udev-utils"

FILES_${PN}-keyboard = "\
	${rules_dir}/*keyboard*.rules	\
	${rules_dir}/*keymap*.rules	\
	${_libexecdir}/findkeyboards \
	${_libexecdir}/keyboard-force-release.sh \
"
RRECOMMENDS_${PN}-keyboard += "${PN}-usb-id"

FILES_${PN}-keymaps = "\
	${_libexecdir}/keymap \
	${_libexecdir}/keymaps"

FILES_${PN}-extra = "\
	${_libexecdir}/create_floppy_devices	\
	${_libexecdir}/collect			\
	${_libexecdir}/accelerometer		\
"

FILES_${PN}-scsi-id += "/etc/scsi_id.config"

FILES_${PN}-fstab-import = "\
	${_libexecdir}/fstab_import			\
	${rules_dir}/*fstab_import.rules	\
"

FILES_${PN}-usb-id   += "${_libexecdir}/usb-db"
FILES_${PN}-pci-db    = "${_libexecdir}/pci-db"

FILES_libudev = "${base_libdir}/libudev.so.*"
FILES_libgudev = "${base_libdir}/libgudev-*.so.*"

FILES_${PN}-firmware  = " \
	${_libexecdir}/firmware \
	${rules_dir}/*firmware.rules"

FILES_${PN}-rulegen   = "	\
	${_libexecdir}/write_*_rules		\
	${_libexecdir}/rule_generator.functions	\
"

FILES_${PN}-rules-base = "	\
	${rules_dir}/*udev-default.rules	\
	${rules_dir}/*udev-late.rules		\
	${rules_dir}/*description.rules		\
"
RRECOMMENDS_${PN}-rules-base += "${PN}-usb-id"

FILES_${PN}-sound = "${rules_dir}/*sound*.rules"
RRECOMMENDS_${PN}-sound += "${PN}-usb-id ${PN}-pci-db"

FILES_${PN}-mtd = " \
	${_libexecdir}/mtd_probe	\
	${rules_dir}/*mtd.rules		\
"

FILES_${PN}-rules-persistent = " \
	${rules_dir}/*-persistent*.rules \
	${rules_dir}/*-cd-aliases-generator.rules \
"
RRECOMMENDS_${PN}-rules-persistent = "${PN}-path-id ${PN}-usb-id"

FILES_${PN}-rules-modules    = "${rules_dir}/*-drivers.rules"
RDEPENDS_${PN}-rules-modules = "module-init-tools"

FILES_${PN}-rules-ubi        = "${rules_dir}/*-ubi.rules"

FILES_${PN}-rules-extra      = "${rules_dir}/*.rules"
RRECOMMENDS_${PN}-rules-extra = "${PN}-usb-id ${PN}-pci-db ${PN}-path-id"

FILES_${PN}-dev             += "/usr/lib/*.so /usr/lib/pkgconfig/*.pc"
FILES_${PN}-consolekit       = "/usr/lib/ConsoleKit"

FILES_${PN}-dbg             += "/lib/udev/.debug"

FILES_${PN}-systemd = "${base_libdir}/systemd"
