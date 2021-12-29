package com.shdata.osp;

import com.shdata.osp.shenyu.nacos.ShdataNacosClientRegisterRepository;
import com.shdata.osp.vs.NacosVirtualServiceRegistry;
import org.apache.shenyu.register.client.nacos.NacosClientRegisterRepository;
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
    public ShenyuRegisterCenterConfig shenyuRegisterCenterConfig() {
        ShenyuRegisterCenterConfig shenyuRegisterCenterConfig = new ShenyuRegisterCenterConfig();
        shenyuRegisterCenterConfig.setRegisterType("nacos");
        shenyuRegisterCenterConfig.setServerLists("127.0.0.1:8848");
        Properties prop = new Properties();
        prop.setProperty("nacosNameSpace", ""); //不填这个 初始化会空指针 可以给个空
        shenyuRegisterCenterConfig.setProps(prop);
        return shenyuRegisterCenterConfig;
    }

    /**
     * 初始化Nacos注册中心接入
     */
    @Bean
    public ShdataNacosClientRegisterRepository shdataNacosClientRegisterRepository(ShenyuRegisterCenterConfig shenyuRegisterCenterConfig) {
        ShdataNacosClientRegisterRepository shdataNacosClientRegisterRepository = new ShdataNacosClientRegisterRepository();
        shdataNacosClientRegisterRepository.init(shenyuRegisterCenterConfig);
        return shdataNacosClientRegisterRepository;
    }
}
