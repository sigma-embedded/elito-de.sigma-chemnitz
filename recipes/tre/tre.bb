HOMEPAGE = "http://laurikari.net/tre"
LICENSE  = "BSD"
PV = "0.8.0"
PR = "r0"

SRC_URI = "http://laurikari.net/tre/tre-${PV}.tar.bz2;name=tarball \
	file://tre-pkgconfig.patch;patch=1"

SRC_URI[tarball.md5sum] = "b4d3232593dadf6746f4727bdda20b41"
SRC_URI[tarball.sha256sum] = "8dc642c2cde02b2dac6802cdbe2cda201daf79c4ebcbb3ea133915edf1636658"

inherit autotools autotools_stage pkgconfig
