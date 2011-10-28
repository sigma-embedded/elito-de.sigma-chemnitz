#

def uri(uri, d):
    premirrors = d.getVar("PREMIRRORS", True).split('\n')

    for m in premirrors:
        (k,v) = m.strip().split()
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
