package com.harry;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

//@FeignClient(name = "task")
@Component
public interface SubscriptionClient {

    @GetMapping(value = "/t/t")
    boolean t();

}

