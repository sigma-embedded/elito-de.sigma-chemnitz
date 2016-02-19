## --*- conf -*--

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

COPY profile.d		  /etc/profile.d/
COPY elito-config	  /home/@USER@/.elito-config
COPY authorized_keys	  /home/@USER@/.ssh/authorized_keys
COPY ssh_config		  /home/@USER@/.ssh/config
COPY authorized_keys	  /root/.ssh/authorized_keys
COPY bin		  /usr/local/bin

# start e.g. '/usr/sbin/thttpd -h localhost -p 9000 -D -d .' in the
# directory with elito.pem
#ADD  http://localhost:9000/elito.pem /home/@USER@/.elito.pem

ADD  http://www.sigma-chemnitz.de/dl/elito/sources/elito-setup \
     /home/@USER@/bin/elito-setup

ADD  http://www.sigma-chemnitz.de/dl/elito/sources/bitbake-conf.sample \
     /home/@USER@/.bitbake.elito.conf

RUN \
	chmod 0644 /root/.ssh/authorized_keys && \
	touch /home/@USER@/.elito.pem && \
	chown -R @USER@:@USER@ /home/@USER@ && \
	chmod 0644 /home/@USER@/.ssh/authorized_keys && \
	chmod 0600 /home/@USER@/.ssh/config && \
	chmod 0600 /home/@USER@/.elito.pem && \
	chmod 0755 /home/@USER@/bin/* && \
	chmod 0755 /usr/local/bin/* && \
	git config --system --add core.gitProxy /usr/local/bin/git-tunnel && \
	:

EXPOSE 22
ENTRYPOINT ["/usr/sbin/sshd", "-D"]