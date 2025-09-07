# ---- build stage (Maven + JDK 21) ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# copy pom first to leverage cache
COPY pom.xml ./
RUN mvn -B dependency:go-offline

# copy rest and build
COPY . .
RUN mvn -B clean package -DskipTests

# ---- runtime stage (JDK 21) ----
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Spring Boot should use PORT env var
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
