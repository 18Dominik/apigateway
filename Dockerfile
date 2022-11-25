FROM openjdk:11

COPY ./target/api-1.0.0-SNAPSHOT-runner.jar api-1.0.0-SNAPSHOT-runner.jar

ENTRYPOINT [ "java", "-jar", "/api-1.0.0-SNAPSHOT-runner.jar"]

CMD ["api-1.0.0-SNAPSHOT-runner.jar"]