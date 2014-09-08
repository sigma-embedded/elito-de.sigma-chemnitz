# --*- python -*--
FILESEXTRAPATHS_prepend := "${THISDIR}:"

WATCHDOG_TIMEOUT ?= "60"

DEPENDS += "elfutils curl"

PACKAGECONFIG[oldkernel] = "--disable-networkd,,,"

SRC_URI += "\
  file://embedded.patch \
  file://readahead.patch \
  file://read-only.patch \
  file://0001-journalctl-allow-to-build-with-older-kernels.patch \
  file://networkd-split.patch \
  file://no-needs-update-on-ro.patch \
"

EXTRA_OECONF += "\
  --with-sysvinit-path= \
  --with-sysvrcnd-path= \
"

systemd_bindir = "${systemd_unitdir}"

def systemd_default_target(d):
    features=d.getVar('DISTRO_FEATURES', True).split()
    if 'headless' in features:
        return 'multi-user';
    elif 'x11' in features:
        return 'graphical'
    else:
        return 'multi-user';

SYSTEMD_DEFAULT_TARGET[vardeps] += "DISTRO_FEATURES"
SYSTEMD_DEFAULT_TARGET ?= "${@systemd_default_target(d)}"

do_unpackextra() {
    echo 'install-aliases-hook:	install-directories-hook' >> ${S}/Makefile.am

    grep '#RuntimeWatchdogSec=0' ${S}/src/core/system.conf
    if test -n "${WATCHDOG_TIMEOUT}"; then
       echo 'RuntimeWatchdogSec=${WATCHDOG_TIMEOUT}' >> ${S}/src/core/system.conf
    fi
}
addtask unpackextra after do_unpack before do_configure

