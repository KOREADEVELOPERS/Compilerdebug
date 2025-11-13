# Step 1: Use official Java JDK 17 image
FROM eclipse-temurin:17-jdk-alpine

# Step 2: Set working directory inside container
WORKDIR /app

# Step 3: Copy Maven build files
COPY pom.xml .
COPY src ./src

# Step 4: Build the application
RUN ./mvnw package -DskipTests

# Step 5: Expose port for backend (Render will assign via $PORT)
ENV PORT=10000
EXPOSE $PORT

# Step 6: Run the Spring Boot jar
CMD ["java", "-jar", "target/your-backend-0.0.1-SNAPSHOT.jar"]