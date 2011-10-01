# --*- python -*--
SRC_URI += "\
  file://embedded.patch \
"

EXTRA_OECONF += "\
  --with-distro=other \
  --with-sysvinit-path= \
  --with-sysvrcd-path= \
"

do_install_append() {
    rm -f ${D}${sysconfdir}/systemd/system/getty*/*tty1.service
    rm ${D}${libdir}/tmpfiles.d/tmp.conf
}

python populate_packages_prepend () {
	pkg_info = {
	'remount-rootfs' : ['remount-rootfs.service'],
	'swap' : ['swap.target'],
	'cryptsetup' : ['cryptsetup.target'],
	'logind' : ['systemd-logind.service',
                    'dbus-org.freedesktop.login1.service'],
	'vconsole' : ['systemd-vconsole-setup.service'],
        'user-sessions' : ['systemd-user-sessions.service',
                           'systemd-ask-password-wall.path']
	}

	pkgs = ''
	pn = bb.data.getVar('PN', d, 1)
	for (_p,i) in pkg_info.items():
		p = '%s-%s' % (pn, _p)
		files = map(lambda x:
                            '${base_libdir}/systemd/system/%s' % x, i)
		files += map(lambda x:
                            '${base_libdir}/systemd/system/*/%s' % x, i)
		pkgs = pkgs + ' ' + p

		bb.data.setVar('FILES_' + p, ' '.join(files), d)
		bb.data.setVar('RDEPENDS_' + p, 'systemd (= ${EXTENDPKGV})', d)

	bb.data.setVar('PACKAGES',
                       pkgs + ' ' + bb.data.getVar('PACKAGES', d, 0), d)
}

PACKAGES_DYNAMIC = "systemd-.*"
RDEPENDS_systemd := "${@(bb.data.getVar('RDEPENDS_systemd', d, True) or '')\
                       .replace('dbus-systemd', '')}"
