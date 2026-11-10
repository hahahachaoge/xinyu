package com.example.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.web.utils.BaseContext;

@ComponentScan(basePackages = { "com.example" })
@SpringBootApplication
@EnableScheduling
public class WebApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext run = SpringApplication.run(WebApplication.class, args);

        BaseContext.setApplicationContext(run);
    }
}
