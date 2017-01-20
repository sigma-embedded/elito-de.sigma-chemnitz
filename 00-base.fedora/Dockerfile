## --*- conf -*--

FROM docker/sigma-fedora:25

RUN \
    dnf upgrade -y && \
    dnf install -y \
	autoconf \
	automake \
	bzip2 \
	ccache \
	chrpath \
	cpio \
	cscope \
	ctags \
	curl \
	diffstat \
	file \
	gawk \
	gcc \
	gcc-c++ \
	git \
	global \
	gnutls-devel \
	hostname \
	libgcrypt-devel \
	libtool \
	lsof \
	lzop \
	make \
	ncurses-devel \
	openssh-server \
	patch \
	'perl(bigint)' \
	pkgconfig \
	procps-ng \
	python \
	python-beautifulsoup4 \
	python-inotify \
	socat \
	strace \
	texinfo \
	wget \
	which \
	xz \
\
	libproxy-bin \
	libproxy-devel \
    && \
    dnf clean all

RUN \
    /usr/libexec/openssh/sshd-keygen ed25519 && \
    /usr/libexec/openssh/sshd-keygen rsa

COPY profile.d /etc/profile.d/

RUN \
    mkdir -p /var/run/sshd && \
    sed 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' -i /etc/pam.d/sshd && \
    :
