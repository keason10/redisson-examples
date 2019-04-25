package com.hw.redis.keason10resubmit.aspect;

import com.alibaba.fastjson.JSON;
import com.hw.redis.keason10resubmit.annocation.PreventingDuplicateSubmissions;
import com.hw.redis.keason10resubmit.util.MD5Utils;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class DuplicateSubmissionsAspect {
    private static final Logger logger = LoggerFactory.getLogger(DuplicateSubmissionsAspect.class);
    private static final String expression = "execution(* org.redisson.example.locks.controller.*.*(..))";

    @Autowired
    private RedissonClient redisBean;

    @Around(value = "pointCut()")
    public Object doDuplicateSubmissions(ProceedingJoinPoint proceedingJoinPoint) throws Exception {
        //判断方法是否有注解
        Signature signature = proceedingJoinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        Annotation annotation = targetMethod.getAnnotation(PreventingDuplicateSubmissions.class);
        if (annotation == null) {
            return null;
        }
        String submitKey = ((PreventingDuplicateSubmissions) annotation).lockedKeyPrefix();
        int milliseconds = ((PreventingDuplicateSubmissions) annotation).lockedMilliseconds();
        Object[] objects = proceedingJoinPoint.getArgs();
        List<Object> objectList = new ArrayList<>();
        if (objects == null || objects.length <= 0) {
            return null;
        }
        for (Object object : objects) {
            if (object instanceof HttpServletRequest || object instanceof HttpServletResponse) {
                continue;
            }
            objectList.add(object);
        }
        if (CollectionUtils.isEmpty(objectList)) {
            logger.warn("DuplicateSubmissionsAspect.doDuplicateSubmissions params null ,return");
            return null;
        }
        submitKey = submitKey.concat(MD5Utils.getMd5Val(JSON.toJSONString(objectList)));
        RLock rLock = redisBean.getLock(submitKey);
        if (rLock.isLocked()) {
            logger.warn("lockedKeyPrefix {} duplicate submissions within {} milliseconds", submitKey, milliseconds);
            return String.format("lockedKeyPrefix %s duplicate submissions within %s milliseconds", submitKey, milliseconds);
        }
        rLock.lock(milliseconds, TimeUnit.MILLISECONDS);
        try {
            return proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            logger.error("DuplicateSubmissionsAspect.doDuplicateSubmissions process error", throwable);
            return "DuplicateSubmissionsAspect.doDuplicateSubmissions process error";
        }
    }

    @Pointcut("@annotation(com.hw.redis.keason10resubmit.annocation.PreventingDuplicateSubmissions)")
    public void pointCut() {
    }

}
