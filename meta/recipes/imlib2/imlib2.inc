HOMEPAGE = "http://docs.enlightenment.org/api/imlib2/html"
PV = "1.4.6"
LICENSE = "GPL"
LIC_FILES_CHKSUM = "\
  file://COPYING;md5=344895f253c32f38e182dcaf30fe8a35 \
  file://COPYING-PLAIN;md5=f59cacc08235a546b0c34a5422133035 \
"


SRC_URI = "${SOURCEFORGE_MIRROR}/enlightenment/imlib2-${PV}.tar.bz2"

SRC_URI[md5sum] = "5c7104121ec6db652b37f74a6d7048e2"
SRC_URI[sha256sum] = "af51be727d62cfcff7457c753f355e44848fb997f33a7e1d43775276a9073274"

S = "${WORKDIR}/imlib2-${PV}"

PROVIDES += "virtual/imlib2"
DEPENDS += "jpeg zlib libpng tiff giflib freetype bzip2 libid3tag"

PACKAGES_DYNAMIC += 'imlib2-filters-.* imlib2-loaders-.*'


_extra_oeconf_i686 = '--enable-mmx'
EXTRA_OECONF       = ' \
  --enable-visibility-hiding \
  --disable-static \
  --with-jpeg \
  --with-png \
  --with-tiff \
  --with-gif=giflib \
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
