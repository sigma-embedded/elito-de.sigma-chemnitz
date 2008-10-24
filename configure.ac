#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ(2.61)
AC_INIT(elito, 0.8, [ELiTo Support <elito@sigma-chemnitz.de>])

AC_DEFUN([ELITO_TRUE_FALSE],
[
	if $2; then
		AC_SUBST($1[_FALSE], [\#])
	else
		AC_SUBST($1[_TRUE],  [\#])
	fi
])

AC_ARG_ENABLE(maintainer-mode,
	AS_HELP_STRING([--enable-maintainer-mode],
		       [Recreate buildsystem automatically]),
	[],[enable_maintainer_mode=no])

ELITO_TRUE_FALSE(ENABLE_MAINTAINER_MODE, test x"${enable_maintainer_mode}" != xno)

AC_ARG_ENABLE(distro,
	AS_HELP_STRING([--enable-distro=<type>],
		       [Distribution type; valid values are 'debug' and 'release' (default: 'debug')]),
	[DISTRO_TYPE=$enableval], [DISTRO_TYPE=debug])
AC_SUBST(DISTRO_TYPE)

AC_ARG_ENABLE(machine,
	AS_HELP_STRING([--enable-machine=<machine>],
		[(mandatory) use configuration for given machine; possible values are 'toradex-colibri', 'toradex-colibri320', 'phytec-pcm027' and 'phytec-pcm034']),
	[MACHINE=$enableval], [AC_MSG_ERROR([No machine specified])])
AC_SUBST(MACHINE)

AC_ARG_WITH(cache-dir,
	AS_HELP_STRING([--with-cache-dir=<dir>],
		       [Directory where to place e.g. downloaded sources or ccache data.
		        This can be shared between multiple projects. Default: ../cache]),
	[CACHE_DIR=$withval], [CACHE_DIR=\${abs_top_builddir}/../cache])
AC_SUBST(_CACHE_DIR, [$CACHE_DIR])

AC_ARG_ENABLE(ccache,
	AS_HELP_STRING([--enable-ccache],
		       [Enable ccache support (default:detected)],
	[],
	[enable_ccache=auto]))

AC_ARG_WITH(kernel-repo,
	AS_HELP_STRING([--with-kernel-repo=<dir>],
			[Specify location of the upstream ELiTo kernel git repository. Default: git://www.sigma-chemnitz.de/elito/kernel]),
	[kernel_repo=$withval], [kernel_repo=git://www.sigma-chemnitz.de/elito/kernel])
AC_SUBST([kernel_repo])

AC_ARG_WITH(kernel-reference-repo,
	AS_HELP_STRING([--with-kernel-reference-repo=<dir>],
			[Specify location of a local reference git repository which holds kernel sources]),
	[kernel_ref_repo=$withval], [kernel_ref_repo=/usr/share/elito/kernel])
AC_SUBST([kernel_ref_repo])

dnl -----------------

AC_ARG_ENABLE(ipkg,
	AS_HELP_STRING([--disable-ipkg],
		[disable build of 'ipkg' packages]),
	[enable_ipkg=$enableval], [enable_ipkg=yes])
ELITO_TRUE_FALSE(IPKG, test x"$enable_ipkg" != xno)

AC_ARG_ENABLE(tar,
	AS_HELP_STRING([--enable-tar],
		[enable build of 'tar' packages]),
	[enable_tar=$enableval], [enable_tar=no])
ELITO_TRUE_FALSE(TAR, test x"$enable_tar" != xno)



AC_ARG_ENABLE(nfs-root,
	AS_HELP_STRING([--enable-nfs-root=<dir>],
		[install built packages into <dir> which becomes suitable for exporting it as an NFS root filesystem]),
	[nfs_rootdir=$enableval], [nfs_rootdir=])
AC_SUBST(nfs_rootdir)
ELITO_TRUE_FALSE(NFSROOT, test x"${nfs_rootdir}" != x)

dnl -----------------

AC_ARG_VAR(EXTRA_BBFILES, [Extra bitbake files])
ELITO_TRUE_FALSE(EXTRA_BBFILES, test x"${EXTRA_BBFILES}" != x)

dnl -----------------

AC_DEFUN([ELITO_INIT_VARS],[
	elito_assumed=
	AC_SUBST(elito_assumed)
])

AC_DEFUN([ELITO_ASSUME],[
	AC_REQUIRE([ELITO_INIT_VARS])

	AC_ARG_ENABLE(pkg-$1,
		AS_HELP_STRING([--enable-pkg-$1=<mode>],
			[Set type for package $1. Possible values are 'auto', 'system' or 'builtin'. Default is 'auto']),
		[], [enable_pkg_$1=auto])

	force=$2
	case $enable_pkg_$1 in
	(auto)	;;
	(no)	force=false;;
	(yes)	force=true;;
	esac
	$force && elito_assumed="$elito_assumed ifelse($3,,$1-native,$3)"
])


	

AC_DEFUN([ELITO_CHECK_PROGRAM],[
	AC_PATH_PROGS($1, $2)
	if test -x "${$1}"; then
		ELITO_ASSUME(ifelse($3,,$2,$3),true)
	else
		ELITO_ASSUME(ifelse($3,,$2,$3),false)
	fi
])

AC_PATH_PROGS(BITBAKE, bitbake)
ELITO_CHECK_PROGRAM(QUILT,      quilt)
ELITO_CHECK_PROGRAM(M4,         m4)
ELITO_CHECK_PROGRAM(BISON,      bison)
ELITO_CHECK_PROGRAM(FLEX,       flex)
dnl ELITO_CHECK_PROGRAM(GETTEXT,    gettext)
ELITO_CHECK_PROGRAM(FILE,       file)
ELITO_CHECK_PROGRAM(QEMU,       qemu)
ELITO_CHECK_PROGRAM(FAKEROOT,   fakeroot)
dnl ELITO_CHECK_PROGRAM(PKGCONFIG,  pkg-config,  pkgconfig)
ELITO_CHECK_PROGRAM(PYTHON,     python)
ELITO_CHECK_PROGRAM(XML2CONFIG, xml2-config, libxml2)
ELITO_CHECK_PROGRAM(ZIP,        zip)
dnl ELITO_CHECK_PROGRAM(CURL,       curl)
ELITO_CHECK_PROGRAM(GIT,        git)
dnl ELITO_CHECK_PROGRAM(AUTOCONF,   autoconf)
dnl ELITO_CHECK_PROGRAM(AUTOMAKE,   automake)
dnl ELITO_CHECK_PROGRAM(LIBTOOL,    libtoolize,  libtool)

AC_CHECK_HEADER(gmp.h,       [ELITO_ASSUME(gmp,true)])
AC_CHECK_HEADER(zlib.h,      [ELITO_ASSUME(zlib,true)])
AC_CHECK_HEADER(mpfr.h,      [ELITO_ASSUME(mpfr,true)])
AC_CHECK_HEADER(ncurses.h,   [ELITO_ASSUME(ncurses,true)])
AC_CHECK_HEADER(expat.h,     [ELITO_ASSUME(expat,true)])
AC_CHECK_HEADER(popt.h,      [ELITO_ASSUME(popt,true)])
AC_CHECK_HEADER(db.h,        [ELITO_ASSUME(db,true)])
AC_CHECK_HEADER(lzo/lzo2a.h, [ELITO_ASSUME(lzo,true)])

dnl PKG_CHECK_MODULES(GLIB20,    [glib-2.0],    [ELITO_ASSUME(glib20,    true, glib-2.0-native)],  [true])
dnl PKG_CHECK_MODULES(DBUS,      [dbus-1],      [ELITO_ASSUME(dbus,      true)],      [true])
dnl PKG_CHECK_MODULES(DBUS_GLIB, [dbus-glib-1], [ELITO_ASSUME(dbus_glib, true, dbus-glib-native)], [true])
dnl PKG_CHECK_MODULES(ORBIT2,    [ORBit-2.0],   [ELITO_ASSUME(orbit2,    true)], [true])

AC_CONFIG_FILES([Makefile
		set-env.in
		conf/local.conf.in])
AC_CONFIG_FILES(bitbake, [chmod +x bitbake])

AC_CONFIG_COMMANDS([config], [make -s config])

AC_OUTPUT()
