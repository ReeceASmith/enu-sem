FROM openjdk:latest
COPY ./target/enu-sem-0.1.0.5-jar-with-dependencies.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "enu-sem-0.1.0.5-jar-with-dependencies.jar"]