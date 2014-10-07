FILESEXTRAPATHS_prepend := "${THISDIR}:"
#SRC_URI += "file://reset-mtime.patch;apply=no"

do_configure_prepend() {
    cd ${WORKDIR}
    #patch -p1 < reset-mtime.patch
    cd -
}
