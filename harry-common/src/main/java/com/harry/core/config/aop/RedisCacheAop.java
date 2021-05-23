package com.harry.core.config.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import sun.misc.Cache;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class RedisCacheAop {
    @Autowired
    private RedisTemplate redisTemplate;

    @Pointcut("@annotation(com.harry.core.config.aop.CacheRedisAfter)")
    public void cacheRedis() {

    }

    @AfterReturning(value = "cacheRedis()",returning = "returnValues")
    public Object interceptDtTransactionalMethod(ProceedingJoinPoint joinPoint, Object returnValue) throws Throwable {
        Signature signature = joinPoint.getSignature();//此处joinPoint的实现类是MethodInvocationProceedingJoinPoint
        MethodSignature methodSignature = (MethodSignature) signature;//获取参数名
//        Class returnType = methodSignature.getReturnType();


        CacheRedisAfter annotation = methodSignature.getMethod().getAnnotation(CacheRedisAfter.class);
        String key = annotation.key();
        long dxpiration = annotation.dxpiration();
        ValueOperations valueOperations = redisTemplate.opsForValue();

        if(redisTemplate.hasKey(key)){
            Object value = valueOperations.get(key);
            returnValue = value;
        }else{
            valueOperations.set(key,returnValue);
        };
        redisTemplate.expire(key,dxpiration, TimeUnit.SECONDS);
        return returnValue;
    }

}