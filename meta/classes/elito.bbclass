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

    v = d.getVar(var_name, True)
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

def elito_quote(s):
    import pipes
    return pipes.quote(s)

def elito_glob(pathname):
    import glob
    return glob.glob(pathname)

def elito_uri(uri, d):
    import elito
    return elito.uri(uri, d)

def elito_repo(local, remote, d):
    import elito
    return elito.repo(local, remote, d)

def elito_get_distro_version(d):
    import elito
    return elito.update_build_info(d, get_layers_branch_rev)

def elito_intersect(var_a, var_b, d):
    a = set((d.getVar(var_a, True) or "").split())
    b = set((d.getVar(var_b, True) or "").split())
    c = a.intersection(b)
    return " ".join(c)

python do_elito_set_home() {
    d.setVar("HOME", "${WORKDIR}/.home");
    d.setVarFlag("HOME", "export", True);
}

def elito_rewrite_src_uri(fetch, d):
    cur   = [fetch.type, fetch.host, fetch.path, fetch.user, fetch.pswd, fetch.parm]
    old   = None
    rlist = (d.expand('${PROJECT_REPOSITORIES}', True) or '').split('\n')
    repos = []
    for r in rlist:
        if r.strip() == '':
            continue
        (rem,loc) = r.split()

        matched = True

        tmp = bb.fetch2.decodeurl(rem)
        for i in xrange(0, len(tmp) - 1):
            if tmp[i] != '' and tmp[i] != cur[i]:
                matched = False
                break

        if matched:
            tmp = bb.fetch2.decodeurl(loc)
            old = list(cur)
            old[5] = old[5].copy()

            for i in xrange(0, len(tmp) - 1):
                cur[i] = tmp[i]

            cur[5].update(tmp[5])

            (fetch.type, fetch.host, fetch.path, fetch.user,
             fetch.pswd, fetch.parm) = cur

            break

    if old:
        bb.logger.debug(1, "rewrite_uri: %s => %s" % (old, cur))

do_compile[prefuncs] += "do_elito_set_home"
do_install[prefuncs] += "do_elito_set_home"

export ABRT_IGNORE_ALL="1"

BB_HASHCONFIG_WHITELIST += "\
 ftp_proxy http_proxy https_proxy no_proxy DISPLAY \
 KRB5CCNAME DESKTOP_STARTUP_ID GNOME_KEYRING_PID GPG_AGENT_INFO \
 BITBAKE_UI BB_NUMBER_THREADS BB_NUMBER_PARSE_THREADS ELITO_METRICS_ID \
 HOSTNAME DOMAIN ELITO_CACHE_DIR ELITO_WORKSPACE_DIR \
 _ELITO_EXT_PROJECT_FEATURES SECWRAP_CMD BB_GENERATE_MIRROR_TARBALLS \
 ABRT_IGNORE_ALL \
"

BB_HASHBASE_WHITELIST += "ELITO_CRT ELITO_TOPDIR"

SRC_URI[vardepsexclude] += "\
  ELITO_GIT_REPO ELITO_PUBLIC_GIT_REPO ELITO_WORKSPACE_DIR"

SRC_URI[_rewrite_fn] = "elito_rewrite_src_uri"

IMAGE_FEATURES[validitems] += "devel-history devel-sshkey no-root-bash"
