package com.shdata.oip;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.shdata.oip.vs.NacosVirtualServiceRegistry;
import org.apache.shenyu.register.common.config.ShenyuRegisterCenterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

/**
 * @author xieguojun
 * @author (2021 / 12 / 17 add by xieguojun)
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
@EnableDiscoveryClient
public class OpenSupportPlatform {

    public static void main(String[] args) {
        SpringApplication.run(OpenSupportPlatform.class, args);
    }

    @Bean
    public NacosVirtualServiceRegistry nacosVirtualServiceRegistry() {
        return new NacosVirtualServiceRegistry();
    }

    /**
     * 注册中心的配置
     */
    @Bean
    public ShenyuRegisterCenterConfig shenyuRegisterCenterConfig(NacosDiscoveryProperties nacosDiscoveryProperties) {
        ShenyuRegisterCenterConfig shenyuRegisterCenterConfig = new ShenyuRegisterCenterConfig();
        shenyuRegisterCenterConfig.setRegisterType("nacos");
        shenyuRegisterCenterConfig.setServerLists(nacosDiscoveryProperties.getServerAddr());
        Properties prop = new Properties();
        prop.setProperty("nacosNameSpace", nacosDiscoveryProperties.getNamespace());//必须
        shenyuRegisterCenterConfig.setProps(prop);
        return shenyuRegisterCenterConfig;
    }
}