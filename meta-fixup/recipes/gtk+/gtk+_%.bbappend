__SKIPPED_pn-gtk+ := "${@\
  bb.utils.contains('DISTRO_DERIVED_FEATURES',\
                    'gtk-supported', '', 'gtk-not-supported', d)}"
