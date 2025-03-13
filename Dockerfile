# 1. Используем официальный образ OpenJDK 21 с JAR
FROM openjdk:21-jdk-slim

# 2. Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# 3. Копируем JAR-файл приложения в контейнер
COPY build/libs/hl-module1-1.0-SNAPSHOT.jar app.jar

# 4. Открываем порт 8080 (для доступа к API)
EXPOSE 8080

# 5. Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
