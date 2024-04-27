FROM openjdk:21
WORKDIR /app
COPY ./target/bot.jar .
ENV TELEGRAM_TOKEN=${TELEGRAM_TOKEN}
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "-Dapp.telegram-token=$TELEGRAM_TOKEN", "-Dspring.kafka.bootstrap-server=$KAFKA_BOOTSTRAP_SERVERS", "./bot.jar"]
