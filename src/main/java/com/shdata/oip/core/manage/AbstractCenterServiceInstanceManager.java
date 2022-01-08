package com.shdata.oip.core.manage;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.shdata.oip.core.manage.storage.LocalMapServiceInstanceStorage;
import com.shdata.oip.core.manage.storage.ServiceInstanceStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wangwj
 * @version 1.0
 * @date 2022/1/7
 */
@Slf4j
public abstract class AbstractCenterServiceInstanceManager implements ServiceInstanceManager {

    protected ServiceInstanceStorage serviceInstanceStorage;

    private ScheduledExecutorService refreshServerListExecutor;

    private final long refreshServerListInternal = TimeUnit.SECONDS.toMillis(30);

    private long lastServerListRefreshTime = 0L;

    public AbstractCenterServiceInstanceManager() {
        serviceInstanceStorage = new LocalMapServiceInstanceStorage();
        refreshServerListExecutor = new ScheduledThreadPoolExecutor(1);
        refreshServerListExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                refreshServerListIfNeed();
            }
        }, 0L, refreshServerListInternal, TimeUnit.MILLISECONDS);
    }


    private void refreshServerListIfNeed() {
        try {
            log.debug("start refresh Server List");
            if (System.currentTimeMillis() - lastServerListRefreshTime < refreshServerListInternal) {
                return;
            }
            List<String> serviceList = getServerListFromEndpoint();
            if (CollectionUtils.isEmpty(serviceList)) {
                throw new Exception("Can not acquire Nacos list");
            }

            serviceInstanceStorage.putService(serviceList);
            lastServerListRefreshTime = System.currentTimeMillis();
        } catch (Throwable e) {
            log.warn("failed to update server list", e);
        }
    }

    public abstract List<String> getServerListFromEndpoint();

    public abstract List<ServiceInstance> getMetaDataFromEndpoint(String serviceId);


    @Override
    public List<String> allService() {
        return this.serviceInstanceStorage.allService();
    }


    @Override
    public List<ServiceInstance> listServiceInstance(String serviceId) {
        List<ServiceInstance> serviceInstanceList = this.serviceInstanceStorage.listServiceInstance(serviceId);
        if (CollUtil.isNotEmpty(serviceInstanceList)) {
            return serviceInstanceList;
        }
        List<ServiceInstance> metaDataFromEndpoint = getMetaDataFromEndpoint(serviceId);
        this.serviceInstanceStorage.putServiceInstance(serviceId, metaDataFromEndpoint);
        return metaDataFromEndpoint;
    }

    @Override
    public ServiceInstance getServiceInstanceOne(String serviceId) {
        List<ServiceInstance> list = listServiceInstance(serviceId);
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        return CollUtil.getFirst(list.iterator());
    }

}
