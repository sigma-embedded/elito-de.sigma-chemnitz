HOMEPAGE = "http://docs.enlightenment.org/api/imlib2/html"
PV = "1.4.4"
PR = "r1"
LICENSE = "GPL"
LIC_FILES_CHKSUM = "\
  file://COPYING;md5=344895f253c32f38e182dcaf30fe8a35 \
  file://COPYING-PLAIN;md5=f59cacc08235a546b0c34a5422133035 \
"


SRC_URI = "${SOURCEFORGE_MIRROR}/enlightenment/imlib2-${PV}.tar.bz2"

SRC_URI[md5sum] = "b6de51879502e857d5b1f7622267a030"
SRC_URI[sha256sum] = "6441fb1e88a31533791c40ea4b3310b6d5d46233ca69d9f731a2846e4e423404"

S = "${WORKDIR}/imlib2-${PV}"

PROVIDES += "virtual/imlib2"
DEPENDS += "jpeg zlib libpng tiff libungif freetype bzip2 libid3tag"

PACKAGES_DYNAMIC += 'imlib2-filters-.* imlib2-loaders-.*'


_extra_oeconf_i686 = '--enable-mmx'
EXTRA_OECONF       = ' \
  --enable-visibility-hiding \
  --disable-static \
  --with-jpeg \
  --with-png \
  --with-tiff \
  --with-gif=libungif \
  --with-zlib \
  --with-bzip2 \
  --with-id3 \
  ${_extra_oeconf}'

PACKAGES =+ "${PN}-utils ${PN}-fonts ${PN}-images"

inherit autotools

do_install_append() {
    rm -f ${D}${libdir}/imlib2/*/*.la
}

python populate_packages_prepend () {
	plugindir = bb.data.expand("${libdir}/imlib2/filters", d)
        do_split_packages(d, plugindir, '^(.*)\.so$', 'imlib2-filter-%s', '%s filter for imlib2', prepend=True, extra_depends = '')

	plugindir = bb.data.expand("${libdir}/imlib2/loaders", d)
        do_split_packages(d, plugindir, '^(.*)\.so$', 'imlib2-loaders-%s', '%s loader for imlib2', prepend=True, extra_depends = '')
}

FILES_${PN}-dbg += "${libdir}/imlib2/*/.debug"
FILES_${PN}-dev += "${bindir}/imlib2-config"
FILES_${PN}-utils += "${bindir}/*"
FILES_${PN}-fonts += "${datadir}/imlib2/data/fonts"
FILES_${PN}-images += "${datadir}/imlib2/data/images"
