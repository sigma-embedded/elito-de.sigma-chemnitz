# --*- python -*--
PRINC := "${@int('${PRINC}') + 1}"

FILESEXTRAPATHS_prepend := "${THISDIR}:"

WATCHDOG_TIMEOUT ?= "60"

SRC_URI += "\
  file://embedded.patch \
  file://readahead.patch \
  file://read-only.patch \
  file://0001-journalctl-allow-to-build-with-older-kernels.patch \
"

EXTRA_OECONF += "\
  --with-sysvinit-path= \
  --with-sysvrcnd-path= \
"

systemd_bindir = "${systemd_unitdir}"

do_configure_prepend() {
    echo 'install-aliases-hook:	install-directories-hook' >> Makefile.am
}

do_configure_append() {
    grep '#RuntimeWatchdogSec=0' src/core/system.conf
    if test -n "${WATCHDOG_TIMEOUT}"; then
    	echo 'RuntimeWatchdogSec=${WATCHDOG_TIMEOUT}' >> src/core/system.conf
    fi
}

do_install_append() {
    rm -f ${D}${sysconfdir}/systemd/system/getty*/*tty1.service
    rm ${D}${libdir}/tmpfiles.d/tmp.conf
    rmdir ${D}${localstatedir}/log/journal

    t=${@base_contains('DISTRO_FEATURES', 'x11', 'graphical', 'multi-user', d)}
    rm -f ${D}${systemd_unitdir}/system/default.target
    ln -s $t.target ${D}${systemd_unitdir}/system/default.target

    mkdir -p ${D}${systemd_unitdir}/system/default.target.wants
    ln -s \
        ../systemd-readahead-replay.service \
        ../systemd-readahead-collect.service \
        ${D}${systemd_unitdir}/system/default.target.wants/

    ${@base_contains("PROJECT_FEATURES", "select-touch", "rm -f ${D}${sysconfdir}/udev/rules.d/touchscreen.rules", ":", d)}
}

python systemd_elito_populate_packages () {
    pkg_info = {
        'remount-rootfs' : ['remount-rootfs.service'],
        'swap' : ['swap.target'],
        'tmpfs' : ['tmp.mount'],
        'logind' : ['systemd-logind.service',
                    'dbus-org.freedesktop.login1.service'],
        'user-sessions' : ['systemd-user-sessions.service',
                           'systemd-ask-password-wall.path'],
        'random-seed' : ['systemd-random-seed.service'],
        'graphical' : ['display-manager.service',
                       'graphical.target',
                       'runlevel5.target'],
        'readahead' : ['systemd-readahead-done.timer',
                       'systemd-readahead-drop.service',
                       'systemd-readahead-done.service',
                       'systemd-readahead-replay.service',
                       'systemd-readahead-collect.service'],
        'bootchart' : [ ],
        }

    xtra_paths = {
        'readahead' : [ '${systemd_bindir}/systemd-readahead' ],
    }

    pkgs = ''
    pn = bb.data.getVar('PN', d, 1)
    for (_p,i) in pkg_info.items():
        p = '%s-%s' % (pn, _p)
        files = map(lambda x:
                        '${systemd_unitdir}/system/%s' % x, i)
        files += map(lambda x:
                        '${systemd_unitdir}/system/%s.wants' % x, i)
        files += map(lambda x:
                         '${systemd_unitdir}/system/*/%s' % x, i)
        files += ['${sysconfdir}/systemd/%s.conf' % _p,
                  '${systemd_unitdir}/systemd-%s' % _p]
        files += map(lambda x:
                         x, (xtra_paths.get(_p) or []))

        pkgs = pkgs + ' ' + p

        bb.data.setVar('FILES_' + p, ' '.join(files), d)
        bb.data.setVar('RDEPENDS_' + p, 'systemd (= ${EXTENDPKGV})', d)

    bb.data.setVar('PACKAGES',
                   pkgs + ' ' + bb.data.getVar('PACKAGES', d, 0), d)
}
PACKAGESPLITFUNCS_prepend = "systemd_elito_populate_packages "

PACKAGES_DYNAMIC = "systemd-.*"
RDEPENDS_systemd := "${@(bb.data.getVar('RDEPENDS_systemd', d, True) or '')\
                       .replace('dbus-systemd', '')}"

RRECOMMENDS_${PN}-swap += "util-linux-swaponoff"
RRECOMMENDS_${PN} += "util-linux-mount systemd-readahead"

python() {
    r = bb.data.getVar('RRECOMMENDS_systemd', d, True) or ""
    for x in ['util-linux-swaponoff', 'systemd-compat-units',
              'e2fsprogs-e2fsck']:
        r = r.replace(x,'')
    r = ' '.join(r.split())
    bb.data.setVar('RRECOMMENDS_systemd', r, d)

    r = bb.data.getVar('RRECOMMENDS_udev', d, True) or ""
    for x in ['udev-hwdb', 'udev-extraconf']:
        r = r.replace(x,'')
    r = ' '.join(r.split())
    bb.data.setVar('RRECOMMENDS_udev', r, d)
}

ALTERNATIVE_${PN} += "init.wrapped"

ALTERNATIVE_LINK_NAME[init.wrapped] = "${base_sbindir}/init.wrapped"
ALTERNATIVE_TARGET[init.wrapped]    = "${systemd_bindir}/systemd"
ALTERNATIVE_PRIORITY[init.wrapped]  = "300"
