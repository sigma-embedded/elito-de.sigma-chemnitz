## --*- conf -*--

FROM docker/sigma/ubuntu:xenial

RUN \
    export DEBIAN_FRONTEND=noninteractive && \
    apt-get update && \
    apt-get upgrade -y -q --no-install-recommends \
        -o Dpkg::Options::="--force-confdef" -o Dpkg::Options::="--force-confold" && \
    apt-get install -y -q --no-install-recommends \
        -o Dpkg::Options::="--force-confdef" -o Dpkg::Options::="--force-confold"  \
	autoconf \
	automake \
	ca-certificates \
	ccache \
	chrpath \
	cpio \
	cscope \
	curl \
	diffstat \
	exuberant-ctags \
	gawk \
	gcc \
	git \
	global \
	g++ \
	libncurses5-dev \
	libncursesw5-dev \
	libproxy-tools \
	libtool \
	lzop \
	make \
	openssh-server \
	patch \
	pkg-config \
	python \
	python-bs4 \
	python-pyinotify \
	python3 \
	socat \
	strace \
	texinfo \
	wget \
	xz-utils \
    && :

COPY profile.d /etc/profile.d/

RUN \
    mkdir -p /var/run/sshd && \
    sed 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' -i /etc/pam.d/sshd
