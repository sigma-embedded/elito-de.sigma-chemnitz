SUMMARY = "FreeScale elftosb tool"
DESCRIPTION = "Tool to convert ELF format executable files and other source files into the encrypted boot images used by the i.MX23 and i.MX28. Includes Windows and Linux binaries, full source code, and all related documentation."
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=172ede34353056ebec7a597d8459f029"

PV = "11.09.01"
PR = "r1"

SRC_URI = " \
	http://foss.doredevelopment.dk/mirrors/imx/elftosb-${PV}.tar.gz \
	file://buildflags.patch \
	file://stdc.patch \
"

SRC_URI[md5sum] = "10deccaf1af9e9d396ffbf51e7d320c0"
SRC_URI[sha256sum] = "78e91fb9c3d0ec2c030f2ec2554bafe14c6723879a9649222bb3b92ea0952c33"

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
