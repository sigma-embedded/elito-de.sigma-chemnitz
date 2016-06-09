FILESEXTRAPATHS_prepend := "${THISDIR}/v4l-utils:"

PATH =. "${B}/_bin:"
export PATH

SRC_URI += "file://0001-configure-added-enable-x11.patch"

# HACK: remove/revalidate me after 2014-01-01
DEPENDS := "${@d.getVar('DEPENDS', False).replace('virtual/libx11','')}"

_PACKAGECONFIG_DEFAULT := "${@d.getVarFlag('PACKAGECONFIG', '_defaultval', False)}"
_PACKAGECONFIG_DEFAULT += "\
   ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} \
"

PACKAGECONFIG ??= "${_PACKAGECONFIG_DEFAULT}"

PACKAGECONFIG[x11] = "--enable-x11,--disable-x11,virtual/libx11"

do_unpackextra() {
	mkdir -p ${B}/_bin
	ln -sf /bin/false ${B}/_bin/qmake-qt4
}
addtask unpackextra after do_unpack before do_configure
