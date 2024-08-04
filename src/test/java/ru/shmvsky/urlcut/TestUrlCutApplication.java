package ru.shmvsky.urlcut;

import org.springframework.boot.SpringApplication;

public class TestUrlCutApplication {

    public static void main(String[] args) {
        SpringApplication.from(UrlCutApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
