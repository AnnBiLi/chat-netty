FROM centos:7.9.2009

ADD jdk-8u211-linux-x64.tar.gz /usr/local/jdk/

RUN echo http://mirrors.ustc.edu.cn/alpine/v3.9/main > /etc/apk/repositories && \
    echo http://mirrors.ustc.edu.cn/alpine/v3.9/community >> /etc/apk/repositories

RUN apk --no-cache add ca-certificates wget && \
    wget -q -O /etc/apk/keys/sgerrand.rsa.pub https://alpine-pkgs.sgerrand.com/sgerrand.rsa.pub && \
    wget https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.29-r0/glibc-2.29-r0.apk && \
    apk add glibc-2.29-r0.apk

ENV JAVA_HOME=/usr/local/jdk/jdk1.8.0_211
ENV CLASSPATH=$JAVA_HOME/bin
ENV PATH=.:$JAVA_HOME/bin:$PATH

FROM ubuntu:18.04

ADD jdk-8u311-linux-x64 .tar.gz /usr/local/jdk/

ENV JAVA_HOME=/usr/local/jdk/jdk1.8.0_311
ENV CLASSPATH=$JAVA_HOME/bin
ENV PATH=.:$JAVA_HOME/bin:$PATH


FROM jdk8:base

RUN echo "http://mirrors.aliyun.com/alpine/v3.6/main" > /etc/apk/repositories \
    && echo "http://mirrors.aliyun.com/alpine/v3.6/community" >> /etc/apk/repositories  \
    && apk add --no-cache procps unzip curl bash tzdata  \
    && apk add yasm && apk add ffmpeg \
    && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime  \
    && echo "Asia/Shanghai" > /etc/timezone

RUN yum install -y epel-release
RUN rpm --import http://li.nux.ro/download/nux/RPM-GPG-KEY-nux.ro
RUN rpm -Uvh http://li.nux.ro/download/nux/dextop/el7/x86_64/nux-dextop-release-0-5.el7.nux.noarch.rpm
RUN yum install ffmpeg ffmpeg-devel -y

RUN yum install autoconf automake gcc gcc-c++ git libtool make nasm pkgconfig zlib-devel -y

ADD ffmpeg-4.1.tar.xz /opt/soft/
RUN mv /opt/soft/ffmpeg-6b6b9e5 /opt/soft/ffmpeg-4.1 \
    && cd /opt/soft/ffmpeg-4.1  \
    && ./configure --prefix=/opt/soft/ffmpeg-4.1 --enable-libx264 --enable-static --enable-shared \
    && make && make install


ENV export FFMPEG_HOME=/opt/soft/ffmpeg-4.1/bin \
    && export PATH=$FFMPEG_HOME:$PATH

FROM jdk1.8:ubuntu

ADD TDengine-client-3.0.1.1-Linux-x64.tar.gz usr/local/taos

RUN usr/local/taos/TDengine-client-3.0.1.1/install_client.sh

FROM jdk8:apline

WORKDIR /usr/local

ADD TDengine-client-3.0.1.1-Linux-x64.tar.gz /usr/local/

RUN cd /usr/local/TDengine-client-3.0.1.1/ && ./install_client.sh

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime  \
    && echo "Asia/Shanghai" > /etc/timezone

#
#ENTRYPOINT ["/usr/local/tomcat/bin/catalina.sh","run"]

FROM ubuntu:18.04 as builder
RUN apt-get update \
    && apt-get install -y gcc cmake build-essential git wget  \
    && apt-get clean \
    && cd /usr/local/src \
    && wget https://github.com/taosdata/TDengine/archive/refs/tags/ver-2.4.0.0.tar.gz \
    && tar zxvf ver-2.4.0.0.tar.gz && cd TDengine-ver-2.4.0.0 \
    && mkdir debug && cd debug \
    && cmake .. && cmake --build . && make install
WORKDIR /root


FROM ubuntu:18.04

