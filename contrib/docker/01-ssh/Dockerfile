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
COPY authorized_keys.target	/home/@USER@/.config/oe/authorized_keys
COPY local.mk			/home/@USER@/.config/oe/local.mk
COPY local.conf			/home/@USER@/.config/oe/local.conf
COPY ssh_config			/home/@USER@/.ssh/config
COPY authorized_keys.docker	/home/@USER@/.ssh/authorized_keys
COPY authorized_keys.docker	/root/.ssh/authorized_keys
COPY bin			/usr/local/bin

ADD  http://www.sigma-chemnitz.de/dl/elito/sources/flymake-kernel.mk \
     /home/@USER@/lib/make/flymake-kernel.mk

RUN \
	chmod 0644 /root/.ssh/authorized_keys && \
	chown -R @USER@:@USER@ /home/@USER@ && \
	chmod 0644 /home/@USER@/.ssh/authorized_keys && \
	chmod 0600 /home/@USER@/.ssh/config && \
	chmod 0755 /usr/local/bin/* && \
	:

USER @UID@:@UID@

RUN \
	cd /home/@USER@ && \
	git config --global --add core.gitProxy 'none for sigma-chemnitz.de' && \
	git config --global --add core.gitProxy 'none for git.andto.de' && \
	git config --global --add core.gitProxy /usr/local/bin/git-tunnel && \
	git config --global --add 'include.path' '~/.gitconfig.mirrors' && \
	git config --global --add 'include.path' '~/.gitconfig.mirrors-ssh' && \
	git config --global --add 'include.path' '/etc/gitconfig.mirrors' && \
	git config --global --add 'include.path' '/etc/gitconfig.mirrors-ssh' && \
	:

USER 0:0

VOLUME	/var/tmp
VOLUME	/var/cache

ENTRYPOINT ["/usr/sbin/sshd", "-D"]
