FROM maven:3.9.8-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup \
    && mkdir -p /var/data/embroidery/inventario && chown -R appuser:appgroup /var/data/embroidery/inventario
USER appuser
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8097
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
