FROM maven:3.6.3-jdk-11-slim AS MAVEN_BUILD
COPY ./ ./
RUN mvn clean package

FROM openjdk:11-jre-slim
WORKDIR /usr/src/bitcoinrate
COPY --from=MAVEN_BUILD target/Bitcoinrate-jar-with-dependencies.jar bitcoinrate.jar
ENTRYPOINT ["java", "-jar", "bitcoinrate.jar"]
CMD [""]