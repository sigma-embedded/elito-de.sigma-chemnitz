OVERRIDES .= "${@base_contains('DISTRO_FEATURES', 'x11', '', ':nox11', d)}"

_nox11-pkgs := "${@'${PACKAGES}'.replace(' udev-consolekit ', ' ')}"

PACKAGES_nox11 = "${_nox11-pkgs}"

do_install_append_nox11() {
    rm -rf ${D}${libdir}/ConsoleKit
}
