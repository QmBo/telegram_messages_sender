package ru.qmbo.telegram_messages_sender.config;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TelegramConfig
 *
 * @author Victor Egorov (qrioflat@gmail.com).
 * @version 0.1
 * @since 08.12.2022
 */
@Configuration
public class TelegramConfig {
    /**
     * Gets telegram bot.
     *
     * @param token the token
     * @return the telegram bot
     */
    @Bean
    public TelegramBot getTelegramBot(@Value("${telegram.bot.token}") String token) {
        return new TelegramBot(token);
    }
}
