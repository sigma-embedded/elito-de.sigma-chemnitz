PV		= "0.2+gitr${SRCPV}"
PR		= "r4"
LICENSE		= "unknown"
PACKAGE_ARCH	= "all"

SRCREV          = "bce93b43e6f143a0d27244a788fc4757aa201ea7"

SRC_URI = "\
	git://git.kernel.org/pub/scm/linux/kernel/git/dwmw2/linux-firmware.git;protocol=git \
	http://www.otit.fi/~crope/v4l-dvb/af9015/af9015_firmware_cutter/firmware_files/4.95.0/dvb-usb-af9015.fw;name=af9015 \
	http://people.freedesktop.org/~pq/nouveau-drm/nouveau-firmware-20091212.tar.gz;name=nvfw \
	http://ilpss8.dyndns.org/dvb-usb-tt-s2400-01.fw;name=tts2400 \
"

SRC_URI[af9015.md5sum] = "dccbc92c9168cc629a88b34ee67ede7b"
SRC_URI[af9015.sha256sum] = "a0ae064c3acef212172f13317d627492d0ed3c0a43f3634821b33a00fbf99621"

SRC_URI[nvfw.md5sum] = "518ce9f432498969c88f63579032da74"
SRC_URI[nvfw.sha256sum] = "4eaeb2044c1da95b50ea4f1facb0effc74d65d8b13dc10893c26e4fe9fe23fb8"

SRC_URI[tts2400.md5sum] = "6774e32bbf8b3d6f86e98e699cc56b97"
SRC_URI[tts2400.sha256sum] = "6290cdcb76adff393e2ade506a611c1f95e4a5c619248d464792bda5a6796794"


INHIBIT_DEFAULT_DEPS = "1"

do_install() {
    cd ../git

    install -d -m 0755 ${D}/lib/firmware ${D}/lib/firmware/nouveau

    install -p -m 0644 libertas/*.bin ath3k*.fw ${WORKDIR}/*.fw  ${D}/lib/firmware/
    install -p -m 0644 ../nouveau/*.ctxprog ../nouveau/*.ctxvals ${D}/lib/firmware/nouveau/
}

_pkginfo = "{'libertas-sd8686-v9' : [ 'sd8686_v9*' ], \
             'libertas-sd8686-v8' : [ 'sd8686_v8*' ], \
             'dvb-usb-af9015'     : [ 'dvb-usb-af9015.fw' ], \
             'dvb-usb-tt-s2400'   : [ 'dvb-usb-tt-s2400-01.fw' ], \
             'nouveau'            : [ 'nouveau/' ], \
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
