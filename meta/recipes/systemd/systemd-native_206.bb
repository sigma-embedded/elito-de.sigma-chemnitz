DESCRIPTION = "Systemd a init replacement"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/systemd"

LICENSE = "GPLv2 & LGPLv2.1 & MIT"
LIC_FILES_CHKSUM = "file://LICENSE.GPL2;md5=751419260aa954499f7abaabaa882bbe \
                    file://LICENSE.LGPL2.1;md5=4fbd65380cdd255951079008b364516c \
                    file://LICENSE.MIT;md5=544799d0b492f119fa04641d1b8868ed"

FILESEXTRAPATHS_prepend := "${THISDIR}:"

SRC_URI = "\
  http://www.freedesktop.org/software/systemd/systemd-${PV}.tar.xz \
  file://remove-unneeded-builtin.patch \
"

inherit pkgconfig autotools native

SRC_URI[md5sum] = "89e36f2d3ba963020b72738549954cbc"
SRC_URI[sha256sum] = "4c993de071118ea1df7ffc4be26ef0b0d78354ef15b2743a2783d20edfcde9de"

EXTRA_OECONF += "\
  --disable-selinux \
  --disable-gudev \
  --disable-audit \
  --disable-ima \
  --disable-kmod \
  --disable-blkid \
  --disable-smack \
  --disable-acl \
  --disable-xattr \
  --disable-chkconfig \
  --disable-gcrypt \
  --disable-manpages \
  --without-python \
"

do_compile() {
    oe_runmake udevadm
}

do_install() {
    install -p -D -m 0755 udevadm ${D}${base_bindir}/udevadm
}
