```text
TECHMARKET-ia
|-- .editorconfig
|-- .gitattributes
|-- .gitignore
|-- docker-compose.yml
|-- Dockerfile
|-- mvnw
|-- mvnw.cmd
|-- pom.xml
|-- README.md
|-- .mvn
|   \-- wrapper
|       \-- maven-wrapper.properties
|-- deployment
|   \-- README.md
|-- docs
|   |-- README.md
|   \-- vertical-structure.md
|-- scripts
|   |-- dev-run.ps1
|   \-- test.ps1
|-- src
|   |-- main
|   |   |-- java
|   |   |   \-- com
|   |   |       \-- TechMarket
|   |   |           \-- techmarket
|   |   |               |-- IaServiceApplication.java
|   |   |               |-- integration
|   |   |               |   |-- batch
|   |   |               |   |-- events
|   |   |               |   |-- listeners
|   |   |               |   \-- schedulers
|   |   |               |-- security
|   |   |               |   |-- config
|   |   |               |   |-- filter
|   |   |               |   |-- jwt
|   |   |               |   |-- permission
|   |   |               |   |-- rbac
|   |   |               |   \-- tenant
|   |   |               \-- shared
|   |   |                   |-- audit
|   |   |                   |-- common-dto
|   |   |                   |-- commondto
|   |   |                   |-- constants
|   |   |                   |-- util
|   |   |                   \-- validation
|   |   \-- resources
|   |       |-- application-dev.yml
|   |       |-- application-docker.yml
|   |       |-- application-prod.yml
|   |       |-- application.yml
|   |       \-- db
|   |           \-- migration
|   |               |-- .gitkeep
|   |               \-- V1__global_parameters_placeholder.sql
|   \-- test
|       |-- java
|       |   \-- com
|       |       \-- TechMarket
|       |           \-- techmarket
|       |               |-- application
|       |               \-- architecture
|       |                   \-- TechMarketIaArchitectureTest.java
|       \-- resources
|           \-- application-test.yml
|-- target
|   |-- spotless-index
|   |-- TECHMARKET-ia-1.0.0-SNAPSHOT.jar.original
|   |-- classes
|   |-- generated-sources
|   |-- generated-test-sources
|   |-- maven-archiver
|   |-- maven-status
|   \-- test-classes
\-- tests
    |-- architecture
    |-- integration
    |-- performance
    |-- security
    \-- unit
```
