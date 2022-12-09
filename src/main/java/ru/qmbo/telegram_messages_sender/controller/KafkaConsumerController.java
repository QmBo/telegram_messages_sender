package ru.qmbo.telegram_messages_sender.controller;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;
import ru.qmbo.telegram_messages_sender.dto.Message;
import ru.qmbo.telegram_messages_sender.service.MessagesService;

/**
 * KafkaConsumerController
 *
 * @author Victor Egorov (qrioflat@gmail.com).
 * @version 0.1
 * @since 08.12.2022
 */
@Controller
@Log4j2
public class KafkaConsumerController {
    private final MessagesService messagesService;

    /**
     * Instantiates a new Kafka consumer controller.
     *
     * @param messagesService the messages service
     */
    public KafkaConsumerController(MessagesService messagesService) {
        this.messagesService = messagesService;
    }

    /**
     * Gets message.
     *
     * @param input the input
     */
    @KafkaListener(topics = {"${kafka.topic.dto}"})
    public void getMessageDTO(ConsumerRecord<Integer, Message> input) {
        final val inputMessage = input.value();
        log.debug("Has new message from Kafka: {}", inputMessage);
        this.messagesService.putNewMessage(inputMessage);
    }

}
