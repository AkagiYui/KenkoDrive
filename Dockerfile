FROM bellsoft/liberica-runtime-container:jre-17-slim-musl

MAINTAINER AkagiYui

COPY build/libs/*.jar /app/app.jar
COPY ./config/.env-prod.yaml /app/.env-prod.yaml
WORKDIR /app
EXPOSE 6677
ENTRYPOINT ["java", "-jar", "./app.jar"]
