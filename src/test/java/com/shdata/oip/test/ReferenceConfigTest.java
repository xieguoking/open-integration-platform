package com.shdata.oip.test;

import com.shdata.oip.OpenSupportPlatform;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

/**
 * @author wangwj
 * @version 1.0
 * @date 2022/1/10
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {OpenSupportPlatform.class})
public class ReferenceConfigTest {

    @Test
    public void initServiceTest() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String[] interfaceName = {"com.dave.dubbo.provider.api.EmailService_2_7_8", "com.dave.dubbo.provider.api.MessageService_2_7_8"};
        System.out.println("====================================");
        for (int i = 0; i < interfaceName.length; i++) {
//            MetaData metaData = new MetaData();
//            metaData.setApplication("dubbo-nacos-provider-2.7.8");
//            metaData.setGroup("");
//            metaData.setVersion("1.0.0");
//            metaData.setInterfaceName(interfaceName[0]);
//            ReferenceConfig<GenericService> serviceReferenceConfig = DubboConfigCache.getInstance().build(metaData);

            ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
            reference.setInterface(interfaceName[0]);
            reference.setVersion("1.0.0");
            reference.setGeneric(true);
            System.out.println(reference.get().toString());
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
        System.out.println("====================================");


    }
}
