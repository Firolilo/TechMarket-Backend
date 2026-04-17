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
|       |-- maven-wrapper.jar
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
|   |   |           \-- peopleuacademic
|   |   |               |-- academicgovernance
|   |   |               |   |-- academicregulations
|   |   |               |   |   |-- .gitkeep
|   |   |               |   |   |-- api
|   |   |               |   |   |   |-- .gitkeep
|   |   |               |   |   |   |-- admin
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   \-- internal
|   |   |               |   |   |       \-- .gitkeep
|   |   |               |   |   |-- application
|   |   |               |   |   |   |-- .gitkeep
|   |   |               |   |   |   |-- dto
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   |-- policy
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   |-- service
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   \-- usecase
|   |   |               |   |   |       \-- .gitkeep
|   |   |               |   |   |-- bootstrap
|   |   |               |   |   |   \-- .gitkeep
|   |   |               |   |   |-- domain
|   |   |               |   |   |   |-- .gitkeep
|   |   |               |   |   |   |-- academicregulation
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   |-- academicregulationversion
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   |-- auditsnapshot
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   |-- conflict
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   |-- evidencerelation
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   |-- publicationlog
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   |-- regulationparameterlink
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   |-- regulationscope
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   \-- valueobject
|   |   |               |   |   |       \-- .gitkeep
|   |   |               |   |   \-- infrastructure
|   |   |               |   |       |-- .gitkeep
|   |   |               |   |       |-- cache
|   |   |               |   |       |   |-- .gitkeep
|   |   |               |   |       |   \-- redis
|   |   |               |   |       |       \-- .gitkeep
|   |   |               |   |       |-- digitalasset
|   |   |               |   |       |   \-- .gitkeep
|   |   |               |   |       |-- events
|   |   |               |   |       |   \-- .gitkeep
|   |   |               |   |       |-- iam
|   |   |               |   |       |   \-- .gitkeep
|   |   |               |   |       |-- observability
|   |   |               |   |       |   \-- .gitkeep
|   |   |               |   |       |-- persistence
|   |   |               |   |       |   |-- .gitkeep
|   |   |               |   |       |   \-- postgres
|   |   |               |   |       |       \-- .gitkeep
|   |   |               |   |       \-- projection
|   |   |               |   |           |-- .gitkeep
|   |   |               |   |           \-- mongo
|   |   |               |   |               \-- .gitkeep
|   |   |               |   |-- contextresolutionengine
|   |   |               |   |   |-- .gitkeep
|   |   |               |   |   |-- api
|   |   |               |   |   |   |-- .gitkeep
|   |   |               |   |   |   |-- admin
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   \-- internal
|   |   |               |   |   |       \-- .gitkeep
|   |   |               |   |   |-- application
|   |   |               |   |   |   |-- .gitkeep
|   |   |               |   |   |   |-- dto
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   |-- policy
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   |-- service
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   \-- usecase
|   |   |               |   |   |       \-- .gitkeep
|   |   |               |   |   |-- bootstrap
|   |   |               |   |   |   \-- .gitkeep
|   |   |               |   |   |-- domain
|   |   |               |   |   |   |-- .gitkeep
|   |   |               |   |   |   |-- ambiguity
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   |-- candidaterule
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   |-- evidencebundle
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   |-- prioritymatrix
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   |-- resolutioncontext
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   |-- resolutionresult
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   |-- specificityscore
|   |   |               |   |   |   |   \-- .gitkeep
|   |   |               |   |   |   \-- valueobject
|   |   |               |   |   |       \-- .gitkeep
|   |   |               |   |   \-- infrastructure
|   |   |               |   |       |-- .gitkeep
|   |   |               |   |       |-- cache
|   |   |               |   |       |   |-- .gitkeep
|   |   |               |   |       |   \-- redis
|   |   |               |   |       |       \-- .gitkeep
|   |   |               |   |       |-- digitalasset
|   |   |               |   |       |   \-- .gitkeep
|   |   |               |   |       |-- events
|   |   |               |   |       |   \-- .gitkeep
|   |   |               |   |       |-- iam
|   |   |               |   |       |   \-- .gitkeep
|   |   |               |   |       |-- observability
|   |   |               |   |       |   \-- .gitkeep
|   |   |               |   |       |-- parameters
|   |   |               |   |       |   \-- .gitkeep
|   |   |               |   |       |-- persistence
|   |   |               |   |       |   |-- .gitkeep
|   |   |               |   |       |   \-- postgres
|   |   |               |   |       |       \-- .gitkeep
|   |   |               |   |       |-- projection
|   |   |               |   |       |   |-- .gitkeep
|   |   |               |   |       |   \-- mongo
|   |   |               |   |       |       \-- .gitkeep
|   |   |               |   |       \-- regulations
|   |   |               |   |           \-- .gitkeep
|   |   |               |   \-- globalparameters
|   |   |               |       |-- api
|   |   |               |       |   |-- .gitkeep
|   |   |               |       |   |-- admin
|   |   |               |       |   |   \-- GlobalParametersTestController.java
|   |   |               |       |   \-- internal
|   |   |               |       |       \-- .gitkeep
|   |   |               |       |-- application
|   |   |               |       |   |-- .gitkeep
|   |   |               |       |   |-- dto
|   |   |               |       |   |   \-- .gitkeep
|   |   |               |       |   |-- policy
|   |   |               |       |   |   \-- .gitkeep
|   |   |               |       |   |-- service
|   |   |               |       |   |   \-- .gitkeep
|   |   |               |       |   \-- usecase
|   |   |               |       |       \-- .gitkeep
|   |   |               |       |-- bootstrap
|   |   |               |       |   |-- .gitkeep
|   |   |               |       |   |-- OpenApiConfig.java
|   |   |               |       |   |-- SecurityConfig.java
|   |   |               |       |   \-- TECHMARKETIaApplication.java
|   |   |               |       |-- domain
|   |   |               |       |   |-- .gitkeep
|   |   |               |       |   |-- academicparameter
|   |   |               |       |   |   \-- .gitkeep
|   |   |               |       |   |-- academicparameterversion
|   |   |               |       |   |   \-- .gitkeep
|   |   |               |       |   |-- auditsnapshot
|   |   |               |       |   |   \-- .gitkeep
|   |   |               |       |   |-- conflict
|   |   |               |       |   |   \-- .gitkeep
|   |   |               |       |   |-- evidencerelation
|   |   |               |       |   |   \-- .gitkeep
|   |   |               |       |   |-- parameterscope
|   |   |               |       |   |   \-- .gitkeep
|   |   |               |       |   |-- publicationlog
|   |   |               |       |   |   \-- .gitkeep
|   |   |               |       |   \-- valueobject
|   |   |               |       |       \-- .gitkeep
|   |   |               |       \-- infrastructure
|   |   |               |           |-- .gitkeep
|   |   |               |           |-- cache
|   |   |               |           |   |-- .gitkeep
|   |   |               |           |   \-- redis
|   |   |               |           |       \-- .gitkeep
|   |   |               |           |-- digitalasset
|   |   |               |           |   \-- .gitkeep
|   |   |               |           |-- events
|   |   |               |           |   \-- .gitkeep
|   |   |               |           |-- iam
|   |   |               |           |   \-- .gitkeep
|   |   |               |           |-- observability
|   |   |               |           |   \-- .gitkeep
|   |   |               |           |-- persistence
|   |   |               |           |   |-- .gitkeep
|   |   |               |           |   \-- postgres
|   |   |               |           |       \-- .gitkeep
|   |   |               |           \-- projection
|   |   |               |               |-- .gitkeep
|   |   |               |               \-- mongo
|   |   |               |                   \-- .gitkeep
|   |   |               |-- integration
|   |   |               |   |-- batch
|   |   |               |   |   \-- .gitkeep
|   |   |               |   |-- events
|   |   |               |   |   \-- .gitkeep
|   |   |               |   |-- listeners
|   |   |               |   |   \-- .gitkeep
|   |   |               |   \-- schedulers
|   |   |               |       \-- .gitkeep
|   |   |               |-- security
|   |   |               |   |-- config
|   |   |               |   |   \-- .gitkeep
|   |   |               |   |-- filter
|   |   |               |   |   \-- .gitkeep
|   |   |               |   |-- jwt
|   |   |               |   |   \-- .gitkeep
|   |   |               |   |-- permission
|   |   |               |   |   \-- .gitkeep
|   |   |               |   |-- rbac
|   |   |               |   |   \-- .gitkeep
|   |   |               |   \-- tenant
|   |   |               |       \-- .gitkeep
|   |   |               \-- shared
|   |   |                   |-- audit
|   |   |                   |   \-- .gitkeep
|   |   |                   |-- common-dto
|   |   |                   |   \-- .gitkeep
|   |   |                   |-- commondto
|   |   |                   |   \-- .gitkeep
|   |   |                   |-- constants
|   |   |                   |   \-- .gitkeep
|   |   |                   |-- util
|   |   |                   |   \-- .gitkeep
|   |   |                   \-- validation
|   |   |                       \-- .gitkeep
|   |   \-- resources
|   |       |-- application-dev.yml
|   |       |-- application-docker.yml
|   |       |-- application-prod.yml
|   |       |-- application.yml
|   |       \-- db
|   |           \-- migration
|   |               \-- V1__global_parameters_placeholder.sql
|   \-- test
|       |-- java
|       |   \-- com
|       |       \-- TechMarket
|       |           \-- peopleuacademic
|       |               |-- application
|       |               |   \-- .gitkeep
|       |               \-- architecture
|       |                   \-- TECHMARKETIaArchitectureTest.java
|       \-- resources
|           \-- application-test.yml
\-- tests
    |-- architecture
    |   \-- .gitkeep
    |-- integration
    |   \-- .gitkeep
    |-- performance
    |   \-- .gitkeep
    |-- security
    |   \-- .gitkeep
    \-- unit
        \-- .gitkeep
```
