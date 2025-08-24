# Sử dụng image JDK 17 dựa trên Alpine cho image cuối cùng
FROM eclipse-temurin:17-jdk-alpine

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Copy file JAR từ thư mục target vào container
# Nếu tên file JAR của bạn khác, hãy thay đổi cho phù hợp.
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Mở cổng 8080 (Render sẽ thiết lập PORT thông qua biến môi trường)
EXPOSE 8081

# Lệnh chạy ứng dụng. Spring Boot đã được cấu hình sử dụng biến môi trường PORT nếu có.
ENTRYPOINT ["java", "-jar", "app.jar"]
