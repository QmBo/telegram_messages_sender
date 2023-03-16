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
import org.springframework.stereotype.Service;
import ru.qmbo.telegram_messages_sender.unit.Currency;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.qmbo.telegram_messages_sender.unit.Currency.TENGE;

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
    public static final String CURRENCY = "currency";

    public static final String SUBSCRIBE = "/subscribe";
    public static final String UNSUBSCRIBE = "/unsubscribe";
    public static final String STASTISTIC = "/statistic";
//    public static final String SEND_ALL = "/sendall ";
    public static final String SORRY = "\uD83E\uDD14 Не могу разобрать, что ты от меня хочешь? Попробуй отправить целое число и я переведу его в рубли по курсу снятия с карты МИР в банкомате ВТБ. \uD83E\uDD11 А если хочешь, можешь отправить мне сумму и валюту. Например: \"100 р\". Тогда я подскажу эквивалент этой суммы в тенге. \uD83D\uDE09";
    public static final String AMOUNT = "amount";
    public static final String WRONG_COMMAND = "Такой команды нет!";
    private final Long adminChatId;
    private final TelegramBot bot;

    private final RestService restService;

    /**
     * Instantiates a new Telegram bot messages.
     *
     * @param adminChatId admin chat id
     * @param bot         the bot
     * @param restService the broadcast
     */
    public TelegramBotMessages(@Value("${telegram.admin.chat-id}") Long adminChatId, TelegramBot bot,
                               RestService restService) {
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
        if (message != null && message.text() != null) {
            log.info("New message: {}", message.text());
            if (message.text().startsWith("/")) {
                this.doCommand(update);
            } else {
                this.doRegularMessage(message);
            }
        }
    }

    private void doRegularMessage(Message message) {
        Map<String, String> context = this.findContext(message);
        final Long chatId = message.chat().id();
        if (context.isEmpty()) {
            this.messageSend(chatId, SORRY);
        } else {
            try {
                final int parseInt = Integer.parseInt(context.get(AMOUNT));
                String currency = context.getOrDefault(CURRENCY, TENGE);
                this.restService.sendCalculateRequest(chatId, parseInt, currency);
            } catch (NumberFormatException e) {
                log.warn("Number format exception: {}", e.getMessage());
            }
        }
    }

    private Map<String, String> findContext(Message message) {
        Map<String, String> result = new HashMap<>();
        List<String> words = Arrays.stream(message.text().split(" "))
                .filter(s -> s.length() != 0)
                .collect(Collectors.toList());
        if (words.size() == 1 && this.isNumber(words.get(0))) {
            result.put(AMOUNT, words.get(0));
        } else if (words.size() == 2) {
            Map<String, String> availableCurrency = Currency.getCurrency();
            if (this.isNumber(words.get(0)) && availableCurrency.containsKey(words.get(1))) {
                result.put(AMOUNT, words.get(0));
                result.put(CURRENCY, availableCurrency.get(words.get(1)));
            } else if (this.isNumber(words.get(1)) && availableCurrency.containsKey(words.get(0))) {
                result.put(AMOUNT, words.get(1));
                result.put(CURRENCY, availableCurrency.get(words.get(0)));
            }
        }
        return result;
    }

    private boolean isNumber(String check) {
        boolean result = false;
        try {
            Integer.parseInt(check);
            result = true;
        } catch (NumberFormatException e) {
            log.warn("Number format exception: {}", e.getMessage());
        }
        return result;
    }

    private void doCommand(Update update) {
        Message message = update.message();
        if (SUBSCRIBE.equals(message.text())) {
            this.restService.sendSubscribeRequest(message.chat().id());
        } else if (UNSUBSCRIBE.equals(message.text())) {
            this.restService.sendUnsubscribeRequest(message.chat().id());
        } else if (STASTISTIC.equals(message.text()) && message.chat().id().equals(this.adminChatId)) {
            this.restService.sendStatisticRequest();
//        } else if (message.text().startsWith(SEND_ALL) && message.chat().id().equals(this.adminChatId)) {
//            this.restService.sendRequest(format(GET_ALL_USERS_TEMPLATE_URL, host))
//                    .ifPresent(stringResponseEntity -> this.sentToAll(stringResponseEntity, message));
        } else {
            this.messageSend(message.chat().id(), WRONG_COMMAND);
            log.info("Uncorrected command.");
        }
    }

//    private void sentToAll(ResponseEntity<String> stringResponseEntity, Message message) {
//        String textMessage = message.text().replace(SEND_ALL, "");
//        log.info("Try to send to all the message: {}", textMessage);
//        if (stringResponseEntity.getStatusCode().equals(HttpStatus.OK) && stringResponseEntity.getBody() != null
//                && !stringResponseEntity.getBody().isEmpty()) {
//            try {
//                Arrays.stream(stringResponseEntity.getBody().split(","))
//                        .forEach(chatId -> this.messageSend(Long.parseLong(chatId), textMessage));
//                log.info("Messages send.");
//            } catch (NumberFormatException e) {
//                log.error("Response Entity contain bad data: {}\n{}", stringResponseEntity.getBody(), e.getMessage());
//            }
//        } else {
//            log.warn("Response code not 200: {}", stringResponseEntity.toString());
//        }
//    }


}
