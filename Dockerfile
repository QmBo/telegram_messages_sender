FROM openjdk:11-jdk-oracle
WORKDIR telegram_messages_sender
ADD target/telegram_messages_sender.jar app.jar
ENTRYPOINT java -jar app.jar