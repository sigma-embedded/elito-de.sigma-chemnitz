DEPENDS += "libxslt-native elito-devicetree"

FILES_${PN}-dev += "${datadir}/mach-${MACHINE}/*.dcd"

do_install() {
    # some sanity checks to verify environment
    test -n "${UBOOT_BOARD}"
    test -d arch/arm/boards/${UBOOT_BOARD}

    install -d -m 0755 ${D}/${datadir}/mach-${MACHINE}

    for i in arch/arm/boards/${UBOOT_BOARD}/*.dcd; do
        install -p -m 0644 "$i" ${D}/${datadir}/mach-${MACHINE}/
    done
}

deploy_file() {
    dst="${1%.*}-$2.${1##.*}"
    install -D -p -m 0444 "$1" "${DEPLOYDIR}/$dst"
    rm -f "${DEPLOYDIR}/$1"
    ln -s "$dst" "${DEPLOYDIR}/$1"
}

do_deploy () {
    gitrev=`git ls-remote . HEAD | sed '1s/^\(........\).*/\1/p;d'`

    deploy_file barebox "${gitrev}"

    cd images
    for i in barebox-*.img; do
	deploy_file "$i" "${gitrev}"
    done
    cd -
}
