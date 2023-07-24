FROM bellsoft/liberica-runtime-container:jre-17-slim-musl
MAINTAINER AkagiYui

COPY build/libs/*.jar /app/app.jar
COPY ./config/.env-prod.yaml /app/.env-prod.yaml
WORKDIR /app
EXPOSE 6677
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD code=$(wget --no-check-certificate --spider --server-response http://localhost:6677/server/version 2>&1 | awk '/^  HTTP/{print $2}'); if [ "$code" -ne "200" ]; then exit 1; fi
ENTRYPOINT ["java", "-jar", "./app.jar"]
