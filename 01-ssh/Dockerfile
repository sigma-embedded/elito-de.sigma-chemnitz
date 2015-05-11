## --*- conf -*--

RUN \
	{ ! grep -q ^wheel: /etc/group || go='-G wheel'; } && \
	/usr/sbin/groupadd -g 505 ensc && \
	/usr/sbin/useradd -u 505 -g ensc $go -m -s /bin/bash ensc && \
	id ensc && \
	mkdir -m 0711 /home/ensc/.ssh && \
	chown ensc:ensc /home/ensc/.ssh && \
	echo 'ensc    ALL=(ALL) NOPASSWD: ALL' >> /etc/sudoers

USER 505:505

RUN \
	HOME=~ensc; export HOME && \
	D=~ensc/bin && \
	mkdir -p "$D" && \
	git config --global user.email "enrico.scholz@sigma-chemnitz.de" && \
	git config --global user.name "Enrico Scholz"

USER 0:0

COPY authorized_keys      /home/ensc/.ssh/authorized_keys

# start e.g. '/usr/sbin/thttpd -h localhost -p 9000 -D -d .' in the
# directory with elito.pem
ADD  http://localhost:9000/elito.pem /home/ensc/.elito.pem

ADD  http://www.sigma-chemnitz.de/dl/elito/sources/elito-setup \
     /home/ensc/bin/elito-setup

ADD  http://www.sigma-chemnitz.de/dl/elito/sources/bitbake-conf.sample \
     /home/ensc/.bitbake.elito.conf

RUN \
	chown -R ensc:ensc /home/ensc && \
	chmod 0644 /home/ensc/.ssh/authorized_keys && \
	chmod 0644 /home/ensc/.elito.pem && \
	chown 0755 /home/ensc/bin/elito-setup

EXPOSE 22
ENTRYPOINT ["/usr/sbin/sshd", "-D"]
