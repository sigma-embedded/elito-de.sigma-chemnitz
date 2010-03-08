PATCHDATE = "20100123"
PR = "r3.${PATCHDATE}"

DESCRIPTION = "Ncurses library"
HOMEPAGE = "http://www.gnu.org/software/ncurses/ncurses.html"
LICENSE = "MIT"
SECTION = "libs"
DEPENDS = "ncurses-native"
PACKAGES = " \
	ncurses-dbg \
	ncurses-dev \
	ncurses-doc \
	ncurses-tools \
	ncurses-libtinfo \
	ncurses-libpanel \
	ncurses-libform \
	ncurses-libmenu \
	ncurses \
	ncurses-terminfo \
"
RSUGGESTS_${PN} = "ncurses-terminfo"

inherit autotools_stage

# This keeps only tput/tset in ncurses
# clear/reset are in already busybox
FILES_ncurses_append   = " ${datadir}/tabset"
FILES_ncurses-tools    = "${bindir}/tic ${bindir}/toe ${bindir}/infotocap ${bindir}/captoinfo ${bindir}/infocmp ${bindir}/clear.${PN} ${bindir}/reset.${PN} ${bindir}/tack "
FILES_ncurses-terminfo = "${datadir}/terminfo"
FILES_ncurses-libtinfo = "${libdir}/libtinfo.so.*"
FILES_ncurses-libpanel = "${libdir}/libpanel.so.*"
FILES_ncurses-libform  = "${libdir}/libform.so.*"
FILES_ncurses-libmenu  = "${libdir}/libmenu.so.*"
FILES_${PN} = "${bindir}/tput ${bindir}/tset ${libdir}/lib*.so.* usr/share/tabset etc/terminfo"

SRC_URI = "${GNU_MIRROR}/ncurses/ncurses-${PV}.tar.gz \
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20091107-patch.sh.bz2;patch=1	\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20091114.patch.gz;patch=1	\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20091121.patch.gz;patch=1	\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20091128.patch.gz;patch=1	\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20091205.patch.gz;patch=1	\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20091212.patch.gz;patch=1	\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20091219.patch.gz;patch=1	\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20091226.patch.gz;patch=1	\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20091227.patch.gz;patch=1	\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20100102.patch.gz;patch=1	\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20100109.patch.gz;patch=1	\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20100116.patch.gz;patch=1	\
	\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-${PATCHDATE}.patch.gz;patch=1	\
	file://tic-hang.patch;patch=1 \
"

do_configure_prepend() {
	rm -rf tack
}

PARALLEL_MAKE = ""
EXTRA_AUTORECONF="-I m4"

do_configure() {
        sed -i -s 's!^\(PKG_CONFIG_LIBDIR.*=\).*!\1 /usr/lib/pkgconfig!g' misc/Makefile.in

	oe_runconf \
		--without-normal						\
		--without-debug							\
		--without-ada							\
		--with-ospeed=unsigned						\
		--enable-hard-tabs --enable-xmc-glitch --enable-colorfgbg	\
		--with-chtype=long						\
		--with-termpath='${sysconfdir}/termcap:${datadir}/misc/termcap'	\
		--with-terminfo-dirs=${sysconfdir}/terminfo:${datadir}/terminfo \
		--with-shared							\
		--disable-big-core						\
		--program-prefix=						\
		--with-termlib=tinfo						\
		--disable-widec							\
		--enable-sigwinch						\
		--enable-pc-files						\
		--with-build-cflags=""						\
		--with-build-cppflags='-D__need_wint_t'
}

do_install() {
	autotools_do_install

	# include some basic terminfo files
	# stolen ;) from gentoo and modified a bit
	for x in ansi console dumb linux rxvt screen sun vt{52,100,102,200,220} xterm-color xterm-xfree86
	do
		local termfile="$(find "${D}${datadir}/terminfo/" -name "${x}" 2>/dev/null)"
		local basedir="$(basename $(dirname "${termfile}"))"

		if [ -n "${termfile}" ]
		then
			install -d ${D}${sysconfdir}/terminfo/${basedir}
			mv ${termfile} ${D}${sysconfdir}/terminfo/${basedir}/
			ln -s /etc/terminfo/${basedir}/${x} \
				${D}${datadir}/terminfo/${basedir}/${x}
		fi
	done
	# i think we can use xterm-color as default xterm
	if [ -e ${D}${sysconfdir}/terminfo/x/xterm-color ]
	then
		ln -sf xterm-color ${D}${sysconfdir}/terminfo/x/xterm
	fi

	if [ "${PN}" = "ncurses" ]; then
		mv ${D}${bindir}/clear ${D}${bindir}/clear.${PN}
		mv ${D}${bindir}/reset ${D}${bindir}/reset.${PN}
	fi
}


pkg_postinst_ncurses-tools () {
	if [ "${PN}" = "ncurses" ]; then
		update-alternatives --install ${bindir}/clear clear clear.${PN} 100
		update-alternatives --install ${bindir}/reset reset reset.${PN} 100
	fi
}


pkg_prerm_ncurses-tools () {
	if [ "${PN}" = "ncurses" ]; then
		update-alternatives --remove clear clear.${PN}
		update-alternatives --remove reset reset.${PN}
	fi
}
