package ru.qmbo.telegram_messages_sender.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@Log4j2
public class RestService {
    public static final String CALC_TEMPLATE_URL = "%s/calc?chatId=%s&amount=%s";
    public static final String SUBSCRIBE_TEMPLATE_URL = "%s/users/add?chatId=%s";
    public static final String UNSUBSCRIBE_TEMPLATE_URL = "%s/users/dell?chatId=%s";
    public static final String GET_ALL_USERS_TEMPLATE_URL = "%s/users/getAll";

    Optional<ResponseEntity<String>> sendRequest(String uri) {
        ResponseEntity<String> response = null;
        RestTemplate rt = new RestTemplate();
        try {
            response = rt.getForEntity(uri, String.class);
        } catch (Exception e) {
            log.error("REST Template fail: {}", e.getMessage());
            log.error("Uri is: {}", uri);
        }
        return response == null ? Optional.empty() : Optional.of(response);
    }
}
