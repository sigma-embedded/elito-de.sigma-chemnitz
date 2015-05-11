## --*- conf -*--

FROM ubuntu:trusty

ENV http_proxy http://www-cache:3128/
ENV https_proxy http://www-cache:3128/
ENV ftp_proxy http://www-cache:3128/
ENV no_proxy .sigma-chemnitz.de,sigma-chemnitz.de,cvg.de,.cvg.de,andto.de,.andto.de,<local>

RUN \
    : 20150511 - invalidate cache && \
    apt-get update && \
    apt-get upgrade -y -q --no-install-recommends \
        -o Dpkg::Options::="--force-confdef" -o Dpkg::Options::="--force-confold"

RUN \
    export DEBIAN_FRONTEND=noninteractive && \
    apt-get install -y -q --no-install-recommends \
        -o Dpkg::Options::="--force-confdef" -o Dpkg::Options::="--force-confold"  \
	autoconf \
	automake \
	ca-certificates \
	ccache \
	chrpath \
	curl \
	diffstat \
	gawk \
	gcc \
	git \
	g++ \
	libgnutls-dev \
	libtool \
	lzop \
	make \
	openssh-server \
	patch \
	pkg-config \
	python \
	python-bs4 \
	python-pyinotify \
	socat \
	strace \
	texinfo \
	wget \
	xz-utils \
\
	libproxy-dev \
	libproxy-tools

COPY profile.d /etc/profile.d/

RUN \
    cat /usr/share/zoneinfo/Europe/Berlin > /etc/localtime && \
    mkdir -p /var/run/sshd && \
    sed 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' -i /etc/pam.d/sshd && \
    { echo "http_proxy='$http_proxy'" && \
      echo 'https_proxy=$http_proxy' && \
      echo 'ftp_proxy=$http_proxy' && \
      echo "no_proxy='$no_proxy'"; \
    } >> /etc/profile.d/proxy.sh

# the workspace with valuable files; it can be on a slow disk
VOLUME /srv/elito
# the build directory; it should be on a fast disk and does not need backup
VOLUME /srv/bld
# a directory shared between multiple instances; does not need backup
VOLUME /srv/cache
