FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY TechMarket-Core TechMarket-Core
COPY TechMarket-IAM TechMarket-IAM

RUN mvn -q -f TechMarket-Core/pom.xml -DskipTests -Dspotless.check.skip=true install
RUN mvn -q -f TechMarket-IAM/pom.xml -DskipTests -Dspotless.check.skip=true clean package

FROM eclipse-temurin:21-jre
WORKDIR /opt/iam-service

COPY --from=builder /workspace/TechMarket-IAM/target/iam-service-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/opt/iam-service/app.jar", "--spring.profiles.active=docker"]
