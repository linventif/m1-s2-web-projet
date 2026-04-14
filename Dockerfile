FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests clean package \
    && JAR_FILE="$(find target -maxdepth 1 -type f -name '*.jar' ! -name '*.original' | head -n 1)" \
    && test -n "$JAR_FILE" \
    && cp "$JAR_FILE" target/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN mkdir -p /app/avatar_upload

COPY --from=builder /build/target/app.jar /app/app.jar

EXPOSE 8186

ENV SERVER_PORT=8186

ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar --server.port=${SERVER_PORT:-8186}"]
