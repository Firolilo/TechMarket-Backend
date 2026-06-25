# Multi-stage build for the AI service (multi-module Maven, no Core dependency).
# Build context is the repo root (see root docker-compose.yml) for consistency
# with the IA/IAM images.
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY TechMarket-AI TechMarket-AI

RUN mvn -q -f TechMarket-AI/pom.xml -DskipTests -Dspotless.check.skip=true clean package

FROM eclipse-temurin:21-jre
WORKDIR /opt/ai-service

COPY --from=builder /workspace/TechMarket-AI/modules/ai-bootstrap/target/ai-bootstrap-*.jar app.jar

EXPOSE 8091

# Profile is driven by the SPRING_PROFILES_ACTIVE env var (default "docker") so Compose
# can override it, e.g. "docker,gemini". Don't hardcode it as a command-line arg, which
# would win over the env var and block overrides.
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT ["java", "-jar", "/opt/ai-service/app.jar"]
