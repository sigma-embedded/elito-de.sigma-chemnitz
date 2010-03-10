IPKGBUILDCMD = "; set -x; fakeroot -i ${S}/fakeroot-session ipkg-build"

fixup_perms () {
    cd ${PKGDEST}
    fakeroot -s ${S}/fakeroot-session << __elito_fixup_perms_EOF__
${FIXUP_PERMISSIONS}
__elito_fixup_perms_EOF__
}

PACKAGEFUNCS_append = " fixup_perms"
