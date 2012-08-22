DESCRIPTION	= "Base file system structure."
SECTION		= "base"
PRIORITY	= "required"
PR		= "r14"
LICENSE		= "GPLv3"
PACKAGE_ARCH	= "all"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

do_install() {
	for i in boot bin dev etc home ${baselib} lib libexec media mnt \
                 opt proc root run sbin selinux share srv sys usr var; do
		install -d -m 0755 ${D}/${i}
	done

	for i in tmpfiles.d modules.d profile.d pki pki/elito; do
		install -d -m 0755 ${D}/etc/$i
	done

	for i in bin ${baselib} lib libexec sbin include share; do
		install -d -m 0755 ${D}/usr/$i
	done

        for i in X11 doc empty icons info locale man misc pixmaps; do
		install -d -m 0755 ${D}/usr/share/${i}
	done

        for i in locale; do
		install -d -m 0755 ${D}/usr/lib/${i}
	done

        for i in firmware modules; do
		install -d -m 0755 ${D}/lib/$i
        done

        for i in lib spool volatile; do
		install -d -m 0755 ${D}/var/$i
	done

        for i in cache run tmp log lock; do
		ln -s volatile/$i ${D}/var/$i
        done

	ln -s var/tmp ${D}/tmp
}

PACKAGES    = "${PN}"
FILES_${PN} = "/bin /boot /dev /etc /home /${baselib} /lib /libexec \
	/media /mnt /opt /proc /root /sbin /selinux /share /srv /sys	\
	/tmp /usr /var /run"
