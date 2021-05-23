# 该镜像需要依赖的基础镜像
FROM java:8
VOLUME /tmp
ADD registry-1.0.0-SNAPSHOT.jar /registry.jar
RUN sh -c 'touch /registry.jar'
EXPOSE 10086
ENV JAVA_OPTS=""

ENTRYPOINT ["java","-Duser.timezone=GMT+08","-Djava.security.egd=file:/dev/./urandom","-jar","/registry.jar"]
