SUMMARY = "FreeScale kobs-ng tool"
DESCRIPTION = "Dump control structures of the NAND"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

PV = "11.09.01"

SRC_URI = " \
	http://foss.doredevelopment.dk/mirrors/imx/kobs-ng-${PV}.tar.gz \
	file://linux-3.2.patch \
"

SRC_URI[md5sum] = "4743aacfd86758d65055aabb03a7d980"
SRC_URI[sha256sum] = "60b877ac02e5880b7f0370b9ea11c1c1db7fa7ca280f80b4ed1e8c5e212d17a5"

inherit autotools

BBCLASSEXTEND = "native"
