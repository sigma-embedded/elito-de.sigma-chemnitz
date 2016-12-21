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
    search_path = (d.getVar("ELITO_REPOSITORY_SEARCH_PATH", True) or
                   d.getVar("ELITO_WORKSPACE_DIR", True) or
                   "").split()

    repos = ( "${ELITO_GIT_REPO}/" + remote,
              "${ELITO_PUBLIC_GIT_REPO}/" + remote )
    path = None

    for p in search_path:
        for abs_path in ("%s/%s" % (p, local),
                         "%s/%s.git" % (p, local)):
            abs_path = os.path.normpath(abs_path)
            if os.path.isdir(abs_path):
                path = abs_path
                break

        if path != None:
            break

    if path == None:
        return ""
    else:
        l = "git://%s;protocol=file" % os.path.normpath(path)
        return ''.join(map(lambda r: "%-40s\t%s\n" % (r,l), repos))

def update_build_info(d, oe_info_fn):
    import bb.parse, time
    import os

    f = d.expand("${TMPDIR}/build-info")

    with bb.utils.fileslocked([f + ".lock"]):
        try:
            with open(f) as fd:
                old_info = fd.read()
        except IOError:
            old_info = ""

        home_set = False
        if 'HOME' not in os.environ:
            os.environ.putenv('HOME', '/')
            home_set = True

        # 'git' (which might be called by this function) might need
        # $HOME
	new_info = "\n".join(oe_info_fn(d)) + "\n"

        if home_set:
            os.environ.unsetenv('HOME')

        if new_info != old_info:
            with open(f, "w") as fd:
                fd.write(new_info)
            bb.parse.update_mtime(f)

    ftime = time.localtime(bb.parse.cached_mtime(f))
    bb.parse.mark_dependency(d, f)

    d.setVar("ELITO_BUILDINFO", new_info)
    return "${DISTRO_VERSION_MAJOR}." + time.strftime("%Y%m%dT%H%M%S", ftime)
