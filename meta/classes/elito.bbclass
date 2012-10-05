# --*- python -*--

def get_filedate(filename, dflt=None):
    import os, time

    try:
        st = os.stat(filename)
    except:
        if dflt != None:
            st = ( 0,0,0,0, 0,0,0,0, dflt )
        else:
            st = None

    if st == None:
        raise Exception("can not stat file")

    tm = time.gmtime(st[8])
    return '%04d%02d%02d%02d%02d%02d' % (tm[0], tm[1], tm[2], tm[3], tm[4], tm[5])

def elito_skip(d, feature_pos, feature_neg = None, var_name = "MACHINE_FEATURES"):
    import elito
    elito.test_skip(d, feature_pos, feature_neg, var_name)

def elito_base_switch(d, var_name, *args):
    import bb

    v = bb.data.getVar(var_name, d, 1)
    if len(args) % 2 == 0:
        raise bb.parse.ParseError('elito_base_switch called with odd number of parms: %s' % (args,))

    for i in xrange(0, len(args)-1, 2):
        if v == args[i]:
            return args[i+1]

    return args[-1]

def elito_join_locale(pkgs, langs):
    suffix = map(lambda x: 'locale-%s' % x, langs.split())
    res    = []
    for p in pkgs.split():
        res.append(p)
        res.extend(map(lambda x: '%s-%s' % (p,x), suffix))

    return ' '.join(res)

def elito_upstart_job(d, pkg, job_files, rdepends = None, extra_files = [], \
                      rrecommends = None, do_prepend = 1):
    p = bb.data.expand(pkg, d)

    if isinstance(job_files, basestring):
        job_files = job_files.split()

    if isinstance(extra_files, basestring):
        extra_files = extra_files.split()

    files = map(lambda x: '${sysconfdir}/init/%s' % x, job_files)
    files.extend(extra_files)

    bb.data.setVar("WEAK_RUNTIME_DEPENDENCIES_%s" % p, "True", d)
    bb.data.setVar("FILES_%s" % p, ' '.join(files), d)

    all_pkgs = bb.data.getVar("PACKAGES", d, 0)
    if do_prepend:
        all_pkgs = pkg + " " + all_pkgs
    else:
        all_pkgs = all_pkgs + " " + pkg

    bb.data.setVar("PACKAGES",  all_pkgs, d)

    if rdepends:
        bb.data.setVar("RDEPENDS_%s" % p, rdepends, d)

    if rrecommends:
        bb.data.setVar("RRECOMMENDS_%s" % p, rrecommends, d)

    return p

def elito_quote(s):
    import pipes
    return pipes.quote(s)

def elito_glob(pathname):
    import glob
    return glob.glob(pathname)

def elito_uri(uri, d):
    import elito
    return elito.uri(uri, d)

def elito_build_number(d):
    try:
        f = open(os.path.join(d.getVar("TMPDIR", True), "build-num"))
        return int(f.readline().strip())
    except:
        return 0

def elito_repo(local, remote, d):
    import elito
    return elito.repo(local, remote, d)

def elito_get_distro_version(d):
    import elito
    return elito.update_build_info(d, get_layers_branch_rev)

python do_elito_set_home() {
    d.setVar("HOME", "${WORKDIR}/.home");
    d.setVarFlag("HOME", "export", True);
}

do_compile[prefuncs] += "do_elito_set_home"
do_install[prefuncs] += "do_elito_set_home"

BB_HASHCONFIG_WHITELIST += "\
 ftp_proxy http_proxy https_proxy no_proxy DISPLAY ELITO_BUILD_NUMBER \
 KRB5CCNAME DESKTOP_STARTUP_ID GNOME_KEYRING_PID GPG_AGENT_INFO \
 BITBAKE_UI BB_NUMBER_THREADS BB_NUMBER_PARSE_THREADS ELITO_METRICS_ID \
"

ELITO_BUILD_NUMBER := "${@elito_build_number(d)}"
ELITO_BUILD_NUMBER[vardepvalue] = "${ELITO_BUILD_NUMBER}"
