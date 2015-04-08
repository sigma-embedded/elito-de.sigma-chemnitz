HOMEPAGE = "http://schrodinger.sourceforge.net/"
LICENSE = "MPL && GPL && LGPL && MIT"
DEPENDS = "liboil orc-native orc"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=d91a46405fc074b88c963cc4f2a0aae9 \
	file://COPYING.GPL;md5=e181e3b7c66f5f96921d813c1074f833 \
	file://COPYING.LGPL;md5=38c893e21baec4cd75ad800ba9e2410a \
	file://COPYING.MIT;md5=8b345371b3536b4ce37ead1eafc88221 \
	file://COPYING.MPL;md5=0117647fecb9a932c25a7bbfc0333c37"

FILESEXTRAPATHS_prepend := "${THISDIR}/schroedinger:"

SRC_URI = "http://www.diracvideo.org/download/schroedinger/${P}.tar.gz;name=schroedingertargz"
SRC_URI += "file://configure.ac.patch"

EXTRA_OECONF += "STAGING_DIR=${STAGING_DIR_NATIVE}"

inherit autotools

do_configure_append () {
        # Fix stupid libtool... handling. 
        # rpath handling can't be disabled and the Makefile's can't be regenerated..
        # (GNU sed required)
        sed -i s/^hardcode_libdir_flag_spec/#hardcode_libdir_flag_spec/ ${S}/*-libtool
}
