package org.redisson.example.locks.controller;

import com.alibaba.fastjson.JSON;
import com.hw.redis.keason10resubmit.annocation.PreventingDuplicateSubmissions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/test")
public class MyRedisTestController {
    @PreventingDuplicateSubmissions(lockedKeyPrefix = "test-redis", lockedMilliseconds = 10000)
    @GetMapping("/redis/{string}")
    public String getTest(@PathVariable("string") String string) {
        System.out.println("string  " + string + "redis get lock success " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss:SSS")));
        return "string  " + string + "redis get lock success " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss:SSS"));
    }


    @PreventingDuplicateSubmissions(lockedKeyPrefix = "test-redis-body", lockedMilliseconds = 1000)
    @PostMapping("/redis/body")
    public String getTest(@RequestBody Map map) {
        log.info("redis get lock success param {}  time {}", JSON.toJSONString(map), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss:SSS")));
        return "redis get lock success " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss:SSS"));
    }
}
