_pv = "0.2"
PR = "r11"

PV = "${_pv}+gitr${SRCPV}"
PKGV = "${_pv}+gitr${GITPKGV}"

LICENSE		= "GPLv3"
LICENSE		= "unknown"
PACKAGE_ARCH	= "all"
LIC_FILES_CHKSUM = "\
  file://GPL-3;md5=f27defe1e96c2e1ecd4e0c9be8967949 \
  file://LICENCE.Marvell;md5=9ddea1734a4baf3c78d845151f42a37a \
  file://LICENCE.agere;md5=af0133de6b4a9b2522defd5f188afd31 \
  file://LICENCE.atheros_firmware;md5=30a14c7823beedac9fa39c64fdd01a13 \
  file://LICENCE.broadcom_bcm43xx;md5=3160c14df7228891b868060e1951dfbc \
  file://LICENCE.i2400m;md5=14b901969e23c41881327c0d9e4b7d36 \
  file://LICENCE.iwlwifi_firmware;md5=311cc823df5b1be4f00fbf0f17d96a6b \
  file://LICENCE.mwl8335;md5=9a6271ee0e644404b2ff3c61fd070983 \
  file://LICENCE.phanfw;md5=5b9c38c029d2825d2a3a32c4f482794b \
  file://LICENCE.qla2xxx;md5=4005328a134054f0fa077bdc37aa64f2 \
  file://LICENCE.ralink-firmware.txt;md5=ab2c269277c45476fb449673911a2dfd \
  file://LICENCE.rtlwifi_firmware.txt;md5=00d06cfd3eddd5a2698948ead2ad54a5 \
  file://LICENCE.ti-connectivity;md5=c4073948801d7b82b41ddffd6c7288d0 \
  file://LICENCE.ueagle-atm4-firmware;md5=4ed7ea6b507ccc583b9d594417714118 \
  file://LICENCE.via_vt6656;md5=e4159694cba42d4377a912e78a6e850f \
  file://LICENCE.xc5000;md5=1e170c13175323c32c7f4d0998d53f66 \
"

SRCREV          = "a2995d38c234e3d767df7223d84a0f33a1a24997"

SRC_URI = "\
	git://git.kernel.org/pub/scm/linux/kernel/git/firmware/linux-firmware.git;protocol=git \
	http://www.otit.fi/~crope/v4l-dvb/af9015/af9015_firmware_cutter/firmware_files/4.95.0/dvb-usb-af9015.fw;name=af9015 \
	http://people.freedesktop.org/~pq/nouveau-drm/nouveau-firmware-20091212.tar.gz;name=nvfw \
	http://ilpss8.dyndns.org/dvb-usb-tt-s2400-01.fw;name=tts2400 \
"

inherit gitpkgv

SRC_URI[af9015.md5sum] = "dccbc92c9168cc629a88b34ee67ede7b"
SRC_URI[af9015.sha256sum] = "a0ae064c3acef212172f13317d627492d0ed3c0a43f3634821b33a00fbf99621"

SRC_URI[nvfw.md5sum] = "518ce9f432498969c88f63579032da74"
SRC_URI[nvfw.sha256sum] = "4eaeb2044c1da95b50ea4f1facb0effc74d65d8b13dc10893c26e4fe9fe23fb8"

SRC_URI[tts2400.md5sum] = "6774e32bbf8b3d6f86e98e699cc56b97"
SRC_URI[tts2400.sha256sum] = "6290cdcb76adff393e2ade506a611c1f95e4a5c619248d464792bda5a6796794"


S = "${WORKDIR}/git"
INHIBIT_DEFAULT_DEPS = "1"

do_install() {
    cd ../git

    install -d -m 0755 ${D}/lib/firmware ${D}/lib/firmware/nouveau \
        ${D}/lib/firmware/libertas ${D}/lib/firmware/rtlwifi

    install -p -m 0644 ath3k*.fw ${WORKDIR}/*.fw  ${D}/lib/firmware/
    install -p -m 0644 rt*.bin ${D}/lib/firmware/
    install -p -m 0644 libertas/*.bin ${D}/lib/firmware/libertas/
    install -p -m 0644 rtlwifi/*.bin ${D}/lib/firmware/rtlwifi/
    install -p -m 0644 ../nouveau/*.ctxprog ../nouveau/*.ctxvals ${D}/lib/firmware/nouveau/
}

_pkginfo = "{ \
        'libertas-lbtf-sdio' : [ 'libertas/lbtf_sdio*' ], \
        'libertas-cf8381'    : [ 'libertas/cf8381*' ], \
        'libertas-cf8385'    : [ 'libertas/cf8385*' ], \
        'libertas-gspi8682'  : [ 'libertas/gspi8682*' ], \
        'libertas-gspi8686-v9' : [ 'libertas/gspi8686_v9*' ], \
        'libertas-gspi8688'  : [ 'libertas/gspi8688*' ], \
        'libertas-sd8385'    : [ 'libertas/sd8385*' ], \
        'libertas-sd8682'    : [ 'libertas/sd8682*' ], \
        'libertas-sd8686-v8' : [ 'libertas/sd8686_v8*' ], \
        'libertas-sd8686-v9' : [ 'libertas/sd8686_v9*' ], \
        'libertas-sd8688'    : [ 'libertas/sd8688*' ], \
        'libertas-usb8388-olpc' : [ 'libertas/usb8388_olpc*' ], \
        'libertas-usb8388-v5' : [ 'libertas/usb8388_v5*' ], \
        'libertas-usb8388-v9' : [ 'libertas/usb8388_v9*' ], \
        'libertas-usb8682'   : [ 'libertas/usb8682*' ], \
        'dvb-usb-af9015'     : [ 'dvb-usb-af9015.fw' ], \
        'dvb-usb-tt-s2400'   : [ 'dvb-usb-tt-s2400-01.fw' ], \
        'nouveau'            : [ 'nouveau/' ], \
        'rt2561'             : [ 'rt2561.bin' ], \
        'rt2561s'            : [ 'rt2561s.bin' ], \
        'rt2661'             : [ 'rt2661.bin' ], \
        'rt2860'             : [ 'rt2860.bin' ], \
        'rt2870'             : [ 'rt2870.bin' ], \
        'rt3070'             : [ 'rt3070.bin' ], \
        'rt3071'             : [ 'rt3071.bin' ], \
        'rt3090'             : [ 'rt3090.bin' ], \
        'rt73'               : [ 'rt73.bin' ], \
        'rtl8192cfw'         : [ 'rtlwifi/rtl8192cfw*' ], \
        'rtl8192cufw'        : [ 'rtlwifi/rtl8192cufw*' ], \
        'rtl8192defw'        : [ 'rtlwifi/rtl8192defw*' ], \
        'rtl8192sefw'        : [ 'rtlwifi/rtl8192sefw*' ], \
        'rtl8712u'           : [ 'rtlwifi/rtl8712u*' ], \
        'ath3k'              : [ 'ath3k-1.fw' ]}"

PACKAGES_DYNAMIC += 'firmware-.*'

python populate_packages_prepend() {
    v = eval(bb.data.getVar('_pkginfo', d, 1))
    pkgs = ''
    for (n,f) in v.items():
        pname = 'firmware-%s' % n
        pkgs += ' ' + pname
        bb.data.setVar('FILES_' + pname, ' '.join(map(lambda x: '/lib/firmware/' + x, f)), d)

        print pname, f, bb.data.getVar('FILES_' + pname, d, 1)

    bb.data.setVar('PACKAGES', pkgs, d)
}

PV_pn_firmware-dvb-usb-af9015 = "4.95.0"
