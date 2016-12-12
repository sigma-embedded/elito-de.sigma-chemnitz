## --*- conf -*--

FROM fedora:25

ENV http_proxy http://www-cache:3128/
ENV https_proxy http://www-cache:3128/
ENV ftp_proxy http://www-cache:3128/
ENV no_proxy .sigma-chemnitz.de,sigma-chemnitz.de,cvg.de,.cvg.de,andto.de,.andto.de,localhost,<local>

RUN \
    : 20160304 - invalidate cache && \
    rpm --import /etc/pki/rpm-gpg/* && \
    dnf upgrade --setopt="proxy=$http_proxy" -y && \
    dnf clean all && \
    dnf makecache --setopt="proxy=$http_proxy" -y

RUN \
    dnf install --setopt="proxy=$http_proxy" -y \
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

RUN /usr/libexec/openssh/sshd-keygen

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

# CA setup

ADD https://www.sigma-chemnitz.de/ca/crt/2007-SIGMA-CA-top.crt \
    /usr/local/share/ca-certificates/2007-SIGMA-CA-top.crt

ADD https://www.sigma-chemnitz.de/ca/crt/2014-SIGMA-CA-server.crt \
    /usr/local/share/ca-certificates/2014-SIGMA-CA-server.crt

ADD https://www.sigma-chemnitz.de/ca/crt/2016-SIGMA-CA-root.crt \
    /usr/local/share/ca-certificates/2016-SIGMA-CA-root.crt

ADD https://www.sigma-chemnitz.de/ca/crt/2016-SIGMA-CA-server.crt \
    /usr/local/share/ca-certificates/2016-SIGMA-CA-server.crt

RUN \
    /bin/chmod a+r /usr/local/share/ca-certificates/*-SIGMA-CA-*.crt && \
    /usr/bin/update-ca-trust
