INITSCRIPT_PACKAGES = "${PN}-sysv"

do_install_append() {
	install -d -m 0755 ${D}${localstatedir}/lib/connman
}

#### remove me when upstream is at 1.6 ####

PV = "1.9"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRCREV = "4bf217329babf4f7792b43d70228af8269c3332c"
SRC_URI += "file://0001-added-noipconfig-option.patch"

IGNORE_PATCHES = " \
            file://0002-storage.c-If-there-is-no-d_type-support-use-fstatat.patch \
            file://0001-timezone.c-If-there-is-no-d_type-support-use-fstatat.patch"

EXTRA_OECONF += "--enable-client"
DEPENDS += "readline"

def filter_patches(d):
    src_uri = d.getVar('SRC_URI', True).split()
    ignore  = set(d.getVar('IGNORE_PATCHES', True).split())

    res = filter(lambda x: x not in ignore, src_uri)
    return ' '.join(res)

SRC_URI := "${@filter_patches(d)}"

do_install_append() {
    install -D -p -m 0755 client/connmanctl ${D}${bindir}/connmanctl
}

PACKAGES =+ "${PN}-client"
FILES_${PN}-client += "${bindir}/connmanctl"
