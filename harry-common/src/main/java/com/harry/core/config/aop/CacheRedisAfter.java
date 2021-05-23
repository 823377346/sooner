package com.harry.core.config.aop;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheRedisAfter {

    String key() default "";

    long dxpiration () default 30000L;

//    Class returnClazz() default Object.class;
}




