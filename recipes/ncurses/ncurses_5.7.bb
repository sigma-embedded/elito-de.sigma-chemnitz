PATCHDATE = "20100501"
PR = "r7.${PATCHDATE}"

DESCRIPTION = "Ncurses library"
HOMEPAGE = "http://www.gnu.org/software/ncurses/ncurses.html"
LICENSE = "MIT"
SECTION = "libs"
DEPENDS = "ncurses-native"
DEPENDS_virtclass-native = ""
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

inherit autotools_stage binconfig

BBCLASSEXTEND = "native sdk"

# This keeps only tput/tset in ncurses
# clear/reset are in already busybox
FILES_ncurses-tools    = "${bindir}/tic ${bindir}/toe ${bindir}/infotocap ${bindir}/captoinfo ${bindir}/infocmp ${bindir}/clear.${PN} ${bindir}/reset.${PN} ${bindir}/tack ${bindir}/tabs"
FILES_ncurses-terminfo = "${datadir}/terminfo"
FILES_${PN} = "${bindir}/tput ${bindir}/tset ${datadir}/tabset ${sysconfdir}/terminfo"

SRC_URI = "${GNU_MIRROR}/ncurses/ncurses-${PV}.tar.gz;name=tarball \
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-20100424-patch.sh.bz2;apply=yes;name=p20100424sh	\
	\
	ftp://invisible-island.net/ncurses/5.7/ncurses-5.7-${PATCHDATE}.patch.gz;name=p20100501	\
	file://tic-hang.patch \
"

SRC_URI[tarball.md5sum] = "cce05daf61a64501ef6cd8da1f727ec6"
SRC_URI[tarball.sha256sum] = "0a9bdea5c7de8ded5c9327ed642915f2cc380753f12d4ad120ef7da3ea3498f4"
SRC_URI[p20100424sh.md5sum] = "3a5f76613f0f7ec3e0e73b835bc24864"
SRC_URI[p20100424sh.sha256sum] = "1e9d70d2d1fe1fea471868832c52f1b9cc6065132102e49e2a3755f2f4f5be53"
SRC_URI[p20100501.md5sum] = "6518cfa5d45e9069a1e042468161448b"
SRC_URI[p20100501.sha256sum] = "a97ccc30e4bd6fbb89564f3058db0fe84bd35cfefee831556c500793b477abde"


do_configure_prepend() {
	rm -rf tack
}

PARALLEL_MAKE = ""
EXTRA_AUTORECONF="-I m4"

ENABLE_WIDEC = true
ENABLE_WIDEC_virtclass-native = false

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

        ! ${ENABLE_WIDEC} || \
            oe_runmake -C widec libs
}

do_install() {
        ! ${ENABLE_WIDEC} || \
               oe_runmake -C widec 'DESTDIR=${D}' \
               install.libs install.includes install.man

        oe_runmake -C narrowc 'DESTDIR=${D}' install.libs install.progs install.data

        cd narrowc

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
