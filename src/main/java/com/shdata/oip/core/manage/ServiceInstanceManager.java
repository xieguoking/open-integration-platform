package com.shdata.oip.core.manage;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

public interface ServiceInstanceManager {


    /**
     * 获取所有服务列表
     *
     * @return
     */
    List<String> allService();

    /***
     * --------| Instance1
     * -------------------| ip:xxx
     * -------------------| port:xxx
     * --------| Instance2
     * -------------------| ip:xxx
     * -------------------| port:xxx
     * 包含服务本身的信息
     */
    List<ServiceInstance> listServiceInstance(String serviceId);

    /**
     * 获取服务实例中的第一个
     */
    ServiceInstance getServiceInstanceOne(String serviceId);

}
