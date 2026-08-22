# Official lightweight Java 21 image (Alpine)
FROM eclipse-temurin:21-jdk-alpine

# Working directory inside the container
WORKDIR /app

# Copies the .jar built by Maven into the container
COPY target/*.jar app.jar

# Default Spring Boot port
EXPOSE 8080

# A container image is a deployment artifact, not a local dev tool — default to the "prod"
# profile so running this image always requires real configuration (see below) instead of
# silently falling back to the H2/local profile. Override with -e SPRING_PROFILES_ACTIVE=local
# only if you specifically want to run this image against H2 for a quick manual test.
ENV SPRING_PROFILES_ACTIVE=prod

# Required environment variables when running this image with the default "prod" profile
# (see application-prod.properties) — the app will fail fast at startup if any are missing:
#   API_SECURITY_TOKEN_SECRET   long random secret used to sign JWTs
#   DB_URL                      JDBC URL of the real database (e.g. jdbc:postgresql://host:5432/db)
#   DB_USERNAME                 database username
#   DB_PASSWORD                 database password
#
# Example:
#   docker run -p 8080:8080 \
#     -e API_SECURITY_TOKEN_SECRET=a-long-random-value \
#     -e DB_URL=jdbc:postgresql://db-host:5432/ecommerce \
#     -e DB_USERNAME=ecommerce_app \
#     -e DB_PASSWORD=change-me \
#     ecommerce:latest

ENTRYPOINT ["java", "-jar", "app.jar"]
