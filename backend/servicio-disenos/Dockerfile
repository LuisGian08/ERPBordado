# Multi-stage Dockerfile para servicio-disenos (Java 21)
FROM maven:3.9.8-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache su-exec \
    && addgroup -S appgroup \
    && adduser -S appuser -G appgroup \
    && mkdir -p /var/data/embroidery/disenos
COPY --from=builder /app/target/*.jar app.jar
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod 0755 /usr/local/bin/docker-entrypoint.sh
EXPOSE 8096
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
