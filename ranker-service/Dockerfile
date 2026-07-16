# ── Stage 1: Build ──
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY shared/pom.xml shared/pom.xml
COPY ranker-service/pom.xml ranker-service/pom.xml
RUN chmod +x mvnw && ./mvnw dependency:go-offline -pl ranker-service -am -q
COPY shared/src shared/src
COPY ranker-service/src ranker-service/src
RUN ./mvnw package -pl ranker-service -am -DskipTests -q

# ── Stage 2: Run ──
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /app/ranker-service/target/*.jar app.jar
RUN chown app:app app.jar
USER app
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
