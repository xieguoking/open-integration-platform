package com.shdata.oip.test;

import com.shdata.oip.OpenSupportPlatform;
import com.shdata.oip.modular.api.RegistryController;
import com.shdata.oip.modular.model.dto.ServiceConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

/**
 * @author wangwj
 * @version 1.0
 * @date 2022/1/5
 */


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {OpenSupportPlatform.class})
public class VirtualServiceRegistryTest {


    @Autowired
    private RegistryController registryController;

    @Test
    public void shenYnNacosRegistry() throws InterruptedException {
        ServiceConfigDTO serviceConfigDTO = new ServiceConfigDTO();
        serviceConfigDTO.setPackagePrefix("com.dave");
        serviceConfigDTO.setServiceType("dubbo");
        serviceConfigDTO.setServiceName("万达金保二期");
        serviceConfigDTO.setServiceId("dubbo-service");
        serviceConfigDTO.setServiceDesc("万达金保二期");
        serviceConfigDTO.setTransformStrategy("default");
        serviceConfigDTO.setOspIp("127.0.0.1");
        serviceConfigDTO.setOspPort(9001);

        registryController.serviceConfig(serviceConfigDTO);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        //阻塞主线程，手动关闭，自动关闭心跳也就结束无法直观的看到是否注册成功
        countDownLatch.await();
    }
}
