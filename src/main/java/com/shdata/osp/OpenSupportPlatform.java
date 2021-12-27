package com.shdata.osp;

import com.shdata.osp.vs.NacosVirtualServiceRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

/**
 * @author xieguojun
 * @author (2021 / 12 / 17 add by xieguojun)
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class OpenSupportPlatform {
    public static void main(String[] args) {
        SpringApplication.run(OpenSupportPlatform.class, args);
    }

    @Bean
    public NacosVirtualServiceRegistry nacosVirtualServiceRegistry() {
        return new NacosVirtualServiceRegistry();
    }
}
