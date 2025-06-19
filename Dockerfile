FROM openjdk:17-jdk-slim

WORKDIR /app

# 1. mvnw ve pom.xml'i kopyala
COPY mvnw .
COPY pom.xml .

# 2. mvnw'ye çalıştırma izni ver
RUN chmod +x mvnw

# 3. .mvn klasörünü kopyala (önceki COPY'de .mvn sonradan gelirse chmod bozulur)
COPY .mvn .mvn

# 4. Bağımlılıkları indir
RUN ./mvnw dependency:resolve

# 5. Projenin tamamını kopyala
COPY . .

# 6. Tekrar emin ol çalıştırılabilirlik bozulduysa
RUN chmod +x mvnw

# 7. Build
RUN ./mvnw clean package -DskipTests

# 8. Uygulama başlat
CMD ["java", "-jar", "target/drds-0.0.1-SNAPSHOT.jar"]
