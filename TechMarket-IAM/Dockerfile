FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY core-plataform core-plataform
COPY iam-service iam-service

RUN mvn -q -f core-plataform/pom.xml -DskipTests -Dspotless.check.skip=true install
RUN mvn -q -f iam-service/pom.xml -DskipTests -Dspotless.check.skip=true clean package

FROM eclipse-temurin:21-jre
WORKDIR /opt/iam-service

COPY --from=builder /workspace/iam-service/target/iam-service-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/opt/iam-service/app.jar", "--spring.profiles.active=docker"]
