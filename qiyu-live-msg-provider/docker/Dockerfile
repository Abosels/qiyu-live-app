FROM eclipse-temurin:21-jre

WORKDIR /app

VOLUME /tmp

# 容器内 JVM 参数，便于和本地启动保持一致，排查问题更直接
ENV JAVA_OPTS="-server -Xmx512m -Xms512m -Xmn128m"

# Spring Boot 打出来的可执行 jar
ARG JAR_FILE=target/qiyu-live-msg-provider-app-exec.jar
COPY ${JAR_FILE} app.jar

# 这里预留 Web 端口和 Dubbo 端口
EXPOSE 8084
EXPOSE 9090

# shell 方式启动，确保 JAVA_OPTS 会被展开
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Duser.timezone=Asia/Shanghai -jar /app/app.jar"]
