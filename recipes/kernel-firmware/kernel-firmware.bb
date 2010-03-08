PV		= "0.2+gitr${SRCPV}"
PR		= "r2"
LICENSE		= "unknown"
PACKAGE_ARCH	= "all"

SRCREV          = "${AUTOREV}"

SRC_URI = "\
	git://git.kernel.org/pub/scm/linux/kernel/git/dwmw2/linux-firmware.git;protocol=git \
	http://www.otit.fi/~crope/v4l-dvb/af9015/af9015_firmware_cutter/firmware_files/4.95.0/dvb-usb-af9015.fw;name=af9015 \
"

SRC_URI[af9015.md5sum] = "dccbc92c9168cc629a88b34ee67ede7b"
SRC_URI[af9015.sha256sum] = "a0ae064c3acef212172f13317d627492d0ed3c0a43f3634821b33a00fbf99621"

INHIBIT_DEFAULT_DEPS = "1"

do_install() {
    cd ../git

    install -d ${D}/lib/firmware
    install -p -m 0644 libertas/*.bin ath3k*.fw ${WORKDIR}/*.fw ${D}/lib/firmware/
}

_pkginfo = "{'libertas-sd8686-v9' : [ 'sd8686_v9*' ], \
             'libertas-sd8686-v8' : [ 'sd8686_v8*' ], \
             'dvb-usb-af9015'     : [ 'dvb-usb-af9015.fw' ], \
             'ath3k'              : [ 'ath3k-1.fw' ]}"

PACKAGES_DYNAMIC += 'firmware-.*'

python populate_packages_prepend() {
	v = eval(bb.data.getVar('_pkginfo', d, 1))
	pkgs = ''
	for (n,f) in v.items():
		pname = 'firmware-%s' % n
		pkgs += ' ' + pname
		bb.data.setVar('FILES_' + pname, ' '.join(map(lambda x: '/lib/firmware/' + x, f)), d)
		bb.data.setVar('RRECOMMENDS_' + pname, '${FIRMWARE_LOADER_PROVIDER}', d)

		print pname, f, bb.data.getVar('FILES_' + pname, d, 1)

	bb.data.setVar('PACKAGES', pkgs, d)
}

PV_pn_firmware-dvb-usb-af9015 = "4.95.0"
