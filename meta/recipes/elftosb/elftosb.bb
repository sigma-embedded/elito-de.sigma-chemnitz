SUMMARY = "FreeScale elftosb tool"
DESCRIPTION = "Tool to convert ELF format executable files and other source files into the encrypted boot images used by the i.MX23 and i.MX28. Includes Windows and Linux binaries, full source code, and all related documentation."
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=172ede34353056ebec7a597d8459f029"

#PV = "2.6.1"
PV = "10.12.01"

SRC_URI = " \
	http://foss.doredevelopment.dk/mirrors/imx/elftosb-${PV}.tar.gz \
	file://buildflags.patch \
"

SRC_URI[md5sum] = "e8005d606c1e0bb3507c82f6eceb3056"
SRC_URI[sha256sum] = "77bb6981620f7575b87d136d94c7daa88dd09195959cc75fc18b138369ecd42b"

S = "${WORKDIR}/elftosb-${PV}"

EXTRA_OEMAKE = " \
  CC='${CC}' \
  CXX='${CXX}' \
  XCFLAGS='${CXXFLAGS}' \
  XLDFLAGS='${LDFLAGS}' \
"

do_compile() {
    unset CFLAGS
    oe_runmake -e
}

do_install() {
    for i in elftosb keygen sbtool; do
	install -p -D -m 0755 bld/linux/$i ${D}${bindir}/freescale-$i
    done

#    for i in *.pdf; do
#	install -p -D -m 0644 $i ${D}${datadir}/doc/elftosb-${PV}/$i
#    done
}

BBCLASSEXTEND = "native nativesdk"
