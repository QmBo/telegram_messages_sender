package ru.qmbo.telegram_messages_sender.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static java.lang.String.format;
import static ru.qmbo.telegram_messages_sender.service.RestService.*;

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
    public static final String SUBSCRIBE = "/subscribe";
    public static final String UNSUBSCRIBE = "/unsubscribe";
    public static final String SEND_ALL = "/sendall ";
    public static final String SORRY = "Пока я понимаю только целые числа =(";
    public final String host;
    private final Long adminChatId;
    private final TelegramBot bot;

    private final RestService restService;

    /**
     * Instantiates a new Telegram bot messages.
     *
     * @param host             calculator host
     * @param adminChatId      admin chat id
     * @param bot              the bot
     * @param restService the broadcast
     */
    public TelegramBotMessages(@Value("${mir.calc.host}") String host,
                               @Value("${telegram.admin.chat-id}") Long adminChatId, TelegramBot bot,
                               RestService restService) {
        this.host = host;
        this.adminChatId = adminChatId;
        this.bot = bot;
        this.restService = restService;
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
        Message message = update.message();
        if (message != null) {
            if (message.text().startsWith("/")) {
                this.doCommand(update);
            } else {
                boolean parsOk = false;
                try {
                    int parseInt = Integer.parseInt(message.text());
                    this.restService.sendRequest(format(CALC_TEMPLATE_URL, host, message.chat().id(), parseInt));
                    parsOk = true;
                } catch (Exception e) {
                    log.warn("Number format exception: {}", e.getMessage());
                }
                if (!parsOk) {
                    this.messageSend(message.chat().id(), SORRY);
                }
            }
        }
    }

    private void doCommand(Update update) {
        Message message = update.message();
        if (SUBSCRIBE.equals(message.text())) {
            this.restService.sendRequest(format(SUBSCRIBE_TEMPLATE_URL, host, message.chat().id()));
        } else if (UNSUBSCRIBE.equals(message.text())) {
            this.restService.sendRequest(format(UNSUBSCRIBE_TEMPLATE_URL, host, message.chat().id()));
        } else if (message.text().startsWith(SEND_ALL) && message.chat().id().equals(this.adminChatId)) {
            this.restService.sendRequest(GET_ALL_USERS_TEMPLATE_URL)
                    .ifPresent(stringResponseEntity -> this.sentToAll(stringResponseEntity, message));
        } else {
            this.messageSend(message.chat().id(), "Такой команды нет!");
        }
    }

    private void sentToAll(ResponseEntity<String> stringResponseEntity, Message message) {
        String textMessage = message.text().replace(SEND_ALL, "");
        if (stringResponseEntity.getStatusCode().equals(HttpStatus.OK) && stringResponseEntity.getBody() != null
                && !stringResponseEntity.getBody().isEmpty()) {
            try {
                Arrays.stream(stringResponseEntity.getBody().split(","))
                        .forEach(chatId -> this.messageSend(Long.parseLong(chatId), textMessage));
            } catch (NumberFormatException e) {
                log.error("Response Entity contain bad data: {}\n{}", stringResponseEntity.getBody(), e.getMessage());
            }
        } else {
            log.warn("Response code not 200: {}", stringResponseEntity.toString());
        }
    }


}
