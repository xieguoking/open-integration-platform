package com.shdata.oip.modular.handler;

import cn.hutool.core.collection.CollUtil;
import com.shdata.oip.core.spi.VirtualService;
import com.shdata.oip.core.spi.VirtualServiceRegistry;
import com.shdata.oip.modular.service.IServiceConfigService;
import com.shdata.oip.modular.service.IVirtualServiceRegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wangwj
 * @version 1.0
 * @date 2022/1/6
 * TODO 检查注册失败 继续注册
 */
@Slf4j
public class VirtualServiceRegistryHandler implements ApplicationListener<ApplicationReadyEvent>, DisposableBean {

    @Autowired
    private VirtualServiceRegistry virtualServiceRegistry;
    @Autowired
    private IServiceConfigService iServiceConfigService;
    @Autowired
    private IVirtualServiceRegistryService iVirtualServiceRegistryService;
    private ScheduledExecutorService refreshServerListExecutor;
    private final long refreshServerListInternal = TimeUnit.SECONDS.toMillis(60);
    private long lastServerListRefreshTime = 0L;

    public void init() {
        refreshServerListExecutor = new ScheduledThreadPoolExecutor(1);
        refreshServerListExecutor.scheduleWithFixedDelay(() -> refreshServerListIfNeed(), 0, refreshServerListInternal, TimeUnit.MILLISECONDS);
    }


    private void refreshServerListIfNeed() {
        try {
            log.info("Start Registry Server Center List");
            if (System.currentTimeMillis() - lastServerListRefreshTime < refreshServerListInternal) {
                return;
            }
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
            try {
                virtualServiceRegistry.register(virtualServices.get(i));
                iVirtualServiceRegistryService.virtualServiceUp(virtualServices.get(i));
            } catch (Exception e) {
                log.error("{},注册失败", virtualServices.get(i).getService(), e);
            }
        }
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        initVirtual();
    }

    @Override
    public void destroy() throws Exception {
        //下线
    }
}
