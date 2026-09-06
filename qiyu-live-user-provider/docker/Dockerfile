FROM eclipse-temurin:21-jre

WORKDIR /app

VOLUME /tmp

# 旧写法示例：如果后面要在容器里 attach Arthas，可以把压缩包拷进来。
# COPY arthas-bin.zip /opts/arthas-bin.zip

# 容器内的 JVM 参数，和你本地启动时的参数尽量保持一致，方便排查问题。
ENV JAVA_OPTS="-server -Xmx1g -Xms1g -Xmn256m"

# 老的 jar 名：qiyu-live-user-provider-docker.jar
# 新的可执行 jar 名：qiyu-live-user-provider-app-exec.jar
ARG JAR_FILE=target/qiyu-live-user-provider-app-exec.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8082
EXPOSE 9090

# 用 shell 方式启动，便于把 JAVA_OPTS 展开后再传给 java。
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Duser.timezone=Asia/Shanghai -jar /app/app.jar"]
