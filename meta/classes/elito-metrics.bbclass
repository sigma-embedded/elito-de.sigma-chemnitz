# --*- python -*--

METRICS_ID := "${@os.environ['ELITO_METRICS_ID']}"

python elito_metrics_eventhandler () {
    from bb.event import getName

    if getName(e) == "ConfigParsed":
        # search path for 'elito.metrics' is not setup yet for this
        # event
        return

    import bb
    import elito.metrics

    if bb.__version__.startswith("1.8."):
        return elito.metrics.eventhandler_18(e)
    else:
        return elito.metrics.eventhandler(e)
}
addhandler elito_metrics_eventhandler
