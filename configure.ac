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

AC_ARG_WITH([local-bitbake-conf],
	[AS_HELP_STRING([--with-local-bitbake-conf=<file>],
		[local bitbake configuration file (default: conf/local-<domain>.conf (when exists))])],
	[case $withval in
		(yes)	withval=yes;;
		(no)	withval=no;;
		(*)	_f=$withval; withval=yes;;
	 esac],
	[ withval=auto ])

case $withval in
	(yes)	if ! test -r "$_f"; then
			AC_MSG_ERROR([can not find local bitbake.conf file '$_f'])
		fi
		;;
	(no)	_f=;;
	(auto)	_d=`hostname -f`
		_f=
		while test -n "$_d"; do
		      _f=conf/local-"$_d".conf
		      ! test -r "$_f" || break
		      if test x"$_d" = x"${_d#*.}"; then
			 _d=
			 _f=
		      else
			 _d=${_d#*.}
		      fi
		done
		;;
esac

AC_SUBST([LOCAL_BITBAKE_CONF],[$_f])
ELITO_TRUE_FALSE([HAVE_LOCAL_BITBAKE_CONF], [test -n "$_f"])

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

AC_MSG_CHECKING([whether 'sh' is 'bash'])
if env -u BASH sh -c 'test "x${BASH:+set}" = xset'; then
   AC_MSG_RESULT([yes])
else
   s=`readlink /bin/sh`
   AC_MSG_RESULT([no ($s)])
   AC_MSG_ERROR([
************************************************************************
Buildsystem assumes that '/bin/sh' is a bash.  This might cause
various subtle build problems and is not supported hence.
************************************************************************])
fi

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

AC_MSG_CHECKING([whether to enable offline mode])
AC_ARG_ENABLE(offline-mode,
	AS_HELP_STRING([--enable-offline-mode],
		[offline mode; use locally cached sources instead of fetching them from network]),
	[case $enableval in
	      yes)	ELITO_OFFLINE=true;;
	      no)	;;
	      *)	AC_MSG_ERROR([invalid value for --enable-offline-mode]);;
         esac],[
	ELITO_OFFLINE=])

if test -n "$ELITO_OFFLINE"; then
   AC_MSG_RESULT([yes])
else
   AC_MSG_RESULT([no])
fi
AC_SUBST([ELITO_OFFLINE])

