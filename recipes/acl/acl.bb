HOMEPAGE = "http://oss.sgi.com/projects/xfs/"
DESCRIPTION = "Access control list utilities"
LICENSE = "LGPLv2+"
DEPENDS = "attr"

PV = "2.2.48"
PR = "r0"

SRC_URI = "\
	ftp://oss.sgi.com/projects/xfs/cmd_tars/acl-${PV}.src.tar.gz \
	file://configure.patch;patch=1 \
        file://configure-paths.patch;patch=1"

inherit autotools autotools_stage gettext

EXTRA_OECONF += "--disable-static"
EXTRA_AUTORECONF += "-I m4 --exclude=autoheader"
