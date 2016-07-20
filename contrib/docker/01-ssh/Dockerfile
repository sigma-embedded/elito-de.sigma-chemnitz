## --*- conf -*--

## TODO: move this into Ubuntu/Debian Dockerfiles; for now, keep it
## here because only this file is mangled by the 'build' script
RUN \
	if test -x /usr/sbin/locale-gen; then \
		/usr/sbin/locale-gen '@LANG@' '@LANG_PREFIX@'; \
	fi

RUN \
	/usr/sbin/groupadd -g @UID@ @USER@ && \
	/usr/sbin/useradd -u @UID@ -g @USER@ -m -s /bin/bash @USER@ && \
	id @USER@ && \
	mkdir -m 0711 /home/@USER@/.ssh && \
	chown @USER@:@USER@ /home/@USER@/.ssh && \
	:

USER @UID@:@UID@

RUN \
	HOME=~@USER@; export HOME && \
	D=~@USER@/bin && \
	mkdir -p "$D" && \
	git config --global user.email "@EMAIL@" && \
	git config --global user.name "@USERNAME@"

USER 0:0

COPY profile.d			/etc/profile.d/
COPY elito-config		/home/@USER@/.config/elito/toplevel.mk
COPY authorized_keys.target	/home/@USER@/.config/elito/authorized_keys
COPY ssh_config			/home/@USER@/.ssh/config
COPY authorized_keys.docker	/home/@USER@/.ssh/authorized_keys
COPY authorized_keys.docker	/root/.ssh/authorized_keys
COPY bin			/usr/local/bin

# start e.g. '/usr/sbin/thttpd -h localhost -p 9000 -D -d .' in the
# directory with elito.pem
#ADD  http://localhost:9000/elito.pem /home/@USER@/.config/elito/customer.pem

ADD  http://www.sigma-chemnitz.de/dl/elito/sources/elito-setup \
     /home/@USER@/bin/elito-setup

ADD  http://www.sigma-chemnitz.de/dl/elito/sources/bitbake-conf.sample \
     /home/@USER@/.config/elito/bitbake.conf

RUN \
	chmod 0644 /root/.ssh/authorized_keys && \
	touch /home/@USER@/.config/elito/customer.pem && \
	chown -R @USER@:@USER@ /home/@USER@ && \
	chmod 0644 /home/@USER@/.ssh/authorized_keys && \
	chmod 0600 /home/@USER@/.ssh/config && \
	chmod 0600 /home/@USER@/.config/elito/customer.pem && \
	chmod 0755 /home/@USER@/bin/* && \
	chmod 0755 /usr/local/bin/* && \
	:

USER @UID@:@UID@

RUN \
	cd /home/@USER@ && \
	{ ! test -f .config/elito/customer.pem || \
	  /home/@USER@/bin/elito-setup --only-proxy=/home/@USER@/bin --batch; } && \
	git config --global --add core.gitProxy none for sigma-chemnitz.de && \
	git config --global --add core.gitProxy none for git.andto.de && \
	git config --global --add core.gitProxy /usr/local/bin/git-tunnel && \
	:

USER 0:0

EXPOSE 22
ENTRYPOINT ["/usr/sbin/sshd", "-D"]
