PR .= "+b${ELITO_BUILD_NUMBER}"

python () {
    import bb.parse
    try:
        bb.parse.mark_dependency(d, d.expand("${TMPDIR}/build-num"))
    except:
        pass
}
