# 1. Temel imaj olarak OpenJDK 17 kullan
FROM openjdk:17-jdk-slim

# 2. Çalışma dizinini ayarla
WORKDIR /app

# 3. Bağımlılıkların daha hızlı kurulması için pom.xml ve mvnw dosyalarını kopyala
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# 4. Maven bağımlılıklarını indir (önbelleğe alınır)
RUN ./mvnw dependency:resolve

# 5. Uygulama kaynaklarını kopyala
COPY . .

# 6. Uygulama build edilir
RUN ./mvnw clean package -DskipTests

# 7. Uygulama çalıştırılır
CMD ["java", "-jar", "target/erasmus-0.0.1-SNAPSHOT.jar"]