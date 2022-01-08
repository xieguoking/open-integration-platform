package com.shdata.oip.core.manage.storage;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * @author wangwj
 * @version 1.0
 * @date 2022/1/7
 */

public interface ServiceInstanceStorage {

    List<String> allService();

    List<ServiceInstance> listServiceInstance(String serviceId);

    void removeServiceInstance(String serviceId);

    void putService(List<String> serviceList);

    void putServiceInstance(String serviceId, List<ServiceInstance> serviceInstances);
}
