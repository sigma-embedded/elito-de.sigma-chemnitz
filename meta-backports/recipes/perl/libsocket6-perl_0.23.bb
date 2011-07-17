DESCRIPTION = "Perl extensions for IPv6"
SECTION = "libs"
LICENSE = "unknown"
PR = "r1"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Artistic;md5=f921793d03cc6d63ec4b15e9be8fd3f8"

BBCLASSEXTEND = "native"

CFLAGS += "-D_LARGEFILE_SOURCE -D_LARGEFILE64_SOURCE"
BUILD_CFLAGS += "-D_LARGEFILE_SOURCE -D_LARGEFILE64_SOURCE"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/U/UM/UMEMOTO/Socket6-${PV}.tar.gz;name=socket6-perl-${PV}"
SRC_URI[socket6-perl-0.23.md5sum] = "2c02adb13c449d48d232bb704ddbd492"
SRC_URI[socket6-perl-0.23.sha256sum] = "eda753f0197e8c3c8d4ab20a634561ce84011fa51aa5ff40d4dbcb326ace0833"

S = "${WORKDIR}/Socket6-${PV}"

do_configure_prepend () {
	mkdir -p m4

	(
		unset PERL_INC PERL_LIB PERL_ARCHLIB PERHOSTLIB PERL5LIB PERLCONFIGTARGET
                autoconf -Wcross --verbose --force
	)

	sed -i 's:\./configure\(.[^-]\):./configure --build=${BUILD_SYS} --host=${HOST_SYS} --target=${TARGET_SYS} --prefix=${prefix} --exec_prefix=${exec_prefix} --bindir=${bindir} --sbindir=${sbindir} --libexecdir=${libexecdir} --datadir=${datadir} --sysconfdir=${sysconfdir} --sharedstatedir=${sharedstatedir} --localstatedir=${localstatedir} --libdir=${libdir} --includedir=${includedir} --oldincludedir=${oldincludedir} --infodir=${infodir} --mandir=${mandir}\1:' Makefile.PL
}

inherit autotools cpan
