package com.harry;


import com.acgist.snail.net.application.ApplicationClient;
import com.acgist.snail.pojo.message.ApplicationMessage;
import com.acgist.snail.system.format.BEncodeEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/t")
public class TestController {
//
//    @Autowired
//    private EurekaClient eurekaClient;

//    @Autowired
//    private SubscriptionClient subscriptionClient;

    final ApplicationClient client = ApplicationClient.newInstance();

//    @GetMapping("/t")
    public void t() {
//        boolean t = subscriptionClient.t();
        final Map<String, String> map = new HashMap<>();
        // 单个文件任务
        map.put("url", "C:\\Users\\harry\\Desktop\\02cc1d2f79c9b90087c6cd2e8b7de50b.torrent");
        // BT任务
//				map.put("url", "种子文件路径");
//				map.put("files", "B编码下载文件列表");
        client.send(ApplicationMessage.message(ApplicationMessage.Type.TASK_NEW, BEncodeEncoder.encodeMapString(map)));
    }
}
