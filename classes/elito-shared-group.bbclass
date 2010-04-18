do_elito_shared_group() {
	! test -d "${S}" || chmod -R g+wrX "${S}"
}

addtask elito_shared_group after do_unpack before do_patch
