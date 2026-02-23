package ru.practicum.ewm.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "ru.practicum.ewm.main",
        "ru.practicum.ewm.stats"
})
public class EwmMainApp {
    public static void main(String[] args) {
        SpringApplication.run(EwmMainApp.class, args);
    }
}
