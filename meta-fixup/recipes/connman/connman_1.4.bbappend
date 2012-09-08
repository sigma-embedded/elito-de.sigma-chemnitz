INITSCRIPT_PACKAGES = "${PN}-sysv"

do_install_append() {
	install -d -m 0755 ${D}${localstatedir}/lib/connman
}

#### remove me when upstream is at 1.6 ####

PV = "1.6"

SRCREV = "7e151a7520740b586fe4f2adad1aa21864ef5321"

IGNORE_PATCHES = " \
            file://0002-storage.c-If-there-is-no-d_type-support-use-fstatat.patch \
            file://0001-timezone.c-If-there-is-no-d_type-support-use-fstatat.patch"

def filter_patches(d):
    src_uri = d.getVar('SRC_URI', True).split()
    ignore  = set(d.getVar('IGNORE_PATCHES', True).split())

    res = filter(lambda x: x not in ignore, src_uri)
    return ' '.join(res)

SRC_URI := "${@filter_patches(d)}"
