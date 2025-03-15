FROM gradle:8.5-jdk21-alpine AS builder
WORKDIR /opt/app

# Install dos2unix, Python, and pip
RUN apk add --no-cache dos2unix python3 py3-pip

# Install faker using pip
RUN pip install faker --break-system-packages
RUN pip install psycopg2-binary


COPY build.gradle settings.gradle ./
COPY gradlew gradlew
COPY gradle gradle
COPY gradle/wrapper gradle/wrapper

RUN [-f gradle/wrapper/gradle-wrapper.jar ] || (echo "gradle-wrapper.jar не найден, создаём..." && gradle wrapper)

RUN dos2unix gradlew && chmod +x gradlew

RUN ./gradlew dependencies --no-daemon


COPY src src
RUN ./gradlew clean build --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /opt/app

COPY --from=builder /opt/app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]
