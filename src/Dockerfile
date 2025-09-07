# ---- build stage (Maven + JDK 21) ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# copy pom first to leverage layer cache
COPY pom.xml ./
RUN mvn -B dependency:go-offline

# copy source and build
COPY . .
RUN mvn -B clean package -DskipTests

# ---- runtime stage (JDK 21) ----
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# If your application reads PORT from env:
# server.port=${PORT:8080} in application.properties
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
