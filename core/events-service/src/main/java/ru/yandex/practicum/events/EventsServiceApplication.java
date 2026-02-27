package ru.yandex.practicum.events;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = {"ru.yandex.practicum.contracts"})
@SpringBootApplication
public class EventsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventsServiceApplication.class, args);
	}

}
