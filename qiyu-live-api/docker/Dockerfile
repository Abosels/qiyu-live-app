FROM eclipse-temurin:21-jre

WORKDIR /app

VOLUME /tmp

# 旧写法示例：如果后面要在容器里 attach Arthas，可以把压缩包一起拷进来。
# COPY arthas-bin.zip /opts/arthas-bin.zip

# 新写法：这里直接复制 Spring Boot 的可执行 jar。
# 老的普通 jar：qiyu-live-api-1.0-SNAPSHOT.jar
# 新的可执行 jar：qiyu-live-api-1.0-SNAPSHOT-exec.jar
ARG JAR_FILE=target/qiyu-live-api-1.0-SNAPSHOT-exec.jar
COPY ${JAR_FILE} app.jar

# 容器启动参数，和本机调试时尽量保持一致，方便排查问题。
ENV JAVA_OPTS="\
-server \
-Xmx512m \
-Xms512m \
-Xmn256m "

EXPOSE 8081
EXPOSE 9090

# 统一用 shell 方式启动，方便展开 JAVA_OPTS。
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Duser.timezone=Asia/Shanghai -jar /app/app.jar"]