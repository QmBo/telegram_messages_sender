package ru.qmbo.telegram_messages_sender.service;

import com.pengrad.telegrambot.model.Message;

public interface RequestService {

    void sendSubscribeRequest(Long chatId);

    void sendUnsubscribeRequest(Long chatId);

    void sendStatisticRequest(Long chatId);

    void sendCollectUserRequest(Message message);

    void sendGetAllUsersRequest(Long id);
}
