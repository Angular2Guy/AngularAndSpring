FROM adoptopenjdk/openjdk16:alpine-jre
VOLUME /tmp
ARG JAR_FILE
ADD target/${JAR_FILE} /app.jar
ENV JAVA_OPTS="-Xms256m -Xmx768m -XX:+UseG1GC -XX:MaxGCPauseMillis=150 -XX:+UseStringDeduplication"
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
