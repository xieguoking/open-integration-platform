package com.shdata.oip.core.manage.storage;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author wangwj
 * @version 1.0
 * @date 2022/1/7
 */
@Slf4j
public class LocalMapServiceInstanceStorage implements ServiceInstanceStorage {

    private List<String> serviceList = new ArrayList<>();

    private final LoadingCache<String, List<ServiceInstance>> serviceInfos = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build(new CacheLoader<String, List<ServiceInstance>>() {
                @Override
                @Nonnull
                public List<ServiceInstance> load(@Nonnull final String key) {
                    return new ArrayList<>();
                }
            });


    @Override
    public List<String> allService() {
        return serviceList;
    }


    @Override
    public List<ServiceInstance> listServiceInstance(String serviceId) {
        try {
            return serviceInfos.get(serviceId);
        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    @Override
    public void removeServiceInstance(String serviceId) {
        this.serviceInfos.invalidate(serviceId);
    }

    @Override
    public void putServiceInstance(String serviceId, List<ServiceInstance> serviceInstances) {
        this.serviceInfos.put(serviceId, serviceInstances);
    }

    @Override
    public void putService(List<String> serviceList) {
        this.serviceList = serviceList;
    }
}
