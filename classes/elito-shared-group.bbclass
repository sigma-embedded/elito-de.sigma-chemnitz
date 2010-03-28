do_elito_shared_group() {
	chmod -R g+wrX ${WORKDIR}
}

addtask elito_shared_group after do_unpack before do_patch
