# ── Stage 1: Build ──
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY shared/pom.xml shared/pom.xml
COPY hybrid-retrieval-service/pom.xml hybrid-retrieval-service/pom.xml
RUN chmod +x mvnw && ./mvnw dependency:go-offline -pl hybrid-retrieval-service -am -q
COPY shared/src shared/src
COPY hybrid-retrieval-service/src hybrid-retrieval-service/src
RUN ./mvnw package -pl hybrid-retrieval-service -am -DskipTests -q

# ── Stage 2: Run ──
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /app/hybrid-retrieval-service/target/*.jar app.jar
RUN chown app:app app.jar
USER app
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
