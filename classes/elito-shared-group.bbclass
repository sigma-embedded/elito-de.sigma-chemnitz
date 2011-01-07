do_elito_shared_group() {
	! test -d "${S}" || chmod -R g+wrX "${S}"
}

addtask elito_shared_group after do_patch before do_configure
