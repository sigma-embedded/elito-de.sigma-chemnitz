HOMEPAGE = "http://docs.enlightenment.org/api/imlib2/html"
PV = "1.4.2"
PR = "r0"

SRC_URI = "${SOURCEFORGE_MIRROR}/enlightenment/imlib2-${PV}.tar.bz2;name=tarball \
	file://imlib2-1.4.2-cve-2008-5187.patch;patch=1"

SRC_URI[tarball.md5sum] = "9f15568e76ebda9092c571e0df261f54"
SRC_URI[tarball.sha256sum] = "fc7b9eaad7b028872db4d660053b700c27dcbddb5212a03a9d7661ef8f09e89e"

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
  ${_extra_oeconf_i686}'

PACKAGES =+ "${PN}-utils ${PN}-fonts ${PN}-images"

inherit autotools autotools_stage

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
