package ru.qmbo.telegram_messages_sender.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static java.lang.String.format;

@Service
@Log4j2
public class RestService {
    public final String host;
    public static final String CALC_TEMPLATE_URL_WITH_CURRENCY = "%s/calc?chatId=%s&amount=%s&currency=%s";
    public static final String SUBSCRIBE_TEMPLATE_URL = "%s/users/add?chatId=%s";
    public static final String UNSUBSCRIBE_TEMPLATE_URL = "%s/users/dell?chatId=%s";
//    public static final String GET_ALL_USERS_TEMPLATE_URL = "%s/users/getAll";
    public static final String STATISTIC_TEMPLATE_URL = "%s/users/stats";

    public RestService(@Value("${mir.calc.host}") String host) {
        this.host = host;
    }

    private Optional<ResponseEntity<String>> sendRequest(String uri) {
        log.info("Try to send request: {}", uri);
        ResponseEntity<String> response = null;
        RestTemplate rt = new RestTemplate();
        try {
            response = rt.getForEntity(uri, String.class);
            log.info("Response: {}", response.toString());
        } catch (Exception e) {
            log.error("REST Template fail: {}", e.getMessage());
            log.error("Uri is: {}", uri);
        }
        return response == null ? Optional.empty() : Optional.of(response);
    }

    Optional<ResponseEntity<String>> sendCalculateRequest(long chatId, int amount, String currency) {
        return this.sendRequest(format(CALC_TEMPLATE_URL_WITH_CURRENCY, host, chatId, amount, currency));
    }

    Optional<ResponseEntity<String>> sendSubscribeRequest(long chatId) {
        return this.sendRequest(format(SUBSCRIBE_TEMPLATE_URL, host, chatId));
    }

    Optional<ResponseEntity<String>> sendUnsubscribeRequest(long chatId) {
        return this.sendRequest(format(UNSUBSCRIBE_TEMPLATE_URL, host, chatId));
    }

    Optional<ResponseEntity<String>> sendStatisticRequest() {
        return this.sendRequest(format(STATISTIC_TEMPLATE_URL, host));
    }
}
