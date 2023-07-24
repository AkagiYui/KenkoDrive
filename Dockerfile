FROM bellsoft/liberica-runtime-container:jre-17-slim-musl

MAINTAINER AkagiYui

COPY build/libs/*.jar /app.jar
WORKDIR /
EXPOSE 6677
ENTRYPOINT ["java", "-jar", "/app.jar"]
