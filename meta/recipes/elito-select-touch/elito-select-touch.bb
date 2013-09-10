DESCRIPTION  = "Touchscreen selector helper"
LICENSE      = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

_elito_skip := "${@elito_skip(d, 'select-touch', None, 'PROJECT_FEATURES')}"

PV = "0.2"
PR = "r1"

SRC_URI = " \
  file://elito-select-touch \
\
  file://99-touch.i2c \
  file://99-touch.ac97 \
  file://99-touch.lradc \
\
  file://qt.multitouch \
  file://qt.tslib \
\
  file://ts.conf.captouch \
  file://ts.conf.restouch \
\
  file://modules.conf \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"

python() {
    TDEVS = {
        "captouch" : { 
            True      : [ "edt", "i2c", "multitouch" ],
        },
        "restouch" : {
            "wm9715"  : [ "wm9715",  "ac97",  "tslib" ],
            "ucb1400" : [ "ucb1400", "ac97",  "tslib" ],
            "wm9712"  : [ "wm9712",  "ac97",  "tslib" ],
            "lradc"   : [ "lradc",   "lradc", "tslib" ],
        }
    }

    touches = []
    all_touches = []

    mach_features = set((d.getVar('MACHINE_FEATURES', True) or "").split())

    for devs in TDEVS.values():
        all_touches.extend(map(lambda x: x[0], devs.values()))

    for (tech, devs) in TDEVS.items():
        if not tech in mach_features:
            continue
        for (type, info) in devs.items():
            if type != True and type not in mach_features:
                continue

            touches.append(info + [tech])

    d.setVar("TOUCHDEVICES", ' '.join(map(lambda x: x[0], touches)))

    d.setVar("TOUCHDEVICE_SPEC", ' '.join(
        map(lambda x: '"%s %s %s %s"' % (x[0], x[1], x[2], x[3]), touches)))

    d.prependVar("PACKAGES", ' '.join(map(lambda x:
                                          '${PN}-%s' % x[0], touches)) + ' ')

    d.appendVar("SRC_URI", ' ' + 
                ' '.join(map(lambda x: "file://pointercal.%s" % x[0], touches)))

    d.appendVar('PACKAGES_DYNAMIC', ' ^${PN}-(%s)$' % ('|'.join(all_touches),))
}

try_ln() {
    test -e "$2" || ln -s "$1" "$2"
}

do_configure[vardeps] += "MACHINE_FEATURES"
do_configure() {
    sed 's!@sysconfdir@!${sysconfdir}!g' ${WORKDIR}/elito-select-touch \
    	> elito-select-touch

    for i in ${TOUCHDEVICE_SPEC}; do
	set -- $i

	test -s ${WORKDIR}/pointercal.$1 || rm -f ${WORKDIR}/pointercal.$1

    	try_ln 99-touch.$2 ${WORKDIR}/99-touch.$1
    	try_ln qt.$3       ${WORKDIR}/qt.$1
    	try_ln ts.conf.$4  ${WORKDIR}/ts.conf.$1
    done
}

python elito_select_touch_populate_packages() {
    FILES = [
        "${sysconfdir}/pointercal",
        "${sysconfdir}/ts.conf",
        "${sysconfdir}/udev/rules-local/99-touch.rules",
        "${sysconfdir}/default/qt.env",
    ]

    pn = d.getVar('PN', True)

    touches = d.getVar("TOUCHDEVICES", True).split()
    #d.prependVar("PACKAGES", ' '.join(map(lambda x: '${PN}-%s' % x, touches)) + ' ')

    for t in touches:
        pkg="%s-%s" % (pn, t)

        localdata = d.createCopy()
        localdata.prependVar("OVERRIDES", pkg + ":")
        bb.data.update_data(localdata)

        d.appendVar("FILES_" + pkg,
                    ' '.join(map(lambda x: "%s.%s" % (x, t), FILES)))
        d.appendVar("RDEPENDS_" + pkg, " ${PN} (= ${EXTENDPKGV})")
        d.appendVar("RPROVIDES_" + pkg, " pointercal")

        prio = localdata.getVar('DEVPRIO', True) or "50"

        postinst  = localdata.getVar('pkg_postinst', True) or '#!/bin/sh\n'
        postinst += "$D${sbindir}/elito-select-touch %s %s\n" % (t, prio)

        d.setVar("pkg_postinst_" + pkg, postinst)
}

PACKAGESPLITFUNCS_prepend += "elito_select_touch_populate_packages"

touch_install() {
    for v in ${TOUCHDEVICES}; do
	install -D -p -m 0644 ${WORKDIR}/$1.$v ${D}$2.$v
    done
}

do_install[vardeps] += "MACHINE_FEATURES"
do_install() {
    touch_install 99-touch   ${sysconfdir}/udev/rules-local/99-touch.rules

:
    touch_install pointercal ${sysconfdir}/pointercal
    touch_install qt         ${sysconfdir}/default/qt.env
    touch_install ts.conf    ${sysconfdir}/ts.conf

    install -D -p -m 0644 ${WORKDIR}/modules.conf ${D}${sysconfdir}/modprobe.d/80-touch.conf
    install -D -p -m 0755 elito-select-touch ${D}${sbindir}/elito-select-touch

    ln -s ../run/pointercal ${D}${sysconfdir}/pointercal
    ln -s ../run/ts.conf    ${D}${sysconfdir}/ts.conf
    ln -s ../../run/qt.env  ${D}${sysconfdir}/default/qt.env
}

FILES_${PN} = "\
  ${sbindir}/elito-select-touch \
  ${sysconfdir}/ts.conf \
  ${sysconfdir}/pointercal \
  ${sysconfdir}/modprobe.d/80-touch.conf \
  ${sysconfdir}/default/qt.env"

DEVPRIO_${PN}-edt = "40"
