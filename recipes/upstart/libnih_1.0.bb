require libnih.inc

DEPENDS  += "libnih-native"
PACKAGES  = "${PN}-dbg ${PN}-dev ${PN}-doc ${PN}"

FILES_${PN}-lib   = "${_libdir}/*.so.*"
FILES_${PN}-dev  += "${_libdir}/*.so ${bindir}/nih-dbus-tool"