AC_ARG_WITH(cache-dir,
	AS_HELP_STRING([--with-cache-dir=<dir>],
		       [Directory where to place downloaded sources. This
		       can be shared between multiple projects. Default:
		       ../cache]),
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

AC_ARG_WITH(secwrap,
	AS_HELP_STRING([--with-secwrap=<dir>],
		       [Wrapper command around 'bitbake' which changes e.g. user or SELinux context. Default: none]),
	[SECWRAP_CMD=$withval], [SECWRAP_CMD=])
AC_SUBST(SECWRAP_CMD, [$SECWRAP_CMD])

AC_MSG_CHECKING([whether security wrapper works])
if test -n "$SECWRAP_CMD" && ! eval $SECWRAP_CMD true </dev/null >/dev/null; then
   AC_MSG_RESULT([no])
   AC_MSG_ERROR([
************************************************************************
Security wrapper '$SECWRAP_CMD' seems to be non functional.
************************************************************************])
else
  AC_MSG_RESULT([yes])
fi

_default_layers='org.openembedded.core/meta org.openembedded.meta/meta-oe'

AC_ARG_ENABLE(layers,
	AS_HELP_STRING([--enable-layers=<dir>[,<dir>]*],
		       [enable additional layers]),
        [],[enable_layers=DEFAULT])

old_IFS=$IFS
IFS=,
set -- $enable_layers
IFS=$old_IFS

layer_str=''
for i; do
    case x$i in
    (xDEFAULT)
        for j in $_default_layers; do
	    AC_MSG_CHECKING([for extra layers])
	    layer_str="${layer_str}\${ELITO_ROOTDIR}/$j "
	    AC_MSG_RESULT([$j (default)])
	done
	;;

    (x*)
        AC_MSG_CHECKING([for extra layers])
	layer_str="${layer_str}\${ELITO_ROOTDIR}/$i "
        AC_MSG_RESULT([$i])
	;;
    esac
done

AC_SUBST([LAYER_DEFINITIONS],[$layer_str])

dnl -----------------

AC_PATH_PROGS([PYTHON], python)

AC_MSG_CHECKING([for bitbake version])
AC_ARG_WITH([bitbake-version],
	AS_HELP_STRING([--with-bitbake-version],
		[bitbake version which shall be used; valid values are 1.12 (default: auto)]),
	[], [with_bitbake_version=auto])

case $with_bitbake_version in
    (auto)
	AC_CACHE_VAL([ensc_cv_BITBAKE_VERSION],[
		$PYTHON -c 'import sys; sys.exit(sys.hexversion >= 0x2060000)'
		case $? in
		     (0)	ensc_cv_BITBAKE_VERSION=1.8;;
		     (*)	ensc_cv_BITBAKE_VERSION=master;;
		esac
		])
	with_bitbake_version=$ensc_cv_BITBAKE_VERSION
	;;
esac
AC_MSG_RESULT([$with_bitbake_version])
AC_SUBST([BITBAKE_BRANCH],[$with_bitbake_version])

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


AC_MSG_CHECKING([whether to enable an NFS root directory])
AC_ARG_ENABLE(nfs-root,
	AS_HELP_STRING([--enable-nfs-root=<dir>],
		[install built packages into <dir> which becomes suitable for exporting it as an NFS root filesystem]),
	[nfs_rootdir=$enableval
	AC_MSG_RESULT([yes (${nfs_rootdir})])],
	[nfs_rootdir=,
	AC_MSG_RESULT([no])])
AC_SUBST(nfs_rootdir)
ELITO_TRUE_FALSE(NFSROOT, test x"${nfs_rootdir}" != x)


_tftpserver_auto=false
AC_MSG_CHECKING([for tftp server])
AC_ARG_WITH(tftp-server,
	AS_HELP_STRING([--with-tftp-server=<ip>],
		[use <ip> as the tftp server (default:`hostname`)]),
	[tftpserver=$withval], [
	tftpserver=`hostname -i`
	_tftpserver_auto=true
	])
AC_MSG_RESULT([$tftpserver])

if $_tftpserver_auto && test "127.0.0.1" = "$tftpserver"; then
   AC_MSG_ERROR([
************************************************************************
A non public IP has been detected for the TFTP which is usually
unwanted.

Please add a '--with-tftp-server' option in global .config or project
specific build-setup configuration.  Alternatively, edit /etc/hosts so
that your hostname will not be assigned to the 127.0.0.1 ip.
************************************************************************
])
fi

AC_SUBST([TFTP_SERVER],[$tftpserver])


AC_MSG_CHECKING([for tftp root directory])
AC_ARG_WITH(tftpboot-dir,
	AS_HELP_STRING([--with-tftpboot-dir=<dir>],
		[use <dir> as the tftp root dir]),
	[tftpbootdir=$withval], [
	tftpbootdir=/var/lib/tftpboot
	test -d "$tftpbootdir" || tftpbootdir=/tftpboot
	])
AC_MSG_RESULT([$tftpbootdir])
AC_SUBST(tftpbootdir)


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

PKG_PROG_PKG_CONFIG()
AC_MSG_CHECKING([for native pkg-config path])
AC_CACHE_VAL([ensc_cv_PKGCONFIG_PATHS],[
	paths=$(env LANG=C ${PKG_CONFIG} --debug 2>&1 | \
                sed "/^Scanning directory/s|.*'\([[^']]*\)'|\1|p;d")
	ensc_cv_PKGCONFIG_PATHS=
	for i in $paths; do
	    ensc_cv_PKGCONFIG_PATHS=$ensc_cv_PKGCONFIG_PATHS:$i
	done
])
AC_MSG_RESULT([$ensc_cv_PKGCONFIG_PATHS])
AC_SUBST([EXTRA_NATIVE_PKGCONFIG_PATH],[$ensc_cv_PKGCONFIG_PATHS])

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

	case $enableval in
	(auto)		force=$3;;
	(no|builtin)	force=false;;
	(yes|system)	force=true;;
	esac

	enable_pkg_[]translit($2,-,_)=$force
	$force && elito_assumed="$elito_assumed ^ifelse($4,,$2-native,$4)\$"

	if $force; then
		AC_MSG_RESULT([yes])
	else
		AC_MSG_RESULT([no])
	fi
])

AC_DEFUN([ELITO_NUMERIC_VERSION],[
	verinfo=`$3` 2>/dev/null

	old_IFS=$IFS
	IFS=.
	set -- ${verinfo} 0 0 0
	IFS=$old_IFS

	$1=$(( m4_for([pos],1,$2,,dnl
               [(${pos} * m4_eval(1 << (8 * ($2-pos)))) + ]) 0 )) dnl
           2>/dev/null || [$1]=
])

AC_DEFUN([ELITO_VERSION_TO_STRING],[dnl
m4_for([pos],1,ifelse([$2],,3,[$2]),,[dnl
ifelse(pos,1,,.)[]m4_eval(([$1] >> ((ifelse([$2],,3,[$2]) - pos) * 8)) & 0xff)dnl
])])

