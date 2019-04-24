package org.redisson.example.locks;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@RequestMapping("/test")
@RestController
@SpringBootApplication
public class ApplicationMy implements DisposableBean {

    public static void main(String[] args) {
        SpringApplication ctx = new SpringApplication(ApplicationMy.class);
        ctx.run(args);
    }

    @Autowired
    private RedissonClient redisson;

    @GetMapping("/redis")
    public String getTest() {
        RLock lock = redisson.getLock("lock");
        boolean locktemp = lock.isLocked();
        if (locktemp) {
            System.out.println("redis get lock fail " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss:SSS")));
            return "redis get lock fail " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss:SSS"));
        }
        lock.lock(5, TimeUnit.SECONDS);
        System.out.println("redis get lock success " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss:SSS")));
        return "redis get lock success " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss:SSS"));
    }

    @Bean
    public RedissonClient redisBean() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setPassword(null);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

    @Override
    public void destroy() throws Exception {
        if (redisBean() != null) {
            redisson.shutdown();
        }
    }
}
