do_configure_prepend() {
	for i in lib/m4/*.m4; do
		grep -q '\(gettext-0.*\)' "$i" || continue
		rm -f "$i"
	done
}
