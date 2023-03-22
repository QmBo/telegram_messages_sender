package ru.qmbo.telegram_messages_sender.service;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.qmbo.telegram_messages_sender.dto.CollectMessage;
import ru.qmbo.telegram_messages_sender.dto.GetAllUsersMessage;
import ru.qmbo.telegram_messages_sender.dto.StatisticMessage;
import ru.qmbo.telegram_messages_sender.dto.SubscribeMessage;

@Service
@RequiredArgsConstructor
public class RabbitMQService implements RequestService {
    private final RabbitTemplate rabbitTemplate;
    @Value("${rabbit.exchangeTopic}")
    private String exchangeTopic;
    @Value("${rabbit.key.subscribe}")
    private String subscribe;
    @Value("${rabbit.key.unsubscribe}")
    private String unsubscribe;
    @Value("${rabbit.key.collect}")
    private String collect;
    @Value("${rabbit.key.statistic}")
    private String statistic;
    @Value("${rabbit.key.getAllUsers}")
    private String getAllUsers;

    @Override
    public void sendSubscribeRequest(Long chatId) {
        this.rabbitTemplate.convertAndSend(
                this.exchangeTopic, this.subscribe, new SubscribeMessage().setChatId(chatId)
        );
    }

    @Override
    public void sendUnsubscribeRequest(Long chatId) {
        this.rabbitTemplate.convertAndSend(
                this.exchangeTopic, this.unsubscribe, new SubscribeMessage().setChatId(chatId)
        );
    }

    @Override
    public void sendStatisticRequest(Long chatId) {
        this.rabbitTemplate.convertAndSend(
                this.exchangeTopic, this.statistic, new StatisticMessage().setChatId(chatId)
        );
    }

    @Override
    public void sendCollectUserRequest(Message message) {
        final Chat chat = message.chat();
        this.rabbitTemplate.convertAndSend(
                this.exchangeTopic, this.collect,
                new CollectMessage()
                        .setChatId(chat.id())
                        .setName(String.format("%s %s (%s)", chat.firstName(), chat.lastName(), chat.username()))
        );
    }

    @Override
    public void sendGetAllUsersRequest(Long id) {
        this.rabbitTemplate.convertAndSend(
                this.exchangeTopic, this.getAllUsers, new GetAllUsersMessage().setId(id)
        );
    }
}
