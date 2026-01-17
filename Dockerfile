# build stage
ARG APP_VERSION="2.1.2"

FROM docker.io/gradle:9-jdk25 AS builder
ARG APP_VERSION
WORKDIR /build
COPY . .
RUN gradle build -x test

# runtime stage
FROM registry:5000/awscorretto:25
ARG APP_VERSION="2.1.2"
COPY --from=builder /build/build/libs/serverless-${APP_VERSION}.jar /app/serverless.jar
WORKDIR /app
USER nobody
ENTRYPOINT ["/bin/sh", "-c", "java -jar serverless.jar"]
