DESCRIPTION = "ELiTo base rescue utils"
LICENSE      = "GPLv3"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

PV = "0.1.2"

DISTRO_FEATURES += "sysvinit"
VIRTUAL-RUNTIME_init_manager = "sysvinit"

SRC_URI = "\
  file://elito-rescue.conf \
  file://init \
  file://dhcp-notify \
  file://dhcp-ntpd \
  file://set-time \
  file://rc.d/sysinit \
  file://rc.d/scan-blockdev \
  file://rc.d/scan-mtd \
  file://rc.d/scan-ubi \
  file://rc.d/syslog \
  file://rc.d/network-dhcp \
  file://rc.d/httpd \
  file://rc.d/tcpstream \
  file://rc.d/blockdev \
"

B := "${S}"
S  = "${WORKDIR}"

python () {
    rc_d = [ ( 3, "sysinit", ""),
             (20, "syslog", ""),
             (21, "scan-blockdev", ""),
             (21, "scan-mtd", ""),
             (21, "scan-ubi", ""),
             (25, "network-dhcp", ""),
             (80, "httpd", ""),
             (80, "tcpstream", ""),
             (81, "blockdev", ""),
         ]

    def extend_var(name, new, prepend=False):
        old  = d.getVar(name, False) or ""
        if prepend:
            v = new + " " + old
        else:
            v = old + " " + new

        d.setVar(name, v)

    pkgs = []
    pn = d.getVar("PN", True)
    for (prio,name, deps) in rc_d:
        pkg = "%s-sysv-%s" % (pn, name)
        fname = "rescue-" + name

        pkgs.append(pkg)
        d.setVar("INITSCRIPT_NAME_" + pkg, fname)
        d.setVar("INITSCRIPT_PARAMS_" + pkg, "defaults %u %u" % (prio, 99-prio))
        d.setVar("FILES_" + pkg, "${INIT_D_DIR}/" + fname)
        d.setVar("RDEPENDS_" + pkg, "update-rc.d " + deps)
        d.setVar("RPROVIDES_" + pkg, "virtual/rescue-init-%s" % name)

    extend_var("INITSCRIPT_PACKAGES", " ".join(pkgs))
    extend_var("PACKAGES", " ".join(pkgs), True)
}

UPDATERCPN = "dummy"

inherit update-rc.d

do_install() {
    install -p -D -m 0755 ${S}/init ${D}/init
    install -p -D -m 0644 ${S}/elito-rescue.conf ${D}${sysconfdir}/elito-rescue.conf

    cd ${S}/rc.d
    for i in *; do
	install -p -D -m 0755 "$i" ${D}${INIT_D_DIR}/rescue-"$i"
    done

    install -p -D -m 0755 ${S}/set-time    ${D}${bindir}/rescue-set-time
    install -p -D -m 0755 ${S}/dhcp-ntpd   ${D}${sysconfdir}/udhcpc.d/80ntpd
    install -p -D -m 0755 ${S}/dhcp-notify ${D}${sysconfdir}/udhcpc.d/90notify
}

PACKAGES += "${PN}-dhcp-notify ${PN}-dhcp-ntpd"
RPROVIDES_${PN} += "virtual/rescue-conf"

RRECOMMENDS_${PN} += "${PN}-sysv-sysinit"

CONFFILES_${PN} = "${sysconfdir}/elito-rescue.conf"
FILES_${PN} = "/init ${sysconfdir}/elito-rescue.conf"
FILES_${PN}-dhcp-notify = "${sysconfdir}/udhcpc.d/90notify"
RDEPENDS_${PN}-dhcp-notify = "virtual/rescue-init-network-dhcp"

FILES_${PN}-dhcp-ntpd = "${sysconfdir}/udhcpc.d/80ntpd ${bindir}/rescue-set-time"
