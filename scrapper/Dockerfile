FROM openjdk:21
WORKDIR /app
COPY ./target/scrapper.jar .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.datasource.url=$DATABASE_URL", "-Dspring.kafka.bootstrap-server=$KAFKA_BOOTSTRAP_SERVERS", "./scrapper.jar"]
