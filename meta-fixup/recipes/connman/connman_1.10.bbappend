INITSCRIPT_PACKAGES = "${PN}-sysv"

do_install_append() {
	install -d -m 0755 ${D}${localstatedir}/lib/connman
}

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "\
  file://0001-added-noipconfig-option.patch \
  file://linux-3.8.patch \
"

EXTRA_OECONF += "--enable-client"
DEPENDS += "readline"

do_install_append() {
    install -D -p -m 0755 client/connmanctl ${D}${bindir}/connmanctl
}

PACKAGES =+ "${PN}-client"
FILES_${PN}-client += "${bindir}/connmanctl"
