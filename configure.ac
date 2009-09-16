#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ(2.59)
AC_INIT(elito, 0.8.1, [ELiTo Support <elito@sigma-chemnitz.de>])

AC_DEFUN([ELITO_TRUE_FALSE],
[
	if $2; then
		AC_SUBST($1[_FALSE], [\#])
	else
		AC_SUBST($1[_TRUE],  [\#])
	fi
])

_f=$HOME/.bitbake.elito.conf

AC_ARG_WITH([global-bitbake-conf],
	[AS_HELP_STRING([--with-global-bitbake-conf=<file>],
		[global bitbake configuration file (default: ~/.bitbake.elito.conf (when exists))])],
	[case $withval in
		(yes)	withval=yes;;
		(no)	withval=no;;
		(*)	_f=$withval; withval=yes;;
	 esac],
	[ withval=auto ])

case $withval in
	(yes)	if ! test -r "$_f"; then
			AC_MSG_ERROR([can not find global bitbake.conf file '$_f'])
		fi
		;;
	(no)	_f=;;
	(auto)	test -r "$_f" || _f=;;
esac

AC_SUBST([GLOBAL_BITBAKE_CONF],[$_f])
ELITO_TRUE_FALSE([HAVE_GLOBAL_BITBAKE_CONF], [test -n "$_f"])

AC_ARG_WITH([project-name],
	[AS_HELP_STRING([--with-project-name=<name>],
		[project name (default: directory basename)])],
	[PROJECT_NAME=$withval],[PROJECT_NAME=$(basename "`pwd`")])
AC_SUBST(PROJECT_NAME)

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
		       [Enable ccache support (default:detected)]),
	[],
	[enable_ccache=auto])

AC_ARG_WITH(kernel-reference-repo,
	AS_HELP_STRING([--with-kernel-reference-repo=<dir>],
			[Specify location of a local reference git repository which holds kernel sources]),
	[kernel_ref_repo=$withval], [kernel_ref_repo=/usr/share/elito/kernel])
AC_SUBST([kernel_ref_repo])

dnl -----------------

AC_MSG_CHECKING([for development components])
AC_ARG_ENABLE([devel],
	AS_HELP_STRING([--enable-devel=<comp0>...],
		[enable development versions of components within the ../workspace/ directory (default:auto). The list of components  is comma separated.]),
	[enable_devel_components=$enableval],[enable_devel_components=auto])
AC_MSG_RESULT([$enable_devel_components])

AC_CACHE_VAL([ensc_cv_DEVEL_COMPONENTS],[
	old_IFS=$IFS
	IFS=\ ,
	ensc_cv_DEVEL_COMPONENTS=
	for i in $enable_devel_components; do
	    case $i in
	    (auto)
		for p in ../workspace/*; do
		    test -d "$p" || continue
		    p=${p%%.git}
		    p=${p%%,$PROJECT_NAME}
		    p=${p##*/}

		    # ignore components of other projects
		    test x"$p" = x"${p##*,}" || continue
		    ensc_cv_DEVEL_COMPONENTS="$ensc_cv_DEVEL_COMPONENTS $p"
		done
		;;
	    (*)
		ensc_cv_DEVEL_COMPONENTS="$ensc_cv_DEVEL_COMPONENTS$p"
		;;
	    esac
	done
	IFS=$old_IFS])

for i in $ensc_cv_DEVEL_COMPONENTS; do
    AC_MSG_NOTICE([  enabling component '$i'])
done
AC_SUBST([devel_components],[${ensc_cv_DEVEL_COMPONENTS## }])


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


AC_CACHE_VAL([ensc_cv_PROG_WHICH],[
	for i in 'type -p' 'which'; do
		AC_MSG_CHECKING([whether '$i' works])
		tmp=`$i sh`
		if test -x "$tmp"; then
			ensc_cv_PROG_WHICH=$i
			AC_MSG_RESULT([yes])
			break
		fi
		AC_MSG_RESULT([no])
	done

	if ! test -n "$ensc_cv_PROG_WHICH"; then
		AC_MSG_ERROR([could not find working 'which' program])
	fi
])
AC_SUBST([WHICH],[$ensc_cv_PROG_WHICH])

dnl -----------------

AC_ARG_VAR(EXTRA_BBFILES, [Extra bitbake files])
ELITO_TRUE_FALSE(EXTRA_BBFILES, test x"${EXTRA_BBFILES}" != x)

dnl -----------------

AC_DEFUN([ELITO_INIT_VARS],[
	elito_assumed=
	AC_SUBST(elito_assumed)

	: ${NATIVE_LEVEL:=11}
	AC_ARG_VAR([NATIVE_LEVEL], [Level for which tools are staged from scratch for -native components])
])

AC_DEFUN([ELITO_ASSUME],[
	AC_REQUIRE([ELITO_INIT_VARS])

	AC_MSG_CHECKING([whether to enable system version for $2])
	AC_ARG_ENABLE(pkg-$2,
		AS_HELP_STRING([--enable-pkg-$2=<mode>],
			[Set type for package $2. Possible values are 'auto', 'system' or 'builtin'. Default is 'auto']),
		[],[
		if test "$1" -lt "$NATIVE_LEVEL"; then
			enableval=auto;
		else
			enableval=no;
		fi])

	enable_pkg_[]translit($2,-,_)=$enableval
	case $enableval in
	(auto)		force=$3;;
	(no|builtin)	force=false;;
	(yes|system)	force=true;;
	esac

	$force && elito_assumed="$elito_assumed ifelse($4,,$2-native,$4)"

	if $force; then
		AC_MSG_RESULT([yes])
	else
		AC_MSG_RESULT([no])
	fi
])

AC_DEFUN([ELITO_CHECK_PROGRAM],[
	AC_PATH_PROGS($2, $3)
	if test -x "${$2}"; then
		ELITO_ASSUME($1,ifelse($4,,$3,$4),true)
	else
		ELITO_ASSUME($1,ifelse($4,,$3,$4),false)
	fi
])

dnl AC_PATH_PROGS(BITBAKE, bitbake)
BITBAKE=\${abs_top_builddir}/tmp/staging/.bitbake/bin/bitbake
AC_SUBST(BITBAKE)

ELITO_CHECK_PROGRAM( 0, QUILT,      quilt)
ELITO_CHECK_PROGRAM( 0, M4,         m4)
ELITO_CHECK_PROGRAM( 0, BISON,      bison)
ELITO_CHECK_PROGRAM( 5, FLEX,       flex)
ELITO_CHECK_PROGRAM(90, GETTEXT,    gettext)
ELITO_CHECK_PROGRAM( 0, FILE,       file)
ELITO_CHECK_PROGRAM( 0, QEMU,       qemu)
ELITO_CHECK_PROGRAM( 0, FAKEROOT,   fakeroot)
ELITO_CHECK_PROGRAM(90, PKGCONFIG,  pkg-config,  pkgconfig)
ELITO_CHECK_PROGRAM(95, PYTHON,     python)
ELITO_CHECK_PROGRAM(20, XML2CONFIG, xml2-config, libxml2)
ELITO_CHECK_PROGRAM( 0, ZIP,        zip)
ELITO_CHECK_PROGRAM( 0, UNZIP,      unzip)
ELITO_CHECK_PROGRAM(80, CURL,       curl)
ELITO_CHECK_PROGRAM( 0, GIT,        git)
ELITO_CHECK_PROGRAM( 0, HG,         hg)
ELITO_CHECK_PROGRAM(50, PAX,        pax, pax-utils)
ELITO_CHECK_PROGRAM( 0, INSTALL,    install)
ELITO_CHECK_PROGRAM( 0, COREUTILS,  basename, coreutils)
ELITO_CHECK_PROGRAM(60, PERL,       perl)
ELITO_CHECK_PROGRAM(90, AUTOCONF,   autoconf)
ELITO_CHECK_PROGRAM(90, AUTOMAKE,   automake)
ELITO_CHECK_PROGRAM(90, LIBTOOL,    libtoolize,  libtool)
ELITO_CHECK_PROGRAM(60, UIC_QT4,    uic-qt4,     uicmoc4)
ELITO_CHECK_PROGRAM(20, MTD_UTILS,  mkfs.ubifs,  mtd-utils)
ELITO_CHECK_PROGRAM( 0, UNIFDEF,    unifdef)
ELITO_CHECK_PROGRAM( 0, GPERF,      gperf)
ELITO_CHECK_PROGRAM( 5, SHARED_MIME_INFO,   update-mime-database, shared-mime-info)
ELITO_CHECK_PROGRAM( 0, DESKTOP_FILE_UTILS, desktop-file-install, desktop-file-utils)ELITO_CHECK_PROGRAM( 0, MKFONTSCALE, mkfontscale)


AC_CHECK_HEADER(gmp.h,       [ELITO_ASSUME(20, gmp,true)])
AC_CHECK_HEADER(zlib.h,      [ELITO_ASSUME(10, zlib,true)])
AC_CHECK_HEADER(mpfr.h,      [ELITO_ASSUME(20, mpfr,true)])
AC_CHECK_HEADER(ncurses.h,   [ELITO_ASSUME(10, ncurses,true)])
AC_CHECK_HEADER(expat.h,     [ELITO_ASSUME(10, expat,true)])
AC_CHECK_HEADER(popt.h,      [ELITO_ASSUME(10, popt,true)])
AC_CHECK_HEADER(db.h,        [ELITO_ASSUME(10, db,true)])
AC_CHECK_HEADER(lzo/lzo2a.h, [ELITO_ASSUME(10, lzo,true)])

PKG_CHECK_MODULES(GLIB20,    [glib-2.0],    [ELITO_ASSUME(70, glib20,    true, glib-2.0-native)],  [true])
PKG_CHECK_MODULES(DBUS,      [dbus-1],      [ELITO_ASSUME(70, dbus,      true)],      [true])
PKG_CHECK_MODULES(DBUS_GLIB, [dbus-glib-1], [ELITO_ASSUME(70, dbus_glib, true, dbus-glib-native)], [true])
PKG_CHECK_MODULES(ORBIT2,    [ORBit-2.0],   [ELITO_ASSUME(70, orbit2,    true)], [true])

AC_CONFIG_FILES([Makefile
		set-env.in
		conf/local.conf.in])
AC_CONFIG_FILES(bitbake,    [chmod a+x bitbake])
AC_CONFIG_FILES(bg-bitbake, [chmod a+x,a-w bg-bitbake])

AC_CONFIG_COMMANDS([config], [make -s config])
AC_CONFIG_COMMANDS([fixperm], [chmod a-w $CONFIG_FILES])

AC_OUTPUT()
