#

def uri(uri, d):
    premirrors = d.getVar("PREMIRRORS", True)
    if premirrors == None:
        premirrors = []
    else:
        premirrors = filter(lambda x: x != '' and x[0] != '#', 
                            map(lambda x: x.strip(),
                                premirrors.split('\n')))

    for m in premirrors:
        try:
            (k,v) = m.split()
        except:
            bb.error("Invalid PREMIRRORS element: '%s'" % (m,))
            raise

        if uri == k:
            return v

    return uri

def test_skip(d, feature_pos, feature_neg = None, var_name = "MACHINE_FEATURES"):
    import bb

    v  = d.getVar(var_name, True).split(' ')
    pn = d.getVar('PN', True)

    if ((feature_pos != None and feature_pos not in v) or
        (feature_neg != None and feature_neg     in v)):
        raise bb.parse.SkipPackage('Package %s not available for this machine' % pn)
    if (feature_pos == None and feature_neg == None):
        raise bb.parse.ParseError('No feature given in package %s' % pn)

def repo(remote, local, d):
    r = "${ELITO_GIT_REPO}/" + remote
    l = "git://${ELITO_WORKSPACE_DIR}/%s;protocol=file" % local

    return "%-40s\t%s\n" % (r,l)

def update_build_info(d, oe_info_fn):
    import bb.parse, time

    f = d.expand("${TMPDIR}/build-info")

    with bb.utils.fileslocked([f + ".lock"]):
        try:
            old_info = open(f).read()
        except IOError:
            old_info = ""

	new_info = "\n".join(oe_info_fn(d)) + "\n"
        if new_info != old_info:
            open(f, "w").write(new_info)
            bb.parse.update_mtime(f)

    ftime = time.localtime(bb.parse.cached_mtime(f))

    d.setVar("ELITO_BUILDINFO", new_info)
    return "${DISTRO_VERSION_MAJOR}." + time.strftime("%Y%m%dT%H%M%S", ftime)
