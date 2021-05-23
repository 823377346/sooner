package com.harry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author: HuYi.Zhang
 * @create: 2018-05-24 10:55
 **/
@SpringBootApplication
@EnableEurekaServer
public class Registry {
    public static void main(String[] args) {
        SpringApplication.run(Registry.class, args);
    }
}
