# 该镜像需要依赖的基础镜像
FROM java:8
VOLUME /tmp
ADD gateway-1.0.0-SNAPSHOT.jar /gateway.jar
RUN sh -c 'touch /gateway.jar'
EXPOSE 10010
ENV JAVA_OPTS=""

ENTRYPOINT ["java","-Duser.timezone=GMT+08","-Djava.security.egd=file:/dev/./urandom","-jar","/gateway.jar"]
