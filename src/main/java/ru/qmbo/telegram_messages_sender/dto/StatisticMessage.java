package ru.qmbo.telegram_messages_sender.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * StatisticMessage
 *
 * @author Victor Egorov (qrioflat@gmail.com).
 * @version 0.1
 * @since 20.03.2023
 */
@Data
@Accessors(chain = true)
public class StatisticMessage {
    @JsonAlias("chat_id")
    private Long chatId;
    private String message;
}
