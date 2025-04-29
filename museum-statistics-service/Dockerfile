FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

FROM openjdk:21-jdk-slim

RUN apt-get update && apt-get install -y curl && apt-get clean

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8081

ENV MUSEUM_URL=http://museum-main-internal.var9.svc.cluster.local:8080

ENTRYPOINT ["java", "-jar", "app.jar"]
