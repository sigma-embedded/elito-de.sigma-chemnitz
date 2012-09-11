# --*- python -*--
PRINC := "${@int('${PRINC}') + 4}"

OVERRIDES .= "${@base_contains('DISTRO_FEATURES', 'headless', ':headless', '', d)}"

_headless-pkgs := "${@'${PACKAGES}'.replace(' ${PN}-analyze', ' ')}"
PACKAGES_headless = "${_headless-pkgs}"

FILESEXTRAPATHS_prepend := "${THISDIR}:"

SRC_URI += "\
  file://embedded.patch \
  file://readahead.patch \
  file://0001-journalctl-allow-to-build-with-older-kernels.patch \
"

EXTRA_OECONF += "\
  --with-distro=other \
  --with-sysvinit-path= \
  --with-sysvrcd-path= \
"

ROOT_HOME = "/root"

do_install_append() {
    rm -f ${D}${sysconfdir}/systemd/system/getty*/*tty1.service
    rm ${D}${libdir}/tmpfiles.d/tmp.conf
    rmdir ${D}${localstatedir}/log/journal

    t=${@base_contains('DISTRO_FEATURES', 'x11', 'graphical', 'multi-user', d)}
    rm -f ${D}${base_libdir}/systemd/system/default.target
    ln -s $t.target ${D}${base_libdir}/systemd/system/default.target
}

do_install_append_headless() {
    rm -f ${D}${bindir}/systemd-analyze
}

python populate_packages_prepend () {
    pkg_info = {
        'binfmt' : ['systemd-binfmt.service'],
        'remount-rootfs' : ['remount-rootfs.service'],
        'swap' : ['swap.target'],
        'tmpfs' : ['tmp.mount'],
        'cryptsetup' : ['cryptsetup.target'],
        'logind' : ['systemd-logind.service',
                    'dbus-org.freedesktop.login1.service'],
        'vconsole' : ['systemd-vconsole-setup.service'],
        'user-sessions' : ['systemd-user-sessions.service',
                           'systemd-ask-password-wall.path'],
        'random-seed' : ['systemd-random-seed-load.service',
                         'systemd-random-seed-save.service'],
        'binfmt' : ['proc-sys-fs-binfmt_misc.mount',
                    'proc-sys-fs-binfmt_misc.automount'],
        'graphical' : ['display-manager.service',
                       'graphical.target',
                       'runlevel5.target'],
        }

    xtra_paths = {
        'binfmt' : ['${base_libdir}/systemd/systemd-binfmt',
                    '${libdir}/binfmt.d'],
    }

    pkgs = ''
    pn = bb.data.getVar('PN', d, 1)
    for (_p,i) in pkg_info.items():
        p = '%s-%s' % (pn, _p)
        files = map(lambda x:
                        '${base_libdir}/systemd/system/%s' % x, i)
        files += map(lambda x:
                        '${base_libdir}/systemd/system/%s.wants' % x, i)
        files += map(lambda x:
                         '${base_libdir}/systemd/system/*/%s' % x, i)
        files += map(lambda x:
                         x, (xtra_paths.get(_p) or []))

        pkgs = pkgs + ' ' + p

        bb.data.setVar('FILES_' + p, ' '.join(files), d)
        bb.data.setVar('RDEPENDS_' + p, 'systemd (= ${EXTENDPKGV})', d)

    bb.data.setVar('PACKAGES',
                   pkgs + ' ' + bb.data.getVar('PACKAGES', d, 0), d)
}

PACKAGES_DYNAMIC = "systemd-.*"
RDEPENDS_systemd := "${@(bb.data.getVar('RDEPENDS_systemd', d, True) or '')\
                       .replace('dbus-systemd', '')}"

RRECOMMENDS_${PN}-swap += "util-linux-swaponoff"

python() {
    r = bb.data.getVar('RRECOMMENDS_systemd', d, True) or ""
    for x in ['util-linux-swaponoff']:
        r = r.replace(x,'')
    r = ' '.join(r.split())
    bb.data.setVar('RRECOMMENDS_systemd', r, d)
}

pkg_postinst_append_systemd () {
update-alternatives --install ${base_sbindir}/init.wrapped init.wrapped ${systemd_unitdir}/systemd 300
}

pkg_prerm_append_systemd () {
update-alternatives --remove init.wrapped ${systemd_unitdir}/systemd
}
