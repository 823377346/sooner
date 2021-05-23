package com.harry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author: HuYi.Zhang
 * @create: 2018-05-30 11:13
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableAspectJAutoProxy//开启AspectJ注解
public class UploadBoot {
    public static void main(String[] args) {
        SpringApplication.run(UploadBoot.class, args);
    }
}
