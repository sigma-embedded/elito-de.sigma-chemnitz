## --*- conf -*--

FROM gitlab.intern.sigma-chemnitz.de:5000/docker/sigma/fedora:29

RUN \
    dnf upgrade -y && \
    dnf install -y \
	autoconf \
	automake \
	bzip2 \
	ccache \
	chrpath \
	coreutils-common \
	cpio \
	cscope \
	ctags \
	curl \
	diffstat \
	file \
	iproute \
	iputils \
	gawk \
	gcc \
	gcc-c++ \
	git \
	global \
	hostname \
	libproxy-bin \
	libtirpc \
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
	python-unversioned-command \
	python2 \
	python3 \
	python3-beautifulsoup4 \
	python3-inotify \
	rpcgen \
	socat \
	strace \
	texinfo \
	wget \
	which \
	xz \
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