do_install_append() {
    rm -f ${D}${sysconfdir}/systemd/system/getty*/*tty1.service
    rm ${D}${libdir}/tmpfiles.d/tmp.conf
    rm ${D}${libdir}/tmpfiles.d/var.conf
    rm ${D}${libdir}/tmpfiles.d/etc.conf
    rmdir ${D}${localstatedir}/log/journal
    rmdir ${D}${localstatedir}/log

    t='${SYSTEMD_DEFAULT_TARGET}'
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
        'logind' : [ 'systemd-logind.service',
                     'dbus-org.freedesktop.login1.service',
                     'org.freedesktop.login1.busname' ],
        'user-sessions' : ['systemd-user-sessions.service',
                           'systemd-ask-password-wall.path',
                           'user.slice',
                           'user@.service' ],
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
        'localed' : [ 'systemd-localed.service',
                      'org.freedesktop.locale1.busname',
                      'dbus-org.freedesktop.locale1.service'],
        'networkd' : [ 'network.target',
                       'network-pre.target',
                       'network-online.target',
                       'systemd-networkd-wait-online.service',
                       'systemd-networkd.service' ],
        'hostnamed' : [ 'systemd-hostnamed.service',
                        'org.freedesktop.hostname1.busname',
                        'dbus-org.freedesktop.hostname1.service' ],
        'machined' : [ 'systemd-machined.service',
                       'dbus-org.freedesktop.machine1.service',
                       'org.freedesktop.machine1.busname',
                       ],
        'nspawn' : [ 'systemd-nspawn@.service' ],
        'resolved' : [ 'systemd-resolved.service',
                       'org.freedesktop.resolve1.busname',
                       'dbus-org.freedesktop.resolve1.service' ],
        'timesyncd' : [ 'systemd-timesyncd.service' ],
        'ldconfig' : [ 'ldconfig.service' ],
        'sysusers' : [ 'systemd-sysusers.service' ],
        'journal-upload' : [ 'systemd-journal-upload.service' ],
        'ask-password' : [ 'systemd-ask-password-console.path',
                           'systemd-ask-password-console.service',
                           'systemd-ask-password-wall.service',
                       ],
        'timedated' : [ 'dbus-org.freedesktop.timedate1.service',
                        'systemd-timedated.service',
                        'org.freedesktop.timedate1.busname',
                        ]
        }

    xtra_paths = {
        'readahead' : [ '${systemd_bindir}/systemd-readahead' ],
        'networkd'  : [ '${systemd_bindir}/systemd-networkd-wait-online',
                        '${systemd_bindir}/network',
                        '${sysconfdir}/systemd/network',
                        '${libdir}/systemd/network',
                        '${libdir}/tmpfiles.d/systemd-networkd.conf',
                        '${base_bindir}/networkctl' ],
        'localed' : [ '${sysconfdir}/dbus-1/system.d/org.freedesktop.locale1.conf',
                      '${datadir}/polkit-1/actions/org.freedesktop.locale1.policy',
                      '${bindir}/localectl' ],
        'hostnamed' : [ '${sysconfdir}/dbus-1/system.d/org.freedesktop.hostname1.conf',
                        '${datadir}/polkit-1/actions/org.freedesktop.hostname1.policy',
                        '${bindir}/hostnamectl' ],
        'machined' : [ '${sysconfdir}/dbus-1/system.d/org.freedesktop.machine1.conf',
                       '${base_bindir}/machinectl' ],
        'nspawn' : [ '${bindir}/systemd-nspawn' ],
        'timesyncd' : [ '${libdir}/systemd/systemd-timesyncd' ],
        'sysusers' :  [ '${libdir}/sysusers.d',
                        '${base_bindir}/systemd-sysusers' ],
        'resolved' : [ '${sysconfdir}/dbus-1/system.d/org.freedesktop.resolve1.conf',
                       '${libdir}/systemd/systemd-resolve-host',
                       '${sysconfdir}/dbus-1/system.d/org.freedesktop.login1.conf',
                       '${systemd_bindir}/systemd-resolve-host',
                   ],
        'logind' : [ '${base_bindir}/loginctl',
                     '${datadir}/polkit-1/actions/org.freedesktop.login1.policy',
                 ],
        'journal-upload' : [ '${libdir}/tmpfiles.d/systemd-remote.conf' ],
        'ask-password' : [ '${systemd_bindir}/systemd-reply-password',
                           '${base_bindir}/systemd-ask-password',
                           '${base_bindir}/systemd-tty-ask-password-agent' ],
        'timedated' : [ '${sysconfdir}/dbus-1/system.d/org.freedesktop.timedate1.conf',
                        '${bindir}/timedatectl',
                        '${datadir}/polkit-1/actions/org.freedesktop.timedate1.policy',
],
        'user-sessions' : [ '${sysconfdir}/xdg/systemd/user',
                            '${sysconfdir}/systemd/user',
                            '${sysconfdir}/systemd/user.conf',
                            '${libdir}/systemd/user-generators',
                            '${libdir}/systemd/user' ],
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
        files += map(lambda x:
                     '${sysconfdir}/systemd/system/%s.wants' % x, i)
        files += map(lambda x:
                     '${sysconfdir}/systemd/system/*/%s' % x, i)
        files += map(lambda x:
                     '${datadir}/dbus-1/system-services/%s' % x.strip('dbus-'), i)
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

PACKAGES =+ "libgudev libnss-mymachines libnss-resolve libnss-myhostname ${PN}-utils"
FILES_libgudev = "${libdir}/libgudev*.so.*"
FILES_libnss-myhostname = "${libdir}/libnss_myhostname.so.*"
FILES_libnss-mymachines = "${libdir}/libnss_mymachines.so.*"
FILES_libnss-resolve = "${libdir}/libnss_resolve.so.*"

FILES_${PN}-utils = "\
  ${bindir}/busctl \
  ${bindir}/systemd-cgls \
  ${bindir}/systemd-cgtop \
  ${bindir}/systemd-delta \
  ${bindir}/systemd-detect-virt \
"

PACKAGES =+ "${PN}-bash-completion"
FILES_${PN}-bash-completion = "${datadir}/bash-completion/completions/*"

PACKAGES_DYNAMIC = "systemd-.*"
RRECOMMENDS_${PN}-swap += "util-linux-swaponoff"
RRECOMMENDS_${PN} += "util-linux-mount systemd-readahead"

USERADD_PACKAGES += "${PN}-networkd"
USERADD_PARAM_${PN}-networkd += "--system systemd-network"

USERADD_PACKAGES += "${PN}-timesyncd"
USERADD_PARAM_${PN}-timesyncd += "--system systemd-timesync"

USERADD_PACKAGES += "${PN}-resolved"
USERADD_PARAM_${PN}-resolved += "--system systemd-resolve"

USERADD_PACKAGES += "${PN}-journal-upload"
USERADD_PARAM_${PN}-journal-upload += "--system systemd-journal-remote"

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

RDEPENDS_${PN}_remove = "volatile-binds"

##### HACK; remove me after 2014-01-01
SYSTEMD_SERVICE_${PN}-binfmt = ""