AC_DEFUN([ELITO_VERSION_CHECK],[
	ELITO_NUMERIC_VERSION([_cver],$2,[$3])
	test -n "$_cver" || _elito_bad_ver=true

	if ${_elito_bad_ver}; then
	   AC_MSG_RESULT([unknown ($verinfo)])
	elif test "$_cver" -ge m4_eval([$1]); then
	   AC_MSG_RESULT([ok ($verinfo)])
	else
	   AC_MSG_RESULT([too old ($verinfo)])
	   _elito_bad_ver=true
	fi
])

AC_DEFUN([_flex_check_version],[
	AC_MSG_CHECKING([for flex version >= ELITO_VERSION_TO_STRING($2)])

	ELITO_VERSION_CHECK([$2],3,
			    [${$1} --version | sed '1s/.* \([[[0-9.]]]\+\).*/\1/p;d'])
])

AC_DEFUN([_m4_check_version],[
	AC_MSG_CHECKING([for m4 version >= ELITO_VERSION_TO_STRING($2)])

	ELITO_VERSION_CHECK([$2],3,
			    [${$1} --version | sed '1s/.* \([[[0-9.]]]\+\).*/\1/p;d'])
])

AC_DEFUN([_cmake_check_version],[
	AC_MSG_CHECKING([for cmake version >= ELITO_VERSION_TO_STRING($2)])

	ELITO_VERSION_CHECK([$2],3,
			    [${$1} --version |
                             sed '1s/.* version \([[[0-9.]]]\+\).*/\1/p;d'])
])

