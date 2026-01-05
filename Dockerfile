FROM vegardit/graalvm-maven:latest-java21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn package -Pnative -DskipTests

FROM debian:12-slim

WORKDIR /work/

COPY --from=build /app/target/*-runner /work/application

RUN chmod +x /work/application

EXPOSE 8080

ENTRYPOINT ["/work/application"]
