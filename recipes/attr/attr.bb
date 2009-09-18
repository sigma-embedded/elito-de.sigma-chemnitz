HOMEPAGE = "http://oss.sgi.com/projects/xfs/"
DESCRIPTION = "Access control list utilities"
LICENSE = "LGPLv2+"

PV = "2.4.43"
PR = "r2"

SRC_URI = "\
	ftp://oss.sgi.com/projects/xfs/cmd_tars/attr_${PV}-1.tar.gz \
	file://configure.patch;patch=1 \
	file://configure-paths.patch;patch=1"

inherit autotools autotools_stage gettext

EXTRA_OECONF += "--disable-gettext --disable-static"
EXTRA_AUTORECONF += "-I m4 --exclude=autoheader"
