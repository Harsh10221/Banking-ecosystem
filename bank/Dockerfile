FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY /target/*.jar app.jar

ENV DB_URL="<insert-database-url>"
ENV DB_NAME="<insert-database-name>"
ENV DB_USER="<insert-database-user>"
ENV DB_PASS="<insert-database-password>"

ENV ACCESS_TOKEN="<insert-access-token>"
ENV REFRESH_TOKEN="<insert-refresh-token>"

ENV CENTRAL_HUB_URL="<insert-central-hub-url>"
ENV BANK_PRIVATE_KEY="<insert-bank-private-key>"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]