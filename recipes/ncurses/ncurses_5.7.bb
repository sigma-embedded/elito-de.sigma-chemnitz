PATCHDATE = "20090718"
PR = "r0.${PATCHDATE}"

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

inherit autotools

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
	http://invisible-island.net/datafiles/release/my-autoconf.tar.gz			\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20081115.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20081122.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20081129.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20081206.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20081213.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20081220.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20081227.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090103.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090104.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090105.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090110.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090117.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090124.patch.gz;patch=1		\
	file://tic-hang.patch;patch=1	\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090207.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090214.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090221.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090228.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090314.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090321.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090328.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090404.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090411.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090418.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090419.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090425.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090502.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090510.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090516.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090523.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090530.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090606.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090607.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090613.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090627.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090704.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20090711.patch.gz;patch=1		\
\
\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-${PATCHDATE}.patch.gz;patch=1	\
"

do_configure_prepend() {
	rm -rf tack
}

EXTRA_AUTORECONF="-I m4"
do_configure() {
	oe_runconf \ 
		--without-normal						\
		--without-debug							\
		--without-ada							\
		--with-termpath='${sysconfdir}/termcap:${datadir}/misc/termcap'	\
		--with-terminfo-dirs=${sysconfdir}/terminfo:${datadir}/terminfo \
		--with-shared							\
		--disable-big-core						\
		--program-prefix=						\
		--with-termlib=tinfo						\
		--disable-widec							\
		--enable-sigwinch						\
		--with-build-cflags=""						\
		--with-build-cppflags='-D__need_wint_t'
}

do_stage() {
	autotools_stage_all
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
