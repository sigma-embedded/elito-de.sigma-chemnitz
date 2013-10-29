DESCRIPTION	= "Base file system structure."
SECTION		= "base"
PRIORITY	= "required"
PR		= "r14"
LICENSE		= "GPLv3"
PACKAGE_ARCH	= "all"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

RDEPENDS_${PN} += "base-files"

do_install() {
	for i in ${baselib} lib libexec opt selinux share srv var; do
		install -d -m 0755 ${D}/${i}
	done

	for i in tmpfiles.d modules.d profile.d pki pki/elito; do
		install -d -m 0755 ${D}/etc/$i
	done

	for i in ${baselib} lib libexec include; do
		install -d -m 0755 ${D}/usr/$i
	done

        for i in X11 doc empty icons locale pixmaps; do
		install -d -m 0755 ${D}/usr/share/${i}
	done

        for i in locale; do
		install -d -m 0755 ${D}/usr/lib/${i}
	done

        for i in firmware modules; do
		install -d -m 0755 ${D}/lib/$i
        done

        for i in cache; do
		ln -s volatile/$i ${D}/var/$i
        done
}

PACKAGES    = "${PN}"
FILES_${PN} = "/${baselib} /lib /libexec /etc/* \
	/opt /selinux /share /srv \
	/usr /var"
