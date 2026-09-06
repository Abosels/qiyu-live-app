FROM eclipse-temurin:21-jre

WORKDIR /app

VOLUME /tmp

# 账号服务运行参数，和本地启动保持一致，方便排查问题
ENV JAVA_OPTS="-server -Xmx512m -Xms512m -Xmn128m"

# Spring Boot 打出来的可执行 jar
# 注意：docker-maven-plugin 会先把资源拷贝到 Docker 构建上下文根目录，
# 所以这里不能再写 target/ 前缀，直接写文件名即可。
ARG JAR_FILE=target/qiyu-live-account-provider-app-exec.jar
COPY ${JAR_FILE} app.jar

EXPOSE 9090

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Duser.timezone=Asia/Shanghai -jar /app/app.jar"]
