# ── Stage 1: Build ──
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY shared/pom.xml shared/pom.xml
COPY query-expansion-service/pom.xml query-expansion-service/pom.xml
RUN chmod +x mvnw && ./mvnw dependency:go-offline -pl query-expansion-service -am -q
COPY shared/src shared/src
COPY query-expansion-service/src query-expansion-service/src
RUN ./mvnw package -pl query-expansion-service -am -DskipTests -q

# ── Stage 2: Run ──
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /app/query-expansion-service/target/*.jar app.jar
RUN chown app:app app.jar
USER app
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
