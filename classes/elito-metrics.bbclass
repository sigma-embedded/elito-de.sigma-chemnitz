# --*- python -*--

python elito_metrics_eventhandler () {
    import bb

    if bb.__version__.startswith("1.8."):
        return _elito_metrics_eventhandler_18(e)
    else:
        return _elito_metrics_eventhandler(e)
}
addhandler elito_metrics_eventhandler

def _elito_metrics_id(d):
    import os
    return bb.data.getVar('METRICS_ID', d, True) or os.environ.get("ELITO_METRICS_ID")

def _elito_metrics_tmpname(d):
    import os.path

    fname = bb.data.getVar('METRICS_FILE', d, True)
    mid   = _elito_metrics_id(d)
    assert(fname != None)
    if mid == None:
        return None

    return os.path.join(bb.data.getVar('TMPDIR', d, True),
                        os.path.basename(fname) + ".tmp.%s" % mid)

def elito_metrics_write(d, str):
    import fcntl, bb

    f = open(_elito_metrics_tmpname(d), "a")
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

def _elito_metrics_write_build_complete(fname, start_info, end_info, e):
    import time, fcntl
    from bb import data

    f_in_name = _elito_metrics_tmpname(e.data)

    try:
        f_out = open(fname, 'a')
        f_in  = open(f_in_name, 'r')
        os.unlink(f_in_name)
    except:
        bb.warn("Failed to execute build-complete step")
        return

    try:
        fcntl.flock(f_out.fileno(), fcntl.LOCK_EX)
        fcntl.fcntl(f_in.fileno(),  fcntl.LOCK_SH)

        f_out.write('<!-- Started: %s   Ended: %s -->\n' %
                    (time.strftime('%c', time.gmtime(start_info['time'])),
                     time.strftime('%c', time.gmtime(end_info['time']))))

        f_out.write('<build project="%s" tmpdir="%s" started="%f" finished="%f"' \
                    ' duration="%f" id="%s">\n' %
                    (data.getVar('PROJECT_NAME', e.data, True),
                     os.path.basename(data.getVar('TMPDIR', e.data)),
                     start_info['time'], end_info['time'],
                     end_info['time'] - start_info['time'],
                     _elito_metrics_id(e.data)))

        f_out.write('  <sysinfo>\n')

        uname = os.uname()
        f_out.write('    <hostname>%s</hostname>\n' \
                    '    <release>%s</release>\n' \
                    '    <machine>%s</machine>\n' % \
                    (uname[1], uname[2], uname[4]))

        try:
            total_mem = os.sysconf("SC_PHYS_PAGES") * os.sysconf("SC_PAGE_SIZE")
            f_out.write('    <memory>%s</memory>\n' % total_mem)
        except:
            pass

        try:
            f_out.write('    <cpus>%u</cpus>\n' %
                        os.sysconf("SC_NPROCESSORS_ONLN"))
        except:
            pass

        f_out.write('  </sysinfo>\n')

        while True:
           buf = f_in.read(8192)
           if len(buf) == 0:
               break
           f_out.write(buf)

        f_out.write('  <metrics>\n')

        if start_info['duse'] != None and end_info['duse'] != None:
            f_out.write('    <diskusage start="%u" end="%u">%d</diskusage>\n' %
                        (start_info['duse'], end_info['duse'],
                         end_info['duse'] - start_info['duse']))

        f_out.write('    <!-- RUSAGE_CHILDREN -->\n' +
                    ''.join(map(lambda x: '    ' + x + '\n',
                                elito_metrics_to_xml("children",
                                                     end_info['res_chld'],
                                                     start_info['res_chld']))) +
                    '    <!-- RUSAGE_SELF -->\n' +
                    ''.join(map(lambda x: '    ' + x + '\n',
                                elito_metrics_to_xml("self",
                                                     end_info['res_self'],
                                                     start_info['res_self']))) +
                    "  </metrics>\n")
        f_out.write('</build>\n')
    finally:
        f_in.close()
        f_out.close()

