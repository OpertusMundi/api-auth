# vim: set syntax=dockerfile: 

FROM maven:3.6.3-openjdk-11 AS maven-build

WORKDIR /app/

COPY pom.xml ./
COPY common/ ./common/
COPY auth_subrequest/ ./auth_subrequest/
RUN mvn -B -DskipTests install


FROM eclipse-temurin:11-jre-alpine 

ARG git_commit=

ENV GIT_COMMIT="${git_commit}"

WORKDIR /app/

RUN addgroup quarkus && adduser -H -D -G quarkus quarkus
RUN mkdir config logs && chown quarkus:quarkus config logs

COPY --from=maven-build --chown=quarkus:quarkus /app/auth_subrequest/target/quarkus-app /app/quarkus-app

USER quarkus
CMD [ "java", "-jar", "/app/quarkus-app/quarkus-run.jar" ]
