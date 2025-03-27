package ru.yandex.practicum.filmorate;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilmorateApplication {
	private static final Logger log = LoggerFactory.getLogger(FilmorateApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(FilmorateApplication.class, args);
		((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME))
				.setLevel(Level.WARN);
	}

}
