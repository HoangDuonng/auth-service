FROM maven:3.9.8-eclipse-temurin-21-alpine AS common-dto-builder
WORKDIR /common-dto
COPY ../common-dto/pom.xml ./
RUN mvn dependency:go-offline
COPY ../common-dto/src ./src
RUN mvn clean package -DskipTests -Dmaven.test.skip=true

FROM maven:3.9.8-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY auth-service/pom.xml .
COPY --from=common-dto-builder /common-dto/target/*.jar ./common-dto.jar
RUN mvn install:install-file \
    -Dfile=./common-dto.jar \
    -DgroupId=com.graduationproject \
    -DartifactId=common-dto \
    -Dversion=0.0.1-SNAPSHOT \
    -Dpackaging=jar
RUN mvn dependency:go-offline -B
COPY auth-service/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runtime
RUN apk add --no-cache tzdata
RUN apk add --no-cache wget
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN mkdir -p logs && chown -R appuser:appgroup /app
USER appuser
EXPOSE 8081
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8081/api/actuator/health || exit 1
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
