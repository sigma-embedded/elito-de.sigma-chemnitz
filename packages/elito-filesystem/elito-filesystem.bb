DESCRIPTION	= "Base file system structure."
SECTION		= "base"
PRIORITY	= "required"
PR		= "r5"
LICENSE		= "GPLv3"
PACKAGE_ARCH	= "all"

do_install() {
	for i in bin dev etc home ${lib-name} libexec media mnt \
		 opt proc root sbin selinux share srv sys usr var; do
		install -d -m0755 ${D}/${i}
	done

	for i in files.d modules.d; do
		install -d -m0755 ${D}/etc/$i
	done

	for i in bin lib libexec sbin include share; do
		install -d -m0755 ${D}/usr/$i
	done

	install -d -m0755 ${D}/lib/firmware
	ln -s var/tmp ${D}/tmp
	ln -s var/lib/boot ${D}/boot
	mkdir -p ${D}/var/tmp/ ${D}/var/lib/boot
}

PACKAGES    = "${PN}"
FILES_${PN} = "/bin /boot /dev /etc /home /${lib-name} /libexec \
	/media /mnt /opt /proc /root /sbin /selinux /share /srv /sys	\
	/tmp /usr /var"
