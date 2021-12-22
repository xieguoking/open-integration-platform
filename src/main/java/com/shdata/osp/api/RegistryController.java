package com.shdata.osp.api;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.registry.NacosRegistration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author xieguojun
 * @author (2021 / 12 / 22 add by xieguojun)
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/")
public class RegistryController implements ApplicationContextAware {


    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private ServiceRegistry serviceRegistry;

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    private ApplicationContext context;

    @GetMapping("app/{name}")
    public Object registry(@PathVariable String name) {
        nacosDiscoveryProperties.setService(name);
        nacosDiscoveryProperties.getMetadata().put("type", getClass().getName());
        nacosDiscoveryProperties.setIp("127.0.0.1");
        nacosDiscoveryProperties.setPort(8848);
        NacosRegistration registration = new NacosRegistration(nacosDiscoveryProperties, context);
        serviceRegistry.register(registration);

        List<String> services = discoveryClient.getServices();
        return services;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }
}
