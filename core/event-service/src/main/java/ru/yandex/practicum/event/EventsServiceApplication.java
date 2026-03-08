package ru.yandex.practicum.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.statsclient.client.config.StatsClientConfig;

@EnableFeignClients(basePackages = {"ru.yandex.practicum.event.fallback"})
@SpringBootApplication
@Import(StatsClientConfig.class)
public class EventsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventsServiceApplication.class, args);
	}

}
