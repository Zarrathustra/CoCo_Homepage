package org.cocoproject.homepage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CocoHomepageApplication {
    public static void main(String[] args) {
        SpringApplication.run(CocoHomepageApplication.class, args);
    }
}
