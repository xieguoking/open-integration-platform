package com.shdata.oip.core.manage;

import cn.hutool.core.collection.CollUtil;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangwj
 * @version 1.0
 * @date 2022/1/7
 */
public class NacosServiceInstanceManager extends AbstractCenterServiceInstanceManager {

    private DiscoveryClient discoveryClient;

    public NacosServiceInstanceManager(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
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
        return serviceInstanceList;
    }
}
