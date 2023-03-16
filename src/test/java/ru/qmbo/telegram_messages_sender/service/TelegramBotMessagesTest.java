package ru.qmbo.telegram_messages_sender.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static ru.qmbo.telegram_messages_sender.service.TelegramBotMessages.SORRY;
import static ru.qmbo.telegram_messages_sender.service.TelegramBotMessages.WRONG_COMMAND;

@SpringBootTest
@Testcontainers
class TelegramBotMessagesTest {
    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaGroupId;

    @Value("${kafka.topic.string}")
    private String kafkaTopic;

    private KafkaConsumer<String, String> consumer;

    private KafkaProducer<String, String> producer;

    @Container
    public static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest"));


    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @BeforeEach
    public void setUp() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaGroupId);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        properties.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        properties.put(JsonDeserializer.VALUE_DEFAULT_TYPE, String.class);

        consumer = new KafkaConsumer<>(properties);

        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        producer = new KafkaProducer<>(producerProps);
    }

    @Mock
    private Update update;
    @Mock
    private Message message;
    @Mock
    private Chat chat;
    @Autowired
    private TelegramBotMessages telegramBotMessages;
    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;
    @Captor
    private ArgumentCaptor<Integer> integerArgumentCaptor;
    @Captor
    private ArgumentCaptor<Long> longArgumentCaptor;
    @Captor
    private ArgumentCaptor<SendMessage> messageArgumentCaptor;

    @MockBean
    private RestService restService;

    @MockBean
    private TelegramBot bot;

    @Test
    public void whetRegularCalculateThenSendCalculateRequest() {
        when(message.text()).thenReturn("100");
        when(chat.id()).thenReturn(123456L);
        when(message.chat()).thenReturn(chat);
        when(update.message()).thenReturn(message);
        telegramBotMessages.requestParser(update);
        verify(restService).sendCalculateRequest(
                longArgumentCaptor.capture(),
                integerArgumentCaptor.capture(),
                stringArgumentCaptor.capture()
        );
        assertThat(integerArgumentCaptor.getValue()).isEqualTo(100);
        assertThat(longArgumentCaptor.getValue()).isEqualTo(123456L);
        assertThat(stringArgumentCaptor.getValue()).isEqualTo("tenge");
    }

    @Test
    public void whetCalculateWithCurrencyTenThenSendCalculateRequest() {
        when(message.text()).thenReturn("100 т");
        when(chat.id()).thenReturn(123456L);
        when(message.chat()).thenReturn(chat);
        when(update.message()).thenReturn(message);
        telegramBotMessages.requestParser(update);
        verify(restService).sendCalculateRequest(
                longArgumentCaptor.capture(),
                integerArgumentCaptor.capture(),
                stringArgumentCaptor.capture()
        );
        assertThat(integerArgumentCaptor.getValue()).isEqualTo(100);
        assertThat(longArgumentCaptor.getValue()).isEqualTo(123456L);
        assertThat(stringArgumentCaptor.getValue()).isEqualTo("tenge");
    }

    @Test
    public void whetCalculateWithCurrencyRubThenSendCalculateRequest() {
        when(message.text()).thenReturn("р.  100");
        when(chat.id()).thenReturn(123456L);
        when(message.chat()).thenReturn(chat);
        when(update.message()).thenReturn(message);
        telegramBotMessages.requestParser(update);
        verify(restService).sendCalculateRequest(
                longArgumentCaptor.capture(),
                integerArgumentCaptor.capture(),
                stringArgumentCaptor.capture()
        );
        assertThat(integerArgumentCaptor.getValue()).isEqualTo(100);
        assertThat(longArgumentCaptor.getValue()).isEqualTo(123456L);
        assertThat(stringArgumentCaptor.getValue()).isEqualTo("rub");
    }


    @Test
    public void whetCommandSubscribeThenSendSubscribeRequest() {
        when(message.text()).thenReturn("/subscribe");
        when(chat.id()).thenReturn(99998888L);
        when(message.chat()).thenReturn(chat);
        when(update.message()).thenReturn(message);
        telegramBotMessages.requestParser(update);
        verify(restService).sendSubscribeRequest(
                longArgumentCaptor.capture()
        );
        assertThat(longArgumentCaptor.getValue()).isEqualTo(99998888L);
    }

    @Test
    public void whetCommandUnsubscribeThenSendCalculateRequest() {
        when(message.text()).thenReturn("/unsubscribe");
        when(chat.id()).thenReturn(99998888L);
        when(message.chat()).thenReturn(chat);
        when(update.message()).thenReturn(message);
        telegramBotMessages.requestParser(update);
        verify(restService).sendUnsubscribeRequest(
                longArgumentCaptor.capture()
        );
        assertThat(longArgumentCaptor.getValue()).isEqualTo(99998888L);
    }

    @Test
    public void whetUnknownCommandThenSendWrongCommandMessage() {
        when(message.text()).thenReturn("/uncorrected");
        when(chat.id()).thenReturn(99998888L);
        when(message.chat()).thenReturn(chat);
        when(update.message()).thenReturn(message);
        telegramBotMessages.requestParser(update);
        verify(bot).execute(
                messageArgumentCaptor.capture()
        );
        assertThat((messageArgumentCaptor.getValue().getParameters()).get("chat_id")).isEqualTo(99998888L);
        assertThat((messageArgumentCaptor.getValue().getParameters()).get("text")).isEqualTo(WRONG_COMMAND);
    }

    @Test
    public void whetUnknownContextThenSendSorryMessage() {
        when(message.text()).thenReturn("т 100 р");
        when(chat.id()).thenReturn(99998888L);
        when(message.chat()).thenReturn(chat);
        when(update.message()).thenReturn(message);
        telegramBotMessages.requestParser(update);
        verify(bot).execute(
                messageArgumentCaptor.capture()
        );
        assertThat((messageArgumentCaptor.getValue().getParameters()).get("chat_id")).isEqualTo(99998888L);
        assertThat((messageArgumentCaptor.getValue().getParameters()).get("text")).isEqualTo(SORRY);
    }

    @Test
    public void whetOnlyStringThenSendSorryMessage() {
        when(message.text()).thenReturn("р");
        when(chat.id()).thenReturn(99998888L);
        when(message.chat()).thenReturn(chat);
        when(update.message()).thenReturn(message);
        telegramBotMessages.requestParser(update);
        verify(bot).execute(
                messageArgumentCaptor.capture()
        );
        assertThat((messageArgumentCaptor.getValue().getParameters()).get("chat_id")).isEqualTo(99998888L);
        assertThat((messageArgumentCaptor.getValue().getParameters()).get("text")).isEqualTo(SORRY);
    }

    @Test
    public void whetCommandStatisticFromAdminThenSendStatisticRequest() {
        when(message.text()).thenReturn("/statistic");
        when(chat.id()).thenReturn(303775921L);
        when(message.chat()).thenReturn(chat);
        when(update.message()).thenReturn(message);
        telegramBotMessages.requestParser(update);
        verify(restService, times(1)).sendStatisticRequest();
    }

    @Test
    public void whetCommandStatisticFromUserThenWrongCommandMessage() {
        when(message.text()).thenReturn("/statistic");
        when(chat.id()).thenReturn(9900L);
        when(message.chat()).thenReturn(chat);
        when(update.message()).thenReturn(message);
        telegramBotMessages.requestParser(update);
        verify(restService, times(0)).sendStatisticRequest();
        verify(bot).execute(
                messageArgumentCaptor.capture()
        );
        assertThat((messageArgumentCaptor.getValue().getParameters()).get("chat_id")).isEqualTo(9900L);
        assertThat((messageArgumentCaptor.getValue().getParameters()).get("text")).isEqualTo(WRONG_COMMAND);

    }

}