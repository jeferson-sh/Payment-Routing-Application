FROM openjdk:17-jdk-slim

WORKDIR /app

COPY gradlew ./
COPY gradle ./gradle

COPY build.gradle settings.gradle ./

COPY src ./src

RUN chmod +x ./gradlew

RUN ./gradlew build --no-daemon

RUN ls build/libs

COPY build/libs/payment-routing-application-*.jar /app/payment-routing-application.jar

EXPOSE 8080
CMD ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-XX:MinRAMPercentage=50.0", "-jar", "payment-routing-application.jar"]
