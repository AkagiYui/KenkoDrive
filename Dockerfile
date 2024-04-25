FROM bellsoft/liberica-runtime-container:jre-21-slim-musl AS builder
COPY . /job
WORKDIR /job
RUN chmod +x gradlew && ./gradlew bootJar

FROM bellsoft/liberica-runtime-container:jre-21-slim-musl
LABEL maintainer="AkagiYui"

COPY --from=builder /job/build/libs/*.jar /app.jar
WORKDIR /app
VOLUME /app/data
EXPOSE 6677
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD code=$(wget --no-check-certificate --spider --server-response http://localhost:6677/system/version 2>&1 | awk '/^  HTTP/{print $2}'); if [ "$code" -ne "200" ]; then exit 1; fi
ENTRYPOINT ["java", "-jar", "/app.jar"]
