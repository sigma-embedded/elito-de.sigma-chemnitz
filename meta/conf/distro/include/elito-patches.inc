# --*- python -*--
# Due to the elito-directory.patch above
ALTERNATIVE_LINK_pn-iproute2 = "${base_sbindir}/ip"

SRC_URI_append_pn-ifupdown = " \
	file://upstart.patch;apply=yes"

SRC_URI_append_pn-bootchart-lite = " \
	file://elito.patch;apply=yes"
