__SKIPPED_pn-gtk+ := "${@base_contains('DISTRO_DERIVED_FEATURES', 'gtk-supported', '', 'gtk-not-supported', d)}"
