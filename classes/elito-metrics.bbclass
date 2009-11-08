# --*- python -*--
addhandler elito_metrics_eventhandler

def elito_metrics_write(d, str):
    import fcntl, bb
    fname = bb.data.getVar('METRICS_FILE', d, True)
    assert(fname != None)

    f = open(fname + ".tmp", "a")
    try:
        fcntl.flock(f.fileno(), fcntl.LOCK_EX)
        f.write(str)
    finally:
        f.close()

def elito_metrics_to_xml(who, res_a, res_b):
    res = []
    for i in filter(lambda x: x.startswith('ru_'), dir(res_a)):
        try:
            a = res_a.__getattribute__(i)
            b = res_b.__getattribute__(i)

            if 0 and a == 0 and b == 0:
                continue
        except:
            continue

        res.append('<resource who="%s" id="%s">%s</resource>' % (who, i[3:], a-b))

    return res


python elito_metrics_eventhandler() {
    import resource, time, fcntl
    from bb import note, error, data
    from bb.event import NotHandled, getName

    if e.data is None or getName(e) == "MsgNote":
        return NotHandled

    if data.getVar("METRICS_FILE", e.data, True) == None:
        return NotHandled


    name = getName(e)
    if name == "PkgStarted":
        assert data.getVar('_PKG_RESOURCES_SELF', e.data, False) == None
        assert data.getVar('_PKG_RESOURCES_CHLD', e.data, False) == None
        assert data.getVar('_PKG_CLOCK', e.data, False) == None
        assert data.getVar('_PKG_TIME',  e.data, False) == None

        data.setVar('_PKG_RESOURCES_CHLD', resource.getrusage(resource.RUSAGE_CHILDREN), e.data)
        data.setVar('_PKG_RESOURCES_SELF', resource.getrusage(resource.RUSAGE_SELF), e.data)
        data.setVar('_PKG_CLOCK',      time.clock(), e.data)
        data.setVar('_PKG_TIME',       time.time(), e.data)
    elif name == "BuildStarted":
        data.setVar('_BUILD_START_CLOCK', time.clock(), e.data)
        data.setVar('_BUILD_START_TIME',  time.time(), e.data)
        data.setVar('_BUILD_RESOURCES_CHLD', resource.getrusage(resource.RUSAGE_CHILDREN), e.data)
        data.setVar('_BUILD_RESOURCES_SELF', resource.getrusage(resource.RUSAGE_SELF), e.data)

        dst_fname = data.getVar("METRICS_FILE", e.data, True)
        try:
            os.unlink(dst_fname + ".tmp")
        except:
            pass
        elito_metrics_write(e.data, '')

    elif name == "BuildCompleted":
        now_chld = resource.getrusage(resource.RUSAGE_CHILDREN)
        now_self = resource.getrusage(resource.RUSAGE_SELF)
        res_chld = data.getVar('_BUILD_RESOURCES_CHLD', e.data, False)
        res_self = data.getVar('_BUILD_RESOURCES_SELF', e.data, False)

        start_tm = data.getVar('_BUILD_START_TIME', e.data, False)
        now      = time.time()

        fname = data.getVar('METRICS_FILE', e.data, True)
        f_out = open(fname, 'a')
        f_in  = open(fname + ".tmp", 'r')
        try:
            fcntl.flock(f_out.fileno(), fcntl.LOCK_EX)
            fcntl.fcntl(f_in.fileno(),  fcntl.LOCK_SH)

            f_out.write('<!-- Started: %s   Ended: %s -->\n' %
                        (time.strftime('%c', time.gmtime(start_tm)),
                         time.strftime('%c', time.gmtime(now))))

            f_out.write('<build started="%f" finished="%f" duration="%f">\n' %
                        (data.getVar('_BUILD_START_TIME', e.data, False),
                         now,
                         now - data.getVar('_BUILD_START_TIME', e.data, False)))

            while True:
               buf = f_in.read(8192)
               if len(buf) == 0:
                   break
               f_out.write(buf)

            f_out.write('  <metrics>\n' +
                        '    <!-- RUSAGE_CHILDREN -->\n' +
                        ''.join(map(lambda x: '    ' + x + '\n',
                                    elito_metrics_to_xml("children", now_chld, res_chld))) +
                        '    <!-- RUSAGE_SELF -->\n' +
                        ''.join(map(lambda x: '    ' + x + '\n',
                                    elito_metrics_to_xml("self", now_self, res_self))) +
                        "  </metrics>\n")
            f_out.write('</build>\n')
            os.unlink(fname + ".tmp")
        finally:
            f_in.close()
            f_out.close()

    if name == "TaskSucceeded" or name == "TaskFailed":
        res_chld = data.getVar('_PKG_RESOURCES_CHLD', e.data, False)
        res_self = data.getVar('_PKG_RESOURCES_SELF', e.data, False)

        now_chld = resource.getrusage(resource.RUSAGE_CHILDREN)
        now_self = resource.getrusage(resource.RUSAGE_SELF)

        assert(res_chld != None)
        assert(res_self != None)

        info = {
        'now' : time.time(),
        'PV'  : data.getVar('PV', e.data, True),
        'PR'  : data.getVar('PR', e.data, True),
        'PN'  : data.getVar('PN', e.data, True),
        'PF'  : data.getVar('PF', e.data, True),
        'start_tm_str' : time.strftime('%c', time.gmtime(data.getVar('_PKG_TIME', e.data, False))),
        'end_tm_str'   : time.strftime('%c', time.gmtime(time.time())),
        'total_time' : time.clock() - data.getVar('_PKG_CLOCK', e.data, False),
        'result' : ['FAIL','OK'][name == "TaskSucceeded"],
        'task' : e.task }

        x = \
        '  <!-- %(task)s(%(PF)s) | %(start_tm_str)s - %(end_tm_str)s -->\n' \
        '  <task name="%(task)s" result="%(result)s" ' \
        'time="%(now)s" pn="%(PN)s" pv="%(PV)s" pr="%(PR)s" duration="%(total_time)f">\n' % info

        elito_metrics_write(e.data, x +
                            '    <!-- RUSAGE_CHILDREN -->\n' +
                            ''.join(map(lambda x: '    ' + x + '\n',
                                        elito_metrics_to_xml("children", now_chld, res_chld))) +
                            '    <!-- RUSAGE_SELF -->\n' +
                            ''.join(map(lambda x: '    ' + x + '\n',
                                        elito_metrics_to_xml("self", now_self, res_self))) +

                            '  </task>\n')

    return NotHandled
}
