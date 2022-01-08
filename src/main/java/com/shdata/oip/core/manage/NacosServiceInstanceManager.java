package com.shdata.oip.core.manage;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangwj
 * @version 1.0
 * @date 2022/1/7
 */
@Slf4j
public class NacosServiceInstanceManager extends AbstractCenterServiceInstanceManager {

    private DiscoveryClient discoveryClient;
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    public NacosServiceInstanceManager(DiscoveryClient discoveryClient, NacosDiscoveryProperties nacosDiscoveryProperties) {
        this.discoveryClient = discoveryClient;
        this.nacosDiscoveryProperties = nacosDiscoveryProperties;
    }


    @Override
    public List<String> getServerListFromEndpoint() {
        return discoveryClient.getServices();
    }

    @Override
    public List<ServiceInstance> getMetaDataFromEndpoint(String serviceId) {
        List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances(serviceId);
        if (CollUtil.isEmpty(serviceInstanceList)) {
            return new ArrayList<>();
        }
        try {
            nacosDiscoveryProperties.namingServiceInstance().subscribe(serviceId, event -> {
                //listener serviceId ,has change remove it ,get is over new
                log.info("serviceId 【{}】 has change,do remove event", serviceId);
                super.serviceInstanceStorage.removeServiceInstance(serviceId);
            });
        } catch (NacosException e) {
            log.error(e.getMessage(), e);
        }

        return serviceInstanceList;
    }
}