def _elito_metrics_write_task_complete(start_info, end_info, e):
    from bb.event import getName
    import bb

    import time

    info = {
        'now' : end_info['time'],
        'PV'  : bb.data.getVar('PV', e.data, True),
        'PR'  : bb.data.getVar('PR', e.data, True),
        'PN'  : bb.data.getVar('PN', e.data, True),
        'PF'  : bb.data.getVar('PF', e.data, True),
        'pid' : start_info['pid'],
        'start_tm'     : start_info['time'],
        'start_tm_str' : time.strftime('%c', time.gmtime(start_info['time'])),
        'end_tm_str'   : time.strftime('%c', time.gmtime(end_info['time'])),
        'total_time' : end_info['time'] - start_info['time'],
        'result' : ['FAIL','OK'][getName(e) == "TaskSucceeded"],
        'preference' : bb.data.getVar('DEFAULT_PREFERENCE', e.data, True) or '0',
        'task' : e.task }

    x = \
    '  <!-- %(task)s(%(PF)s) | %(start_tm_str)s - %(end_tm_str)s (PID %(pid)s) -->\n' \
    '  <task name="%(task)s" result="%(result)s" pn="%(PN)s" pv="%(PV)s" pr="%(PR)s" ' \
    'started="%(start_tm)s" ended="%(now)s" duration="%(total_time)f" preference="%(preference)s" pid="%(pid)s">\n' % info

    if end_info['duse'] != None:
        x = x + '    <info type="diskusage">%u</info>\n' % end_info['duse']

    elito_metrics_write(e.data, x +
                        '    <!-- RUSAGE_CHILDREN -->\n' +
                        ''.join(map(lambda x: '    ' + x + '\n',
                                    elito_metrics_to_xml("children",
                                                         end_info['res_chld'],
                                                         start_info['res_chld']))) +
                        '    <!-- RUSAGE_SELF -->\n' +
                        ''.join(map(lambda x: '    ' + x + '\n',
                                    elito_metrics_to_xml("self",
                                                         end_info['res_self'],
                                                         start_info['res_self']))) +

                        '  </task>\n')

def _elito_metrics_get_duse(e):
    from bb import data
    import os

    try:
        df = os.statvfs(data.getVar("TMPDIR", e.data, True))
        duse = df.f_bsize * (df.f_blocks - df.f_bavail)
    except OSError:
        duse = None

    return duse

def _elito_metrics_eventhandler (e):
    from bb import note, error, data
    from bb.event import NotHandled, getName

    def record_resources(prop, d):
        import resource, time, os

        ev_data = (bb.data.getVarFlag('_event_info', 'resources', d) or {})
        ev_data[prop] = { 'res_chld' : resource.getrusage(resource.RUSAGE_CHILDREN),
                          'res_self' : resource.getrusage(resource.RUSAGE_SELF),
                          'clock'    : time.clock(),
                          'time'     : time.time(),
                          'pid'      : os.getpid(),
                          'duse'   : _elito_metrics_get_duse(e) }

        bb.data.setVarFlag('_event_info', 'resources', ev_data, d)


    if e.data is None or getName(e) in ("MsgNote", "ParseProgress", "RecipeParsed", "ConfigParsed"):
        return NotHandled

    name = getName(e)

    if name == "BuildStarted":
        record_resources('build_start', e.data)
        tmp_fname = _elito_metrics_tmpname(e.data)

        try:
            os.unlink(tmp_fname)
        except OSError:
            pass

        elito_metrics_write(e.data, '')
    elif name == "BuildCompleted":
        record_resources('build_end', e.data)
        info = bb.data.getVarFlags('_event_info', e.data)

        res_start = info['resources']['build_start']
        res_end   = info['resources']['build_end']

        if res_start['pid'] != res_end['pid']:
            bb.warn("PID mismatch in BuildCompleted (%u vs %u)" %
                    (res_start['pid'], res_end['pid']))

        _elito_metrics_write_build_complete(data.getVar('METRICS_FILE', e.data, True),
                                            res_start, res_end, e)
    elif name == "TaskStarted":
        record_resources('task_start', e.data)
    elif name == "TaskSucceeded" or name == "TaskFailed":
        record_resources('task_end', e.data)
        info = bb.data.getVarFlags('_event_info', e.data)

        res_start = info['resources']['task_start']
        res_end   = info['resources']['task_end']

        if res_start['pid'] != res_end['pid']:
            bb.warn("PID mismatch in BuildCompleted (%u vs %u)" %
                    (res_start['pid'], res_end['pid']))

        _elito_metrics_write_task_complete(res_start, res_end, e)

    return NotHandled

