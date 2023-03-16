package ru.qmbo.telegram_messages_sender.unit;

import java.util.HashMap;
import java.util.Map;

public class Currency {

    public static final String RUB = "rub";
    public static final String TENGE = "tenge";

    public static Map<String, String> getCurrency(){
        Map<String, String> result = new HashMap<>();
        result.put("р", RUB);
        result.put("р.", RUB);
        result.put("руб", RUB);
        result.put("руб.", RUB);
        result.put("рубль", RUB);
        result.put("рубли", RUB);
        result.put("т", TENGE);
        result.put("т.", TENGE);
        result.put("тен", TENGE);
        result.put("тен.", TENGE);
        result.put("тенге", TENGE);
        return result;
    }
}
