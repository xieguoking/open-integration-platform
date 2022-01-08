//package com.shdata.oip.modular.handler;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.json.JSONUtil;
//import com.alibaba.nacos.client.naming.utils.CollectionUtils;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.shdata.oip.core.common.OipConstants;
//import com.shdata.oip.core.web.plugin.dubbo.meta.DubboRegistryServerSync;
//import com.shdata.oip.core.dubbo.po.MetaData;
//import com.shdata.oip.core.dubbo.po.Methods;
//import com.shdata.oip.modular.model.po.ApiArgs;
//import com.shdata.oip.modular.model.po.Apis;
//import com.shdata.oip.modular.model.po.ServiceConfig;
//import com.shdata.oip.modular.model.po.VirtualServiceRegistry;
//import com.shdata.oip.modular.service.IApiArgsService;
//import com.shdata.oip.modular.service.IApisService;
//import com.shdata.oip.modular.service.IServiceConfigService;
//import com.shdata.oip.modular.service.IVirtualServiceRegistryService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.common.constants.CommonConstants;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//import org.springframework.context.ApplicationListener;
//
//import java.util.*;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
///**
// * @author wangwj
// * @version 1.0
// * @date 2022/1/6
// * 虚拟服务列表 manage
// */
//@Slf4j
//public class VirtualServiceListHandler implements ApplicationListener<ApplicationReadyEvent> {
//
//    @Autowired
//    private DiscoveryClient discoveryClient;
//    @Autowired
//    private IServiceConfigService iServiceConfigService;
//    @Autowired
//    private IVirtualServiceRegistryService iVirtualServiceRegistryService;
//    @Autowired
//    private IApisService iApisService;
//    @Autowired
//    private IApiArgsService iApisArgsService;
//    @Autowired
//    private DubboRegistryServerSync dubboRegistryServerSync;
//
//
//    private List<String> serversFromEndpoint = new ArrayList<>();
//
//    private ScheduledExecutorService refreshServerListExecutor;
//
//    private final long refreshServerListInternal = TimeUnit.SECONDS.toMillis(30);
//
//    private long lastServerListRefreshTime = 0L;
//
//    @Override
//    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
//        init();
//    }
//
//    public void init() {
//        refreshServerListExecutor = new ScheduledThreadPoolExecutor(1);
//        refreshServerListExecutor.scheduleWithFixedDelay(new Runnable() {
//            @Override
//            public void run() {
//                refreshServerListIfNeed();
//            }
//        }, refreshServerListInternal, refreshServerListInternal, TimeUnit.MILLISECONDS);
//    }
//
//    private void refreshServerListIfNeed() {
//        try {
//            log.info("start refresh Server List");
//            if (System.currentTimeMillis() - lastServerListRefreshTime < refreshServerListInternal) {
//                return;
//            }
//            serversFromEndpoint = getServerListFromEndpoint();
//            if (CollectionUtils.isEmpty(serversFromEndpoint)) {
//                throw new Exception("Can not acquire Nacos list");
//            }
//
//            lastServerListRefreshTime = System.currentTimeMillis();
//        } catch (Throwable e) {
//            log.warn("failed to update server list", e);
//        }
//    }
//
//    private List<String> getServerListFromEndpoint() throws Exception {
//        List<ServiceConfig> serviceConfigs = iServiceConfigService.list();
//        if (CollUtil.isEmpty(serviceConfigs)) {
//            throw new Exception("service config list is null");
//        }
//
//        List<String> serviceIdS = serviceConfigs.stream().map(x -> x.getServiceID()).collect(Collectors.toList());
//        for (int i = 0; i < serviceIdS.size(); i++) {
//            List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances(serviceIdS.get(i));
//
//            if (CollUtil.isEmpty(serviceInstanceList)) {
//                log.warn("{}  instances is empty", serviceIdS.get(i));
//                continue;
//            }
//            //落库虚拟服务
//            List<VirtualServiceRegistry> virtualServiceRegistryList = serviceInstanceList.stream()
//                    .map(this::buildVirtualServiceRegistry).collect(Collectors.toList());
//
//            buildVsUpOrDown(virtualServiceRegistryList);
//            buildVsApiList(virtualServiceRegistryList);
//        }
//        return serviceIdS;
//    }
//
//    private void buildVsUpOrDown(List<VirtualServiceRegistry> virtualServiceRegistryList) {
//
//        Map<String, VirtualServiceRegistry> virtualServiceRegistryMap = virtualServiceRegistryList.stream()
//                .collect(Collectors.toMap(k -> String.format("%s:%s:%s", k.getServiceID(), k.getIp(), k.getPort()), VirtualServiceRegistry -> VirtualServiceRegistry));
//
//        List<VirtualServiceRegistry> dbVsList = iVirtualServiceRegistryService
//                .list(new QueryWrapper<VirtualServiceRegistry>()
//                        .lambda().in(VirtualServiceRegistry::getServiceID, virtualServiceRegistryList.stream().map(x -> x.getServiceID()).collect(Collectors.toList())));
//
//        if (CollUtil.isEmpty(dbVsList)) {
//            iVirtualServiceRegistryService.saveBatch(virtualServiceRegistryList);
//        } else {
//            dbVsList.stream().forEach(x -> {
//                String key = String.format("%s:%s:%s", x.getServiceID(), x.getIp(), x.getPort());
//                if (Objects.nonNull(virtualServiceRegistryMap.get(key))) {
//                    //数据库虚拟服务数据 在 线上能获取得到 说明没有下线
//                    x.setLastTime(DateUtil.date(lastServerListRefreshTime));
//                } else {
//                    //取不到说明下线了
//                    x.setLastTime(DateUtil.date(lastServerListRefreshTime));
//                    x.setStatus("0"); //先不设置通用常量
//                }
//                virtualServiceRegistryMap.remove(key); //多出来的就是新注册的
//            });
//            iVirtualServiceRegistryService.updateBatchById(dbVsList);
//            if (CollUtil.isNotEmpty(virtualServiceRegistryMap.values())) {
//                iVirtualServiceRegistryService.saveBatch(virtualServiceRegistryMap.values());
//            }
//        }
//    }
//
//    private void buildVsApiList(List<VirtualServiceRegistry> virtualServiceRegistryList) {
//        Map<String, VirtualServiceRegistry> virtualServiceRegistryMap = virtualServiceRegistryList.stream()
//                .collect(Collectors.toMap(VirtualServiceRegistry::getServiceID, VirtualServiceRegistry -> VirtualServiceRegistry));
//
//        List<String> services = discoveryClient.getServices();
//        for (String key : virtualServiceRegistryMap.keySet()) {
//            VirtualServiceRegistry var2 = virtualServiceRegistryMap.get(key);
//            Map<String, String> metaData = JSONUtil.toBean(var2.getMetadata(), Map.class);
//            if (StrUtil.isBlank(metaData.get(OipConstants.KEY_PACKAGE_PREFIX))) {
//                continue;
//            }
//            List<String> dubboProviderServices = services.stream()
//                    .filter(k -> k.startsWith(CommonConstants.PROVIDER))
//                    .filter(k -> k.contains(metaData.get(OipConstants.KEY_PACKAGE_PREFIX))).collect(Collectors.toList());
//
//            dubboProviderServices.stream().forEach(x -> buildVsApiMeta(x, var2));
//        }
//    }
//
//    private void buildVsApiMeta(String provider, VirtualServiceRegistry virtualServiceRegistry) {
//        String interfaceName = StrUtil.replace(provider, "providers:", "");
//        MetaData metaCache = dubboRegistryServerSync.getRegistryMetaCache(interfaceName);
//        List<Apis> intoDbApis = new ArrayList<>();
//        List<ApiArgs> intoDbApiArgs = new ArrayList<>();
//        metaCache.getMethods().keySet().stream().forEach(k -> {
//            Methods methods = metaCache.getMethods().get(k);
//
//            Apis apis = new Apis();
//            apis.setApiName(String.format("%s.%s", metaCache.getInterfaceName(), k));
//            apis.setServiceID(virtualServiceRegistry.getServiceID());
//            apis.setSid(1L);
//            apis.setApiStatus("1");
//            intoDbApis.add(apis);
//
//            for (int i = 0; i < methods.getParameterTypes().size(); i++) {
//                ApiArgs apiArgs = new ApiArgs();
//                apiArgs.setArgOrder(i);
//                apiArgs.setArgName(methods.getParameterTypes().get(i));
//                apiArgs.setArgType(methods.getParameterTypes().get(i));
//                apiArgs.setSid(1L);
//                apiArgs.setServiceID(virtualServiceRegistry.getServiceID());
//                intoDbApiArgs.add(apiArgs);
//            }
//        });
//        iApisService.saveBatch(intoDbApis);
//        iApisArgsService.saveBatch(intoDbApiArgs);
//    }
//
//
//    private VirtualServiceRegistry buildVirtualServiceRegistry(ServiceInstance serviceInstance) {
//        VirtualServiceRegistry virtualServiceRegistry = new VirtualServiceRegistry();
//        virtualServiceRegistry.setServiceID(serviceInstance.getServiceId());
//        virtualServiceRegistry.setServiceName(serviceInstance.getServiceId());
//        virtualServiceRegistry.setServiceType(serviceInstance.getMetadata().get(OipConstants.KEY_SERVICE_TYPE));
//        virtualServiceRegistry.setIp(serviceInstance.getHost());
//        virtualServiceRegistry.setPort(serviceInstance.getPort());
//        virtualServiceRegistry.setMetadata(JSONUtil.toJsonStr(serviceInstance.getMetadata()));
//        virtualServiceRegistry.setRegTime(DateUtil.parseDateTime(serviceInstance.getMetadata().get(OipConstants.KEY_SERVICE_REG_TIME)));
//        virtualServiceRegistry.setLastTime(lastServerListRefreshTime == 0 ? new Date() : DateUtil.date(lastServerListRefreshTime));
//        return virtualServiceRegistry;
//    }
//
//}
