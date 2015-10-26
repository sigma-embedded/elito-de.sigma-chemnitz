_pv = "0.2.2"

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
  file://LICENCE.iwlwifi_firmware;md5=3fd842911ea93c29cd32679aa23e1c88 \
  file://LICENCE.mwl8335;md5=9a6271ee0e644404b2ff3c61fd070983 \
  file://LICENCE.phanfw;md5=954dcec0e051f9409812b561ea743bfa \
  file://LICENCE.qla2xxx;md5=f5ce8529ec5c17cb7f911d2721d90e91 \
  file://LICENCE.ralink-firmware.txt;md5=ab2c269277c45476fb449673911a2dfd \
  file://LICENCE.rtlwifi_firmware.txt;md5=00d06cfd3eddd5a2698948ead2ad54a5 \
  file://LICENCE.ti-connectivity;md5=186e7a43cf6c274283ad81272ca218ea \
  file://LICENCE.ueagle-atm4-firmware;md5=4ed7ea6b507ccc583b9d594417714118 \
  file://LICENCE.via_vt6656;md5=e4159694cba42d4377a912e78a6e850f \
  file://LICENCE.xc5000;md5=1e170c13175323c32c7f4d0998d53f66 \
"

SRCREV          = "d82d3c1e5eddb811a38513a7e5b33202773f0fff"

HOMEPAGE = "https://git.kernel.org/cgit/linux/kernel/git/firmware/linux-firmware.git"
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

CLEANBROKEN = "1"

do_install() {
    cd ../git

    install -d -m 0755 ${D}/lib/firmware ${D}/lib/firmware/nouveau \
        ${D}/lib/firmware/libertas ${D}/lib/firmware/rtlwifi ${D}/lib/firmware/mrvl \
        ${D}/lib/firmware/rtl_nic ${D}/lib/firmware/ti-connectivity

    install -p -m 0644 ath3k*.fw ${WORKDIR}/*.fw  ${D}/lib/firmware/
    install -p -m 0644 rt*.bin ${D}/lib/firmware/
    install -p -m 0644 libertas/*.bin ${D}/lib/firmware/libertas/
    install -p -m 0644 rtlwifi/*.bin ${D}/lib/firmware/rtlwifi/
    install -p -m 0644 rtl_nic/*.fw ${D}/lib/firmware/rtl_nic/
    install -p -m 0644 mrvl/*.bin ${D}/lib/firmware/mrvl/
    install -p -m 0644 ti-connectivity/*.bin ${D}/lib/firmware/ti-connectivity/
    install -p -m 0644 ../nouveau/*.ctxprog ../nouveau/*.ctxvals ${D}/lib/firmware/nouveau/
}

_pkginfo = "{ \
        'dvb-usb-af9015'     : [ 'dvb-usb-af9015.fw' ], \
        'dvb-usb-tt-s2400'   : [ 'dvb-usb-tt-s2400-01.fw' ], \
        'nouveau'            : [ 'nouveau/' ], \
        'rtl8712u'           : [ 'rtlwifi/rtl8712u*' ], \
        '_mrvl'		     : [ split_pkgs, 'mrvl-%s', 'mrvl', r'([a-z0-9]+)(_.*)?\.bin' ], \
        '_libertasXXX'	     : [ split_pkgs, 'libertas-%s', 'libertas', r'(([-0-9a-z]|(_v[0-9]+)|(_sdio)|(_olpc))+)(_helper)?\..*' ], \
        '_rtlXXXX'	     : [ split_pkgs, 'rtl%s', 'rtl_nic', r'rtl([0-9]+[a-z]?)-[0-9]+\.fw' ], \
        '_rtXXXX'	     : [ split_pkgs, 'rt%s', '', r'rt([0-9]+[a-z]?)\.bin' ], \
        '_rtlwifi'           : [ split_pkgs, '%s', 'rtlwifi', r'(rtl.*)fw.*\.bin' ], \
        'ath3k'              : [ 'ath3k-1.fw' ] }"

PACKAGES_DYNAMIC += 'firmware-.*'

def extend_var(d, var_name, val):
    old = d.getVar(var_name, False)
    if old is not None:
        val = old + ' ' + val
    d.setVar(var_name, val)

def split_pkgs(d, args):
    import re, os

    firmware_dir = '/lib/firmware/' + args[1]
    dvar = d.getVar('PKGD', True)
    pattern = re.compile(args[2])

    res = {}

    for f in os.listdir(dvar + firmware_dir):
        m = pattern.match(f)

        print f, m

        if not m:
            continue

        pname = 'firmware-' + (args[0] % m.group(1).replace('_', '-'))
        if pname not in res:
            res[pname] = []

        res[pname].append(os.path.join(firmware_dir, f))

    if len(res) == 0:
        raise Exception("No items found for %s" % (args,))

    for (pkg,files) in res.items():
        extend_var(d, 'FILES_%s' % pkg, ' '.join(files))

    return res.keys()

python populate_packages_prepend() {
    v = eval(d.getVar('_pkginfo', True))
    pkgs = set()
    for (n,f) in v.items():
        if n[0] != '_':
            pname = 'firmware-' + n
            pkgs.add(pname)
            files = map(lambda x: '/lib/firmware/' + x, f)
            extend_var(d, 'FILES_' + pname, ' '.join(files))
        else:
            pkgs.update(f[0](d, f[1:]))

    d.setVar('PACKAGES', ' '.join(sorted(pkgs)))
}

PV_pn_firmware-dvb-usb-af9015 = "4.95.0"
