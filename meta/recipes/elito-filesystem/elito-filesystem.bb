DESCRIPTION	= "Base file system structure."
SECTION		= "base"
PRIORITY	= "required"
PR		= "r7"
LICENSE		= "GPLv3"
PACKAGE_ARCH	= "all"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=2c12447f794c304d9cd353f87a432c9e"

do_install() {
	for i in bin dev etc home ${lib-name} libexec media mnt \
                 opt proc root sbin selinux share srv sys usr var; do
		install -d -m 0755 ${D}/${i}
	done

	for i in files.d modules.d pki pki/elito; do
		install -d -m 0755 ${D}/etc/$i
	done

	for i in bin lib libexec sbin include share; do
		install -d -m 0755 ${D}/usr/$i
	done

	install -d -m 0755 ${D}/lib/firmware
	ln -s var/tmp ${D}/tmp
	ln -s var/lib/boot ${D}/boot
	mkdir -p ${D}/var/tmp/ ${D}/var/lib/boot
}

PACKAGES    = "${PN}"
FILES_${PN} = "/bin /boot /dev /etc /home /${lib-name} /libexec \
	/media /mnt /opt /proc /root /sbin /selinux /share /srv /sys	\
	/tmp /usr /var"
