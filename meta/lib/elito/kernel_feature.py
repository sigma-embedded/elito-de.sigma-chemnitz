class KConfigValue:
    def __init__(self, v):
        self.v = v

YES = KConfigValue('y')
MOD = KConfigValue('m')
OFF = KConfigValue('o')

def sed_cmd(all_options):
    options = []
    for o in all_options:
        if not o[0]:
            continue

        if isinstance(o[1], str):
            if len(o) != 3:
                raise Exception("bad number of kernel options: %s" % (o,))
            options.append([o[1], o[2]])
        else:
            options.extend(o[1:])

    disable_opts = filter(lambda x: x[1] == None or  x[1] == False, options)
    enable_opts  = filter(lambda x: x[1] != None and x[1] != False, options)

    interset =  set(map(lambda x: x[0].lstrip('+'), disable_opts))\
        .intersection(map(lambda x: x[0].lstrip('+'), enable_opts))

    if interset:
        raise Exception("kernel option conflict: %s" % interset)

    res = []

    for (name, newval) in options:
        if name.startswith('+'):
            name=name[1:]
            dflt='y'
        else:
            dflt='m'

        if newval == True:
            res.append("CONFIG_%s=%s" % (name, dflt))
        elif newval in [None, False, OFF]:
            res.append("# CONFIG_%s is not set" % name)
        elif isinstance(newval, KConfigValue):
            res.append("CONFIG_%s=%s" % (name, newval.v))
        elif isinstance(newval, str):
            res.append("CONFIG_%s=\"%s\"" % (name, newval))
        elif isinstance(newval, int):
            res.append("CONFIG_%s=%u" % (name, newval))
        else:
            raise Exception("unsupported value '%s'" % newval)

    return '\n'.join(res)

if __name__ == '__main__':
    print(sed_cmd([ [True, 'FOO', None],
                    [True, ['BAR', True], ['B1', 'f']],
                    [True, 'XX', YES],
                    [True, 'XX', YES],
                    [True, 'YY', MOD] ]))
