FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon
 
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8081

ENV MUSEUM_URL=http://museum-main.var9.svc.cluster.local:8080

ENTRYPOINT ["java", "-jar", "app.jar"] 