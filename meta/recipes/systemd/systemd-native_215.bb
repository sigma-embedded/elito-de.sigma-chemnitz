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
  file://manual-sources.patch \
"

inherit pkgconfig autotools native gettext

SRC_URI[md5sum] = "d2603e9fffd8b18d242543e36f2e7d31"
SRC_URI[sha256sum] = "ce76a3c05e7d4adc806a3446a5510c0c9b76a33f19adc32754b69a0945124505"

# required for expanding autoconf macro
DEPENDS += "libgcrypt"

EXTRA_OECONF += "\
  --disable-acl \
  --disable-apparmor \
  --disable-audit \
  --disable-backlight \
  --disable-binfmt \
  --disable-blkid \
  --disable-bootchart \
  --disable-chkconfig \
  --disable-coredump \
  --disable-efi \
  --disable-gcrypt \
  --disable-gnutls \
  --disable-gudev \
  --disable-gudev \
  --disable-hostnamed \
  --disable-ima \
  --disable-kmod \
  --disable-libcryptsetup \
  --disable-localed \
  --disable-logind \
  --disable-machined \
  --disable-manpages \
  --disable-microhttpd \
  --disable-multi-seat-x \
  --disable-myhostname \
  --disable-networkd \
  --disable-pam \
  --disable-polkit \
  --disable-qrencode \
  --disable-quotacheck \
  --disable-randomseed \
  --disable-readahead \
  --disable-resolved \
  --disable-rfkill \
  --disable-seccomp \
  --disable-selinux \
  --disable-smack \
  --disable-smack \
  --disable-tests \
  --disable-timedated \
  --disable-timesyncd \
  --disable-tmpfiles \
  --disable-vconsole \
  --without-python \
"

do_compile() {
    oe_runmake manual-source-build
    oe_runmake udevadm
}

do_install() {
    install -p -D -m 0755 udevadm ${D}${base_bindir}/udevadm
}
