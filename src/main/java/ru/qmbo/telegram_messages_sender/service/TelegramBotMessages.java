package ru.qmbo.telegram_messages_sender.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * TelegramBotMessages
 *
 * @author Victor Egorov (qrioflat@gmail.com).
 * @version 0.1
 * @since 08.12.2022
 */
@Service
@Log4j2
public class TelegramBotMessages {
    /**
     * The constant TEMPLATE_URL.
     */
    public static final String TEMPLATE_URL = "%s/calc?chatId=%s&amount=%s";
    /**
     * The Host.
     */
    public final String host;
    private final TelegramBot bot;

    /**
     * Instantiates a new Telegram bot messages.
     *
     * @param host calculator host
     * @param bot  the bot
     */
    public TelegramBotMessages(@Value("${mir.calc.host}") String host, TelegramBot bot) {
        this.host = host;
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

    /**
     * Run listener.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void runListener() {
        this.bot.setUpdatesListener(updates -> {
            updates.forEach(this::requestParser);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    /**
     * Request parser.
     *
     * @param update the update
     */
    public void requestParser(Update update) {
        if (update.message() != null) {
            boolean parsOk = false;
            try {
                int parseInt = Integer.parseInt(update.message().text());
                this.sendRequest(parseInt, update.message().chat().id());
                parsOk = true;
            } catch (Exception e) {
                log.warn("Number format exception: {}", e.getMessage());
            }
            if (!parsOk) {
                this.messageSend(update.message().chat().id(), "Пока я понимаю только целые числа =(");
            }
        }
    }


    private void sendRequest(int amount, Long chatId) {
        RestTemplate rt =  new RestTemplate();
        final String uri = String.format(TEMPLATE_URL, host, chatId, amount);
        try {
            rt.getForEntity(uri, String.class);
        } catch (Exception e) {
            log.error("REST Template fail: {}", e.getMessage());
            log.error("Uri is: {}", uri);
        }
    }
    
}
