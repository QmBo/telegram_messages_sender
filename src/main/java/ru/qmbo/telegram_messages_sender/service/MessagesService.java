package ru.qmbo.telegram_messages_sender.service;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.stereotype.Service;

/**
 * MessagesService
 *
 * @author Victor Egorov (qrioflat@gmail.com).
 * @version 0.1
 * @since 08.12.2022
 */
@Service
@Log4j2
public class MessagesService {
    private final TelegramBotMessages telegramBotMessages;

    /**
     * Instantiates a new Messages service.
     *
     * @param telegramBotMessages the telegram bot messages
     */
    public MessagesService(TelegramBotMessages telegramBotMessages) {
        this.telegramBotMessages = telegramBotMessages;
    }

    /**
     * Get new message. Parse it to two parts separate by "_".
     * Normal format: "chatId_message"
     *
     * @param inputMessage the input message
     */
    public void putNewMessage(String inputMessage) {
        val split = inputMessage.split("_");
        if (split.length != 2) {
            log.warn("Wrong message format. Normal format: \"chatId_message\" but now: \"{}\"", inputMessage);
        } else {
            val chatId = Long.parseLong(split[0]);
            this.telegramBotMessages.messageSend(chatId, split[1]);
        }
    }
}
