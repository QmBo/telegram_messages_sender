package ru.qmbo.telegram_messages_sender.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static java.lang.String.format;

@Service
@Log4j2
public class RestService {
    @Value("${mir.calc.host}")
    public String host;
    public static final String CALC_TEMPLATE_URL_WITH_CURRENCY = "%s/calc?chatId=%s&amount=%s&currency=%s";

    private void sendRequest(String uri) {
        log.info("Try to send request: {}", uri);
        try {
            ResponseEntity<String> response = new RestTemplate().getForEntity(uri, String.class);
            log.info("Response: {}", response.toString());
        } catch (Exception e) {
            log.error("REST Template fail: {}", e.getMessage());
            log.error("Uri is: {}", uri);
        }
    }

    public void sendCalculateRequest(long chatId, int amount, String currency) {
        this.sendRequest(format(CALC_TEMPLATE_URL_WITH_CURRENCY, host, chatId, amount, currency));
    }
}
