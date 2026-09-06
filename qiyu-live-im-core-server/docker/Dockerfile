FROM eclipse-temurin:21-jre

WORKDIR /app

VOLUME /tmp

# IM 核心服务需要维持大量 TCP/WebSocket 连接，内存调大一些。
ENV JAVA_OPTS="-server -Xmx1g -Xms1g -Xmn256m"

# 从模块 target 复制 Spring Boot 可执行 jar
ARG JAR_FILE=target/qiyu-live-im-core-server-app-exec.jar
COPY ${JAR_FILE} app.jar

# 8084：HTTP 端口；9092：Dubbo provider 端口；18080：IM TCP；18081：IM WebSocket
EXPOSE 8084
EXPOSE 9092
EXPOSE 18080
EXPOSE 18081

# 用 shell 方式启动，便于把 JAVA_OPTS 展开后再传给 java。
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Duser.timezone=Asia/Shanghai -jar /app/app.jar"]
