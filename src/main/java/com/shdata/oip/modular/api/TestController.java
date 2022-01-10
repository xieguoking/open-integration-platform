package com.shdata.oip.modular.api;

import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangwj
 * @version 1.0
 * @date 2022/1/10
 */
@RestController
@RequestMapping("/test/")
public class TestController {

    @RequestMapping("/testInit/")
    public void initServiceTest() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String interfaceName = "com.dave.dubbo.provider.api.MessageService_2_7_8";
        System.out.println("====================================");
        ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
        reference.setInterface(interfaceName);
        reference.setVersion("1.0.0");
        reference.setGeneric(true);
        System.out.println(reference.get().toString());
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
        System.out.println("====================================");


    }
}
