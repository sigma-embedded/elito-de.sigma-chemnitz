PR = "r0"

DESCRIPTION = "Ncurses library"
HOMEPAGE = "http://www.gnu.org/software/ncurses/ncurses.html"
LICENSE = "MIT"
SECTION = "libs"
DEPENDS = "ncurses-native"
PACKAGES_prepend = "ncurses-tools "
PACKAGES_append = " ncurses-terminfo"
PACKAGES_append = " ncurses-libtinfo"
RSUGGESTS_${PN} = "ncurses-terminfo"
RPROVIDES = "libncurses5"

inherit autotools

# This keeps only tput/tset in ncurses
# clear/reset are in already busybox
FILES_ncurses_append   = " ${datadir}/tabset"
FILES_ncurses-tools    = "${bindir}/tic ${bindir}/toe ${bindir}/infotocap ${bindir}/captoinfo ${bindir}/infocmp ${bindir}/clear.${PN} ${bindir}/reset.${PN} ${bindir}/tack "
FILES_ncurses-terminfo = "${datadir}/terminfo"
FILES_ncurses-libtinfo = "${libdir}/libtinfo.so.*"
FILES_${PN} = "${bindir}/tput ${bindir}/tset ${libdir}/lib*.so.* usr/share/tabset etc/terminfo"


SRC_URI = "${GNU_MIRROR}/ncurses/ncurses-${PV}.tar.gz \
       	ftp://invisible-island.net/ncurses/5.6/ncurses-5.6-20080621-patch.sh.bz2;patch=1	\
	ftp://invisible-island.net/ncurses/5.6/ncurses-5.6-20080628.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.6/ncurses-5.6-20080705.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.6/ncurses-5.6-20080712.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.6/ncurses-5.6-20080713.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.6/ncurses-5.6-20080726.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.6/ncurses-5.6-20080804.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.6/ncurses-5.6-20080816.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.6/ncurses-5.6-20080823.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.6/ncurses-5.6-20080830.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.6/ncurses-5.6-20080906.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.6/ncurses-5.6-20080907.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.6/ncurses-5.6-20080913.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.6/ncurses-5.6-20080920.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.6/ncurses-5.6-20080925.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.6/ncurses-5.6-20080927.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.6/ncurses-5.6-20081004.patch.gz;patch=1		\
	ftp://invisible-island.net/ncurses/5.6/ncurses-5.6-20081011.patch.gz;patch=1		\
#	ftp://invisible-island.net/ncurses/5.6/ncurses-5.6-20081012.patch.gz;patch=1		\
	"
S = "${WORKDIR}/ncurses-${PV}"

EXTRA_OECONF = " \
	--without-normal						\
	--without-debug							\
	--without-ada							\
	--with-termpath='${sysconfdir}/termcap:${datadir}/misc/termcap'	\
	--with-terminfo-dirs=${sysconfdir}/terminfo:${datadir}/terminfo \
	--with-shared							\
	--disable-big-core						\
	--program-prefix=						\
	--with-termlib=tinfo 						\
	--enable-widec							\
	--enable-sigwinch						\
	\
"

do_configure_prepend() {
	rm -rf tack
}

do_configure() {
	oe_runconf		\
		--with-build-cflags=""				\
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
