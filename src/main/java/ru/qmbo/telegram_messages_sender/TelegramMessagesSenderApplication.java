package ru.qmbo.telegram_messages_sender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TelegramMessagesSenderApplication
 *
 * @author Victor Egorov (qrioflat@gmail.com).
 * @version 0.1
 * @since 08.12.2022
 */
@SpringBootApplication
public class TelegramMessagesSenderApplication {

	/**
	 * The entry point of application.
	 *
	 * @param args the input arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(TelegramMessagesSenderApplication.class, args);
	}

}
