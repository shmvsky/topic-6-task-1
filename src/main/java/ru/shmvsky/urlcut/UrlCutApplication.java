package ru.shmvsky.urlcut;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UrlCutApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlCutApplication.class, args);
    }

}
