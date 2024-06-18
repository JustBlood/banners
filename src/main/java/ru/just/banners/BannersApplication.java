package ru.just.banners;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BannersApplication {
    public static void main(String[] args) {
        SpringApplication.run(BannersApplication.class, args);
    }
}
