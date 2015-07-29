SUMMARY = "FreeScale kobs-ng tool"
DESCRIPTION = "Dump control structures of the NAND"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

PV = "3.14.28"
_pv = "${PV}-1.0.0"
PE = "1"
FSL_MIRROR ?= "http://www.freescale.com/lgfiles/NMG/MAD/YOCTO/"

SRC_URI = "\
  ${FSL_MIRROR}/imx-kobs-${_pv}.tar.gz \
  file://0001-fixed-bug-in-evaluation-of-setup-options.patch \
"

SRC_URI[md5sum] = "0077ec992b281ebbce2928564a08b207"
SRC_URI[sha256sum] = "cfac042f5c96731205c397a4a6b3ed966f804569ae4d0e2685d22fdf6bdc9eb7"

inherit  autotools pkgconfig

S = "${WORKDIR}/imx-kobs-${_pv}"

BBCLASSEXTEND = "native"
