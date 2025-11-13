# Step 1: Java 17 base image (build stage)
FROM eclipse-temurin:17-jdk-alpine as builder

# Step 2: Maven wrapper और config copy करो
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Step 3: Dependencies pre-download
RUN ./mvnw dependency:go-offline

# Step 4: Source code copy और build
COPY src src
RUN ./mvnw clean package -DskipTests

# Step 5: Final image बनाना
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

# Step 6: Render के लिए dynamic port
EXPOSE 8080

# Step 7: Application run
ENTRYPOINT ["java","-jar","app.jar"]
