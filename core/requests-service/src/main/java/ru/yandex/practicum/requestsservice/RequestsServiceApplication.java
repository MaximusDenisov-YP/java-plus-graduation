package ru.yandex.practicum.requestsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = {"ru.yandex.practicum.requestsservice.fallback"})
@SpringBootApplication
public class RequestsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RequestsServiceApplication.class, args);
	}

}
