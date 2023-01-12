FROM centos

ADD jdk-8u311-linux-x64 .tar.gz /usr/local/jdk/

ENV JAVA_HOME=/usr/local/jdk/jdk1.8.0_311
ENV CLASSPATH=$JAVA_HOME/bin
ENV PATH=.:$JAVA_HOME/bin:$PATH


FROM java-centos:1.8

ADD ffmpeg-git-amd64-static.tar ./

RUN cd ffmpeg-git-20220910-amd64-static

RUN cd /usr/bin \
   && ln -s /ffmpeg-git-20220910-amd64-static/ffmpeg ffmpeg \
   && ln -s /ffmpeg-git-20220910-amd64-static/ffprobe ffprobe

