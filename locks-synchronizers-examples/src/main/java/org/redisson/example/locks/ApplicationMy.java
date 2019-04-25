package org.redisson.example.locks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(value = {"org.redisson.example.locks.controller","com.hw.redis.keason10resubmit"})
public class ApplicationMy{
    public static void main(String[] args) {
        SpringApplication ctx = new SpringApplication(ApplicationMy.class);
        ctx.run(args);
    }
}
