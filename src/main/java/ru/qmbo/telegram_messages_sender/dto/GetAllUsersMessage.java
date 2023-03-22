package ru.qmbo.telegram_messages_sender.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * SubscribeMessage
 *
 * @author Victor Egorov (qrioflat@gmail.com).
 * @version 0.1
 * @since 20.03.2023
 */
@Data
@Accessors(chain = true)
public class GetAllUsersMessage {
    private Long id;
}
