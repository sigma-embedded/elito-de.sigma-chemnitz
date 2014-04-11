DESCRIPTION = "IO::Socket::INET6 - Object interface for AF_INET|AF_INET6 domain sockets"
SECTION = "libs"
LICENSE = "Artistic|GPLv1+"
RDEPENDS_${PN} += "perl-module-test-more libsocket6-perl perl-module-io-socket"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Artistic-2.0;md5=8bbc66f0ba93cec26ef526117e280266"

BBCLASSEXTEND = "native"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/S/SH/SHLOMIF/IO-Socket-INET6-${PV}.tar.gz;name=io-socket-inet6-perl-${PV}"
SRC_URI[io-socket-inet6-perl-2.67.md5sum] = "228c12e74df686f78b09aee1139dc11c"
SRC_URI[io-socket-inet6-perl-2.67.sha256sum] = "dd90e417cbd37047b71469ec99e79fe89a3bb5103769fc9c76b3c87d8cb019b2"

S = "${WORKDIR}/IO-Socket-INET6-${PV}"

inherit cpan

PACKAGE_ARCH = "all"
