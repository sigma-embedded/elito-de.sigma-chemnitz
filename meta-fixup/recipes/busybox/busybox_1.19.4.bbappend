FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += " \
	file://elito-directory.patch \
        file://cvt-color.patch \
        file://busybox-mbus-env.patch \
        file://ifup-upstart.patch \
\
	file://test-access.patch \
"
