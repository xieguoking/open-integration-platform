package com.shdata.oip.web.plugin.dubbo.meta;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shdata.oip.web.plugin.dubbo.cache.DubboConfigCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.config.Environment;
import org.apache.dubbo.common.config.configcenter.DynamicConfiguration;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.metadata.report.identifier.KeyTypeEnum;
import org.apache.dubbo.metadata.report.identifier.MetadataIdentifier;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.event.EventListener;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/30
 */

@Slf4j
public class DubboRegistryServerSync {

    private final DiscoveryClient discoveryClient;

    public DubboRegistryServerSync(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    private final ConcurrentMap<String, MetaData> registryMetaCache = new ConcurrentHashMap<>();

    public ConcurrentMap<String, MetaData> getRegistryMetaCache() {
        return registryMetaCache;
    }

    /**
     * 这个后面要换成监听
     */
    public MetaData getProvider(String interfaceMergerKey) {
        initAllService();

        return registryMetaCache.get(interfaceMergerKey);
    }


    @EventListener(classes = ApplicationReadyEvent.class)
    public void startInitCache() {
        log.info("Init Dubbo meta data Sync Cache...");
        this.initAllService();
        this.initDubboConfigCache();
    }

    private void initDubboConfigCache() {
        this.registryMetaCache.values().stream().forEach(v -> DubboConfigCache.getInstance().initRef(v));
    }


    private void initAllService() {
        //获取所有服务 dubbo的提供者服务是以providers打头
        List<String> services = discoveryClient.getServices();
        if (CollUtil.isEmpty(services)) {
            return;
        }

        List<String> dubboProviderServices = services.stream().filter(x -> x.startsWith(CommonConstants.PROVIDER)).collect(Collectors.toList());

        Environment environment = ApplicationModel.getEnvironment();
        DynamicConfiguration dynamicConfiguration = environment.getDynamicConfiguration().get();

        for (String k : dubboProviderServices) {
            MetaData metaData = new MetaData();
            ServiceInstance serviceInstance = discoveryClient.getInstances(k).stream().findFirst().get();
            metaData.setInterfaceName(serviceInstance.getMetadata().getOrDefault(CommonConstants.INTERFACE_KEY, ""));
            metaData.setVersion(serviceInstance.getMetadata().getOrDefault(CommonConstants.VERSION_KEY, ""));
            metaData.setApplication(serviceInstance.getMetadata().getOrDefault(CommonConstants.APPLICATION_KEY, ""));
            metaData.setGroup(serviceInstance.getMetadata().getOrDefault(CommonConstants.GROUP_KEY, ""));
            //获取元数据
            MetadataIdentifier metadataIdentifier = new MetadataIdentifier(
                    metaData.getInterfaceName(),
                    metaData.getVersion(),
                    metaData.getGroup(),
                    CommonConstants.PROVIDER_SIDE,
                    metaData.getApplication());

            String config = dynamicConfiguration.getConfig(metadataIdentifier.getUniqueKey(KeyTypeEnum.UNIQUE_KEY), CommonConstants.DUBBO);
            log.debug("[{}]获取配置如下:\r\n" + config, metadataIdentifier.getUniqueKey(KeyTypeEnum.UNIQUE_KEY));
            Assert.notBlank(config, String.format("[%s]配置获取失败,请检查!", metadataIdentifier.getUniqueKey(KeyTypeEnum.UNIQUE_KEY)));

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                MetaConfig dubboSourceConfig = objectMapper.readValue(config, MetaConfig.class);
                metaData.setMethods(dubboSourceConfig.getMethods().stream().collect(Collectors.toMap(Methods::getName, Methods -> Methods)));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            registryMetaCache.put(String.format("%s:%s:%s", metaData.getInterfaceName(), metaData.getVersion(), metaData.getGroup()), metaData);
        }
    }
}
