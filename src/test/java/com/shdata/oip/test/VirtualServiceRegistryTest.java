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
        serviceConfigDTO.setPackagePrefix("com.dave"); //必填，写了逻辑会读取这个开头的数据
        serviceConfigDTO.setServiceType("dubbo");
        serviceConfigDTO.setServiceName("万达金保二期");
        serviceConfigDTO.setServiceId("dubbo-service");
        serviceConfigDTO.setServiceDesc("万达金保二期");
        serviceConfigDTO.setTransformStrategy("default");
        serviceConfigDTO.setOspIp("127.0.0.1");
        serviceConfigDTO.setOspPort(9001);
        registryController.serviceConfig(serviceConfigDTO);
    }
}
