package ru.qmbo.telegram_messages_sender.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;

/**
 * TelegramBotMessages
 *
 * @author Victor Egorov (qrioflat@gmail.com).
 * @version 0.1
 * @since 08.12.2022
 */
@Service
public class TelegramBotMessages {
    private final TelegramBot bot;

    /**
     * Instantiates a new Telegram bot messages.
     *
     * @param bot the bot
     */
    public TelegramBotMessages(TelegramBot bot) {
        this.bot = bot;
    }

    /**
     * Message send.
     *
     * @param chatId  the chat id
     * @param message the message
     */
    public void messageSend(Long chatId, String message) {
        this.bot.execute(new SendMessage(chatId, message));
    }
}
