FROM eclipse-temurin:21-jre

WORKDIR /app

VOLUME /tmp

ENV JAVA_OPTS="-server -Xmx512m -Xms512m -Xmn128m"

ARG JAR_FILE=target/qiyu-live-id-generate-provider-1.0-SNAPSHOT-exec.jar
COPY ${JAR_FILE} app.jar

EXPOSE 9090

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Duser.timezone=Asia/Shanghai -jar /app/app.jar"]
