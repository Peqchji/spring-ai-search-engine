# ── Stage 1: Build ──
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Copy Maven wrapper and POMs first (dependency caching)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY shared/pom.xml shared/pom.xml
COPY ingestion-service/pom.xml ingestion-service/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml

# Download dependencies (cached unless POMs change)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -pl api-gateway -am -q

# Copy source and build
COPY shared/src shared/src
COPY api-gateway/src api-gateway/src
RUN ./mvnw package -pl api-gateway -am -DskipTests -q

# ── Stage 2: Run ──
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S app && adduser -S app -G app

WORKDIR /app
COPY --from=build /app/api-gateway/target/*.jar app.jar

RUN chown app:app app.jar
USER app

EXPOSE 8080

HEALTHCHECK --interval=15s --timeout=3s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
