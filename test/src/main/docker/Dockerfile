# 该镜像需要依赖的基础镜像
FROM java:8
VOLUME /tmp
ADD test-1.0.0-SNAPSHOT.jar /test.jar
RUN sh -c 'touch /test.jar'
EXPOSE 10111
ENV JAVA_OPTS=""

ENTRYPOINT ["java","-jar","-Duser.timezone=GMT+08","/test.jar"]