FROM eclipse-temurin:21-jre

WORKDIR /app

VOLUME /tmp

# 旧写法示例：如果后面要在容器里 attach Arthas，可以把压缩包一起拷进来。
# COPY arthas-bin.zip /opts/arthas-bin.zip

# 新写法：这里直接复制 Spring Boot 的可执行 jar。
# 老的普通 jar：qiyu-live-gateway-1.0-SNAPSHOT.jar
# 新的可执行 jar：qiyu-live-gateway-1.0-SNAPSHOT-exec.jar
ARG JAR_FILE=target/qiyu-live-gateway-1.0-SNAPSHOT-exec.jar
COPY ${JAR_FILE} app.jar

# 网关 JVM 参数，给一个偏保守的内存配置，便于你本机调试。
ENV JAVA_OPTS="\
-server \
-Xmx512m \
-Xms512m \
-Xmn256m "

EXPOSE 80

# 网关对外统一出口，浏览器优先访问这个容器端口。
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Duser.timezone=Asia/Shanghai -jar /app/app.jar"]