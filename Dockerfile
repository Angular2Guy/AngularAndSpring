FROM eclipse-temurin:25-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
ADD backend/target/${JAR_FILE} /app.jar
# params for 2GB of container memory limit
# ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=60 -XX:MaxGCPauseMillis=50 -XX:+UseStringDeduplication -XX:MaxDirectMemorySize=64m -XX:+ExitOnOutOfMemoryError"
# params for 1GB of container memory limit
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=50 -XX:MaxMetaspaceSize=128m -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:+UseStringDeduplication -XX:MaxDirectMemorySize=64m -XX:+ExitOnOutOfMemoryError"
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
