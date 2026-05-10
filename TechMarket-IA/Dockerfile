FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY TechMarket-Core/pom.xml pom.xml
COPY TechMarket-Core/core-platform core-platform
COPY TechMarket-IA TechMarket-IA

# Ensure CA certificates are present (fixes SSL handshake issues in some build environments)
RUN apt-get update \
	&& apt-get install -y --no-install-recommends ca-certificates \
	&& update-ca-certificates \
	&& rm -rf /var/lib/apt/lists/*

# Create Maven settings with retry and timeout configuration for robust dependency resolution
RUN mkdir -p ~/.m2 && cat > ~/.m2/settings.xml << 'EOF'
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <mirrors>
    <mirror>
      <id>central-mirror</id>
      <mirrorOf>central</mirrorOf>
      <url>https://repo.maven.apache.org/maven2</url>
      <blocked>false</blocked>
    </mirror>
  </mirrors>
  <profiles>
    <profile>
      <id>default</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo.maven.apache.org/maven2</url>
          <layout>default</layout>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>daily</updatePolicy>
            <checksumPolicy>warn</checksumPolicy>
          </releases>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>default</activeProfile>
  </activeProfiles>
</settings>
EOF

# Configure Maven with robust networking options: extended timeouts, retries, and modern TLS
ENV MAVEN_OPTS="-Dhttps.protocols=TLSv1.2,TLSv1.3 -Dsun.net.inetaddr.ttl=30 -Dsun.net.inetaddr.negative.ttl=10 -Dcom.sun.org.apache.xml.internal.utils.rfc2396_UNSAFE_CHARACTERS_DEBUG=true"

RUN mvn -q -f pom.xml -DskipTests -Dspotless.check.skip=true -Daether.connector.http.connectionMaxTtl=60000 -Daether.connector.http.connectionManager.ttl=60000 install
RUN mvn -q -f core-platform/pom.xml -DskipTests -Dspotless.check.skip=true -Daether.connector.http.connectionMaxTtl=60000 -Daether.connector.http.connectionManager.ttl=60000 install
RUN mvn -q -f TechMarket-IA/pom.xml -DskipTests -Dspotless.check.skip=true -Daether.connector.http.connectionMaxTtl=60000 -Daether.connector.http.connectionManager.ttl=60000 clean package

FROM eclipse-temurin:21-jre
WORKDIR /opt/techmarket-ia

COPY --from=builder /workspace/TechMarket-IA/target/TECHMARKET-ia-*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/opt/techmarket-ia/app.jar", "--spring.profiles.active=docker"]

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/opt/techmarket-ia/app.jar", "--spring.profiles.active=docker"]
