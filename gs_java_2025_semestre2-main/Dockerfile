# ---------------------------------------------------------
# Estágio 1: Build (Compilação)
# Usa uma imagem Maven com Java 17 para gerar o .jar
# ---------------------------------------------------------
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copia o pom.xml e baixa dependências (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte e compila (pula testes para agilizar o build do container)
COPY src ./src
RUN mvn -B -U clean package -DskipTests

# ---------------------------------------------------------
# Estágio 2: Runtime (Execução)
# Usa uma imagem leve apenas com o JRE para rodar a aplicação
# ---------------------------------------------------------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copia apenas o artefato (JAR) gerado no estágio anterior
COPY --from=build /app/target/*.jar ./app.jar

# Documenta que a aplicação usa a porta 8080
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "/app/app.jar"]