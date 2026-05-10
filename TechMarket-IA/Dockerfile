FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY TechMarket-Core/pom.xml pom.xml
COPY TechMarket-Core/core-platform core-platform
COPY TechMarket-IA TechMarket-IA

RUN mvn -q -f pom.xml -DskipTests -Dspotless.check.skip=true install
RUN mvn -q -f core-platform/pom.xml -DskipTests -Dspotless.check.skip=true install
RUN mvn -q -f TechMarket-IA/pom.xml -DskipTests -Dspotless.check.skip=true clean package

FROM eclipse-temurin:21-jre
WORKDIR /opt/techmarket-ia

COPY --from=builder /workspace/TechMarket-IA/target/TECHMARKET-ia-*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/opt/techmarket-ia/app.jar", "--spring.profiles.active=docker"]
