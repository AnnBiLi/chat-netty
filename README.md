dockerFile

```
    FROM java:8
    MAINTAINER april
    WORKDIR /home/jar
    VOLUME /var/logs
    WORKDIR /home/jar  
    
    RUN echo 'Asia/Shanghai' > /etc/timezone
    
    ENV JAVA_OPTS=-Djava.security.egd=file:/dev/./urandom
    
    ENV PATH=/usr/local/jdk1.8.0_181/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin 
    ENTRYPOINT ["sh" "-c" "java -jar $JAVA_OPTS app.jar"] 
    
    CMD ["/bin/bash"]  
    
    jdk-8u201-linux-x64.tar.gz
    
FROM centos:7

ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

#安装jdk8
RUN mkdir /usr/local/java
ADD jdk-8u201-linux-x64.tar.gz /usr/local/java
RUN ln -s /usr/local/java/jdk1.8.0_201 /usr/local/java/jdk
ENV JAVA_HOME /usr/local/java/jdk
ENV JRE_HOME ${JAVA_HOME}/jre
ENV CLASSPATH .:${JAVA_HOME}/lib:${JRE_HOME}/lib
ENV PATH ${JAVA_HOME}/bin:$PATH

#应用运行，最终的ENTRYPOINT就是在容器运行时执行的命令
ENV JAVA_OPTS=""
ENV PARAMS="--spring.config.location=/application.yml"
ADD app.jar /app.jar
ADD application.yml /application.yml

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /app.jar $PARAMS"]
```



docker run -itd --name eureka_application -p 8081:8081 -v /usr/local/docker/jars/app.jar:/app.jar -v /usr/local/docker/jars/application.yml:/application/yml app_image:1.0

```aidl
FROM www.docker.handi.com:5000/handi/jdk:1.8

MAINTAINER sxhandi

WORKDIR /home/jar

VOLUME /var/logs

ADD app.jar app.jar

RUN sh -c 'touch /app.jar'

RUN echo 'Asia/Shanghai' > /etc/timezone

RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

EXPOSE 8761

ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT [ "sh", "-c", "java -jar $JAVA_OPTS app.jar" ]

```

```aidl
FROM www.docker.handi.com:5000/handi/jdk:1.8

MAINTAINER sxhandi

WORKDIR /home/jar

VOLUME /var/logs

ADD app.jar app.jar

RUN sh -c 'touch /app.jar'

RUN echo 'Asia/Shanghai' > /etc/timezone

RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

# EXPOSE 9090

ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=dev"

ENTRYPOINT [ "sh", "-c", "java -jar $JAVA_OPTS app.jar" ]

```





