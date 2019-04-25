package com.hw.redis.keason10resubmit.annocation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PreventingDuplicateSubmissions {
    //锁定key前缀
    String lockedKeyPrefix();

    //锁定的毫秒数
    int lockedMilliseconds();
}
