package com.shdata.oip.modular.handler;

import cn.hutool.core.collection.CollUtil;
import com.shdata.oip.core.common.OipConstants;
import com.shdata.oip.core.manage.ServiceInstanceManager;
import com.shdata.oip.core.spi.VirtualService;
import com.shdata.oip.core.spi.VirtualServiceRegistry;
import com.shdata.oip.modular.service.IServiceConfigService;
import com.shdata.oip.modular.service.IVirtualServiceRegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wangwj
 * @version 1.0
 * @date 2022/1/6
 * 检查注册中心是否注册下线，失败，则继续注册
 */
@Slf4j
public class VirtualServiceRegistryHandler {

    @Autowired
    private VirtualServiceRegistry virtualServiceRegistry;
    @Autowired
    private IServiceConfigService iServiceConfigService;
    @Autowired
    private IVirtualServiceRegistryService iVirtualServiceRegistryService;
    @Autowired
    private ServiceInstanceManager serviceInstanceManager;

    private ScheduledExecutorService refreshServerListExecutor;
    private final long refreshServerListInternal = TimeUnit.SECONDS.toMillis(30);
    private long lastServerListRefreshTime = 0L;

    @PostConstruct
    public void init() {
        refreshServerListExecutor = new ScheduledThreadPoolExecutor(1);
        refreshServerListExecutor.scheduleWithFixedDelay(() -> refreshServerListIfNeed(), 200, refreshServerListInternal, TimeUnit.MILLISECONDS);
    }


    private void refreshServerListIfNeed() {
        try {
            log.info("Start Registry Server Center List");
            if (System.currentTimeMillis() - lastServerListRefreshTime < refreshServerListInternal) {
                return;
            }
            initVirtual();
            lastServerListRefreshTime = System.currentTimeMillis();
        } catch (Throwable e) {
            log.warn("failed to update server list", e);
        }
    }


    public void initVirtual() {
        doRegistry(iServiceConfigService.analysisReadDb());
    }

    private void doRegistry(List<VirtualService> virtualServices) {
        if (CollUtil.isEmpty(virtualServices)) {
            return;
        }

        for (int i = 0; i < virtualServices.size(); i++) {
            VirtualService virtualService = virtualServices.get(i);
            //is already ,do not repeat register
            if (serviceInstanceManager.allService().contains(virtualService.getService())) {
                log.debug("注册中心，已注册该【{}】虚服务,执行跳过！", virtualService.getService());
                continue;
            }

            try {
                virtualServiceRegistry.register(virtualService);
                iVirtualServiceRegistryService.virtualServiceUp(virtualService);
                log.info("registry center 【{}】vs service success！", virtualService.getService());
            } catch (Exception e) {
                log.error("{},注册失败", virtualService.getService(), e);
                iVirtualServiceRegistryService.virtualServiceStatus(virtualService.getService(), OipConstants.REGISTRY_STATUS_ERROR);
            }
        }
    }
}
