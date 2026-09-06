FROM eclipse-temurin:21-jre

WORKDIR /app

VOLUME /tmp

# 容器内的 JVM 参数，和本地启动时的参数尽量保持一致，方便排查问题。
ENV JAVA_OPTS="-server -Xmx512m -Xms512m -Xmn256m"

# 从模块 target 复制 Spring Boot 可执行 jar
ARG JAR_FILE=target/qiyu-live-im-provider-1.0-SNAPSHOT-exec.jar
COPY ${JAR_FILE} app.jar

# 8085：HTTP 端口；9094：Dubbo provider 端口
EXPOSE 8085
EXPOSE 9094

# 用 shell 方式启动，便于把 JAVA_OPTS 展开后再传给 java。
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Duser.timezone=Asia/Shanghai -jar /app/app.jar"]