def _elito_metrics_eventhandler_18 (e):
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
        data.setVar('_BUILD_DUSE', _elito_metrics_get_duse(e), e.data)

        tmp_fname = _elito_metrics_tmpname(e.data)
        try:
            os.unlink(tmp_fname)
        except:
            pass
        elito_metrics_write(e.data, '')

    elif name == "BuildCompleted":
        now_chld = resource.getrusage(resource.RUSAGE_CHILDREN)
        now_self = resource.getrusage(resource.RUSAGE_SELF)
        res_chld = data.getVar('_BUILD_RESOURCES_CHLD', e.data, False)
        res_self = data.getVar('_BUILD_RESOURCES_SELF', e.data, False)
        old_duse = data.getVar('_BUILD_DUSE', e.data, False)

        start_tm = data.getVar('_BUILD_START_TIME', e.data, False)
        now      = time.time()

        f_in_name = _elito_metrics_tmpname(e.data)

        fname = data.getVar('METRICS_FILE', e.data, True)
        f_out = open(fname, 'a')
        f_in  = open(f_in_name, 'r')

        os.unlink(f_in_name)

        try:
            fcntl.flock(f_out.fileno(), fcntl.LOCK_EX)
            fcntl.fcntl(f_in.fileno(),  fcntl.LOCK_SH)

            f_out.write('<!-- Started: %s   Ended: %s -->\n' %
                        (time.strftime('%c', time.gmtime(start_tm)),
                         time.strftime('%c', time.gmtime(now))))

            f_out.write('<build project="%s" tmpdir="%s" started="%f" finished="%f"' \
                        ' duration="%f">\n' %
                        (data.getVar('PROJECT_NAME', e.data, True),
                         os.path.basename(data.getVar('TMPDIR', e.data)),
                         data.getVar('_BUILD_START_TIME', e.data, False),
                         now,
                         now - data.getVar('_BUILD_START_TIME', e.data, False)))

            f_out.write('  <sysinfo>\n')

            uname = os.uname()
            f_out.write('    <hostname>%s</hostname>\n' \
                        '    <release>%s</release>\n' \
                        '    <machine>%s</machine>\n' % \
                        (uname[1], uname[2], uname[4]))

            try:
                total_mem = os.sysconf("SC_PHYS_PAGES") * os.sysconf("SC_PAGE_SIZE")
                f_out.write('    <memory>%s</memory>\n' % total_mem)
            except:
                pass

            try:
                f_out.write('    <cpus>%s</cpus>\n' %
                            os.sysconf("SC_NPROCESSORS_ONLN"))
            except:
                pass

            now_duse = _elito_metrics_get_duse(e)
            if now_duse != None and old_duse != None:
                f_out.write('    <diskusage start="%u" end="%u">%d</diskusage>\n' %
                            (old_duse, now_duse,
                             now_duse - old_duse))
            f_out.write('  </sysinfo>\n')

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
        finally:
            f_in.close()
            f_out.close()

    if name == "TaskSucceeded" or name == "TaskFailed":
        res_chld = data.getVar('_PKG_RESOURCES_CHLD', e.data, False)
        res_self = data.getVar('_PKG_RESOURCES_SELF', e.data, False)

        now_chld = resource.getrusage(resource.RUSAGE_CHILDREN)
        now_self = resource.getrusage(resource.RUSAGE_SELF)

        now_tm   = time.time()

        assert(res_chld != None)
        assert(res_self != None)

        info = {
        'now' : time.time(),
        'PV'  : data.getVar('PV', e.data, True),
        'PR'  : data.getVar('PR', e.data, True),
        'PN'  : data.getVar('PN', e.data, True),
        'PF'  : data.getVar('PF', e.data, True),
        'start_tm'     : data.getVar('_PKG_TIME', e.data, False),
        'start_tm_str' : time.strftime('%c', time.gmtime(data.getVar('_PKG_TIME', e.data, False))),
        'end_tm_str'   : time.strftime('%c', time.gmtime(now_tm)),
        'total_time' : now_tm - data.getVar('_PKG_TIME', e.data, False),
        'result' : ['FAIL','OK'][name == "TaskSucceeded"],
        'preference' : data.getVar('DEFAULT_PREFERENCE', e.data, True) or '0',
        'task' : e.task }

        x = \
        '  <!-- %(task)s(%(PF)s) | %(start_tm_str)s - %(end_tm_str)s -->\n' \
        '  <task name="%(task)s" result="%(result)s" pn="%(PN)s" pv="%(PV)s" pr="%(PR)s" ' \
        'started="%(start_tm)s" ended="%(now)s" duration="%(total_time)f" preference="%(preference)s">\n' % info

        duse = _elito_metrics_get_duse(e)
        if duse != None:
            x = x + '    <info type="diskusage">%u</info>\n' % duse

        elito_metrics_write(e.data, x +
                            '    <!-- RUSAGE_CHILDREN -->\n' +
                            ''.join(map(lambda x: '    ' + x + '\n',
                                        elito_metrics_to_xml("children", now_chld, res_chld))) +
                            '    <!-- RUSAGE_SELF -->\n' +
                            ''.join(map(lambda x: '    ' + x + '\n',
                                        elito_metrics_to_xml("self", now_self, res_self))) +

                            '  </task>\n')

    return NotHandled