COPY ./entrypoint.sh /usr/bin/
COPY --from=0 /usr/local/taos /usr/local/taos
COPY --from=0 /etc/taos /etc/taos

ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update \
    && apt-get install -y apt-utils locales tzdata curl wget net-tools iproute2 iputils-ping sysstat binutils \
    && locale-gen en_US.UTF-8 \
    && apt-get clean \
    && chmod +x /usr/bin/entrypoint.sh \
    && ln -s /usr/local/taos/bin/taos /usr/bin/taos \
    && ln -s /usr/local/taos/bin/taosd       /usr/bin/taosd \
    && ln -s /usr/local/taos/bin/taosdump    /usr/bin/taosdump \
    && ln -s /usr/local/taos/bin/taosdemo    /usr/bin/taosdemo \
    && ln -s /usr/local/taos/bin/remove.sh   /usr/bin/rmtaos \
    && ln -s /usr/local/taos/include/taoserror.h  /usr/include/taoserror.h \
    && ln -s /usr/local/taos/include/taos.h  /usr/include/taos.h \
    && ln -s /usr/local/taos/driver/libtaos.so.2.4.0.0  /usr/lib/libtaos.so.1 \
    && ln -s /usr/lib/libtaos.so.1 /usr/lib/libtaos.so  \
    && mkdir -p /var/lib/taos \
    && mkdir -p /var/log/taos \
    && chmod 777 /var/log/taos

ENV LC_ALL=en_US.UTF-8
ENV LANG=en_US.UTF-8
ENV LANGUAGE=en_US.UTF-8

WORKDIR /etc/taos
EXPOSE 6030 6031 6032 6033 6034 6035 6036 6037 6038 6039 6040 6041 6042
CMD ["taosd"]
VOLUME [ "/var/lib/taos", "/var/log/taos", "/corefile" ]
ENTRYPOINT [ "/usr/bin/entrypoint.sh" ]

FROM jdk8:base

ADD TDengine-client-3.0.1.1-Linux-x64.tar.gz /usr/local/
#ADD TDengine-server-3.0.1.1-Linux-x64.tar.gz /usr/local/

RUN cd /usr/local/TDengine-client-3.0.1.1/ && ./install_client.sh

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime  \
    && echo "Asia/Shanghai" > /etc/timezone


./configure --enable-shared --prefix=/usr/local/ffmpeg --enable-gpl --enable-libx264


RUN curl -O http://mirrors.sau.edu.cn/repo/Centos-7.repo
RUN yum clean all && yum makecache
RUN yum -y update && yum -y install gcc build-essential && yum -y install make zlib-devel gcc-c++ libtool openssl openssl-devel
WORKDIR /yasm
ADD yasm-1.3.0.tar /yasm
RUN cd /yasm/yasm-1.3.0; \
./configure; \
make; \
make install;
WORKDIR /ffmpeg
ADD ffmpeg-4.1.tar.xz /ffmpeg
RUN cd /ffmpeg/ffmpeg-4.1; \
./configure --enable-shared --prefix=/usr/local/ffmpeg; \
make; \
make install; \
echo "/usr/local/ffmpeg/lib" >> /etc/ld.so.conf; \
ldconfig;
ENV PATH /usr/local/ffmpeg/bin:$PATH


FROM ubuntu-jdk:1.8

RUN echo "http://mirrors.aliyun.com/alpine/v3.6/main" > /etc/apk/repositories \
    && echo "http://mirrors.aliyun.com/alpine/v3.6/community" >> /etc/apk/repositories  \
    && apk add --no-cache procps unzip curl bash tzdata  \
    && apk add yasm && apk add ffmpeg \
    && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime  \
    && echo "Asia/Shanghai" > /etc/timezone

FROM jdk1.8:latest

RUN yum install -y epel-release rpm

RUN sudo yum -y install make automake gcc gcc-c++ cc kernel-devel glibc-devel make

ADD yasm-1.3.0.tar /yasm
RUN cd /yasm/yasm-1.3.0; \
./configure; \
make; \
make install;

ADD ffmpeg-4.1.tar.xz /ffmpeg
RUN cd /ffmpeg/ffmpeg-4.1; \
./configure --enable-shared --prefix=/usr/local/ffmpeg; \
make; \
make install; \
echo "/usr/local/ffmpeg/lib" >> /etc/ld.so.conf; \
ldconfig;

cd /usr/bin
ln -s ffmpeg-git-20220910-amd64-static/ffmpeg ffmpeg
ln -s ffmpeg-git-20220910-amd64-static/ffprobe ffprobe


echo "/usr/local/ffmpeg/lib" >> /etc/ld.so.conf; \
ldconfig;


