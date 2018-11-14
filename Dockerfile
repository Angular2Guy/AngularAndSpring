FROM openjdk:11-jre-slim
VOLUME /tmp
RUN sh -c 'touch /app.jar'
ARG JAR_FILE
ADD target/${JAR_FILE} /app.jar
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseStringDeduplication"
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
