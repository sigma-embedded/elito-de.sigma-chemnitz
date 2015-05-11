## --*- conf -*--

FROM fedora:21

ENV http_proxy http://www-cache:3128/
ENV https_proxy http://www-cache:3128/
ENV ftp_proxy http://www-cache:3128/
ENV no_proxy .sigma-chemnitz.de,sigma-chemnitz.de,cvg.de,.cvg.de,andto.de,.andto.de,<local>

RUN \
    : 20150512 - invalidate cache && \
    yum upgrade --setopt="proxy=$http_proxy" -y && \
    yum clean all

RUN \
    yum install --setopt="proxy=$http_proxy" -y \
	autoconf \
	automake \
	bzip2 \
	ccache \
	chrpath \
	curl \
	diffstat \
	gawk \
	gcc \
	gcc-c++ \
	git \
	gnutls-devel \
	hostname \
	libgcrypt-devel \
	libtool \
	lzop \
	make \
	openssh-server \
	patch \
	pkgconfig \
	python \
	python-beautifulsoup4 \
	python-inotify \
	socat \
	strace \
	sudo \
	texinfo \
	wget \
	xz \
\
	libproxy-bin \
	libproxy-devel \
    && yum clean all

RUN /usr/sbin/sshd-keygen

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
