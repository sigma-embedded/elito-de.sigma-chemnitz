PATCHDATE = "20100306"
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
	ncurses \
	ncurses-static \
	ncurses-terminfo \
"
RSUGGESTS_${PN} = "ncurses-terminfo"

inherit autotools_stage

# This keeps only tput/tset in ncurses
# clear/reset are in already busybox
FILES_ncurses-tools    = "${bindir}/tic ${bindir}/toe ${bindir}/infotocap ${bindir}/captoinfo ${bindir}/infocmp ${bindir}/clear.${PN} ${bindir}/reset.${PN} ${bindir}/tack ${bindir}/tabs"
FILES_ncurses-terminfo = "${datadir}/terminfo"
FILES_${PN} = "${bindir}/tput ${bindir}/tset ${datadir}/tabset ${sysconfdir}/terminfo"

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
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20100123.patch.gz;patch=1	\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20100130.patch.gz;patch=1	\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20100206.patch.gz;patch=1	\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20100213.patch.gz;patch=1	\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20100220.patch.gz;patch=1	\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20100227.patch.gz;patch=1	\
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

        for i in \
	'narrowc --with-ticlib' \
	'widec   --enable-widec --without-progs'; do
                set -- $i
		mkdir -p $1
		cd $1
                shift
		oe_runconf \
			--disable-static \
			--without-debug							\
			--without-ada							\
			--enable-hard-tabs \
			--enable-xmc-glitch \
			--enable-colorfgbg \
			--with-termpath='${sysconfdir}/termcap:${datadir}/misc/termcap'	\
			--with-terminfo-dirs=${sysconfdir}/terminfo:${datadir}/terminfo \
			--with-shared							\
			--disable-big-core						\
			--program-prefix=						\
			--with-termlib=tinfo						\
			--enable-sigwinch						\
			--enable-pc-files						\
			--with-build-cc="${BUILD_CC}" \
			--with-build-cpp="${BUILD_CPP}" \
			--with-build-ld="${BUILD_LD}" \
			--with-build-cflags="${BUILD_CFLAGS}" \
			--with-build-cppflags='${BUILD_CPPFLAGS} -D_GNU_SOURCE' \
			--with-build-ldflags='${BUILD_LDFLAGS}' \
			"$@"
                cd ..
	done
}

do_compile() {
	oe_runmake -C narrowc libs
	oe_runmake -C narrowc/progs

	oe_runmake -C widec libs
}

do_install() {
	cd widec
        oe_runmake 'DESTDIR=${D}' install.libs install.includes install.man

        cd ../narrowc
        oe_runmake 'DESTDIR=${D}' install.libs install.progs install.data

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

python populate_packages_prepend () {
	libdir = bb.data.expand("${libdir}", d)
        do_split_packages(d, libdir, '^lib(.*)\.so\..*', 'ncurses-lib%s', 'ncurses %s library', prepend=True, extra_depends = '', allow_links=True)
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