AC_DEFUN([ELITO_CHECK_PROGRAM],[
	AC_PATH_PROGS([$2], [$3])

	m4_if(m4_cmp($1,0),-1,[
		test -x "${$2}" || \
		AC_MSG_ERROR([The `$3' program is required for bootstrapping])]
	)

	_elito_bad_ver=false
	if test -x "${$2}"; then
		ifelse([$5],,:,$5([$2],$6))
	fi

	if ! $_elito_bad_ver && test -x "${$2}"; then
		ELITO_ASSUME([$1],ifelse([$4],,$3,$4),true)
	else
		ELITO_ASSUME([$1],ifelse([$4],,$3,$4),false)
	fi
])

AC_PATH_PROGS([AUTORECONF], autoreconf)

dnl AC_PATH_PROGS(BITBAKE, bitbake)
BITBAKE=\${abs_top_builddir}/.tmp/staging/bin/bitbake
BITBAKE_PATH=\${abs_top_builddir}/.tmp/staging/lib
AC_SUBST(BITBAKE)
AC_SUBST(BITBAKE_PATH)
AC_PROG_EGREP

AC_PATH_PROGS([AS], [as], [])
if test -z "$AS"; then
  AC_MSG_ERROR([The `as' program is required for bootstrapping])
fi

ELITO_CHECK_PROGRAM( 0, QUILT,      quilt)
ELITO_CHECK_PROGRAM(-1, GZIP,       gzip)
ELITO_CHECK_PROGRAM(-1, SED,        sed)
ELITO_CHECK_PROGRAM(-1, WGET,       wget)
ELITO_CHECK_PROGRAM( 0, M4,         m4,[],[_m4_check_version], 0x010410)
ELITO_CHECK_PROGRAM( 0, BISON,      bison)
ELITO_CHECK_PROGRAM( 5, FLEX,       flex,[],[_flex_check_version], 0x02051f)
ELITO_CHECK_PROGRAM(90, GETTEXT,    gettext)
ELITO_CHECK_PROGRAM( 0, FILE,       file)
ELITO_CHECK_PROGRAM(-1, TAR,        tar)
ELITO_CHECK_PROGRAM(-1, BZIP2,      bzip2)
ELITO_CHECK_PROGRAM(-1, XZ,         xz)
ELITO_CHECK_PROGRAM(-1, MAKE,       make)
ELITO_CHECK_PROGRAM(-1, GCC,        gcc)
ELITO_CHECK_PROGRAM( 0, QEMU,       qemu)
ELITO_CHECK_PROGRAM( 0, FAKEROOT,   fakeroot)
ELITO_CHECK_PROGRAM(90, PKGCONFIG,  pkg-config,  pkgconfig)
ELITO_CHECK_PROGRAM(95, PYTHON,     python)
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
ELITO_CHECK_PROGRAM( 0, INTLTOOL,   intltool)
ELITO_CHECK_PROGRAM(90, LIBTOOL,    libtoolize,  libtool)
ELITO_CHECK_PROGRAM(60, UIC_QT4,    uic-qt4,     uicmoc4)
ELITO_CHECK_PROGRAM(20, MTD_UTILS,  mkfs.ubifs,  mtd-utils)
ELITO_CHECK_PROGRAM( 0, UNIFDEF,    unifdef)
ELITO_CHECK_PROGRAM( 0, GPERF,      gperf)
ELITO_CHECK_PROGRAM( 5, SHARED_MIME_INFO,   update-mime-database, shared-mime-info)
ELITO_CHECK_PROGRAM( 0, DESKTOP_FILE_UTILS, desktop-file-install, desktop-file-utils)
ELITO_CHECK_PROGRAM( 0, MKFONTSCALE, mkfontscale)
ELITO_CHECK_PROGRAM( 0, DOCBOOK_UTILS, docbook2man, docbook-utils)
ELITO_CHECK_PROGRAM( 0, CMAKE,      cmake, [], [_cmake_check_version], [0x020800])
ELITO_CHECK_PROGRAM( 0, TCL,        tclsh, tcl)
ELITO_CHECK_PROGRAM( 0, TK,         witch, tk)
ELITO_CHECK_PROGRAM( 0, ANT,        ant, ant)
ELITO_CHECK_PROGRAM( 0, JAVAC,      javac)
ELITO_CHECK_PROGRAM( 0, HELP2MAN,   help2man)
ELITO_CHECK_PROGRAM( 0, MAKEDEPEND, makedepend)
ELITO_CHECK_PROGRAM( 0, IMAKE,      imake)
ELITO_CHECK_PROGRAM(20, RPM,        rpm)
ELITO_CHECK_PROGRAM( 0, FONTFORGE,  fontforge)
ELITO_CHECK_PROGRAM( 0, DOXYGEN,    doxygen)

AC_CHECK_HEADER(gmp.h,       [ELITO_ASSUME(20, gmp,true)])
AC_CHECK_HEADER(zlib.h,      [ELITO_ASSUME(10, zlib,true)])
AC_CHECK_HEADER(mpfr.h,      [ELITO_ASSUME(20, mpfr,true)])
AC_CHECK_HEADER(ncurses.h,   [ELITO_ASSUME(10, ncurses,true)])
AC_CHECK_HEADER(expat.h,     [ELITO_ASSUME(10, expat,true)])
AC_CHECK_HEADER(lzo/lzo2a.h, [ELITO_ASSUME(10, lzo2,true)])
AC_CHECK_HEADER(lzo/lzo1.h,  [ELITO_ASSUME(10, lzo,true)])
AC_CHECK_HEADER(bzlib.h,     [ELITO_ASSUME(0,  bzip2-full,true)])
AC_CHECK_HEADER(readline/readline.h, [ELITO_ASSUME(10, readline,true)])
AC_CHECK_HEADER(linux/version.h, [ELITO_ASSUME(0,  linux-libc-headers,true)])

if test x$enable_pkg_rpm = xyes; then
  AC_CHECK_HEADER(popt.h,    [ELITO_ASSUME(10, popt,true)])
  AC_CHECK_HEADER(db.h,      [ELITO_ASSUME(10, db,true)])
fi

AC_CHECK_LIB(iberty, [cplus_demangle], [ELITO_ASSUME(0, binutils, true)])

PKG_CHECK_MODULES(GLIB20,    [glib-2.0],    [ELITO_ASSUME(70, glib20,    true, glib-2.0-native)],  [true])
PKG_CHECK_MODULES(DBUS,      [dbus-1],      [ELITO_ASSUME(70, dbus,      true)],      [true])
PKG_CHECK_MODULES(DBUS_GLIB, [dbus-glib-1], [ELITO_ASSUME(70, dbus_glib, true, dbus-glib-native)], [true])
PKG_CHECK_MODULES(ORBIT2,    [ORBit-2.0],   [ELITO_ASSUME(70, orbit2,    true)], [true])
PKG_CHECK_MODULES(SQLITE3,   [sqlite3 > 3.6],    [ELITO_ASSUME(50, sqlite3,   true)], [true])
PKG_CHECK_MODULES(LIBXSLT,   [libxslt > 1.1.20], [ELITO_ASSUME(10, libxslt,   true)], [true])
PKG_CHECK_MODULES(LIBXML2,   [libxml-2.0 > 2.7], [ELITO_ASSUME(10, libxml2,   true)], [true])
PKG_CHECK_MODULES(OPENSSL,   [openssl libssl libcrypto],
		  [ELITO_ASSUME(0, openssl, true)], [true])

PKG_CHECK_MODULES(UUID,      [uuid >= 2.18], [
  ELITO_CHECK_PROGRAM( 0, UTIL_LINUX, mount, util-linux)
  ELITO_CHECK_PROGRAM( 0, UTIL_LINUX, mount, util-linux-ng)], [true])

AC_CONFIG_FILES([Makefile
		set-env.in
		conf/bblayers.conf.in
		conf/layer.conf
		conf/local.conf.in])
AC_CONFIG_FILES(bitbake,    [chmod a+x bitbake])

AC_CONFIG_COMMANDS([config],  [make -s config])
AC_CONFIG_COMMANDS([fixperm], [chmod a-w \
     Makefile set-env.in conf/local.conf.in conf/bblayers.conf conf/layer.conf bitbake])

AC_OUTPUT()
