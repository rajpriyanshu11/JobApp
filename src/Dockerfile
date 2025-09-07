# ---- build stage ----
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# copy everything
COPY . .

# make mvnw executable and build
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# ---- runtime stage ----
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
