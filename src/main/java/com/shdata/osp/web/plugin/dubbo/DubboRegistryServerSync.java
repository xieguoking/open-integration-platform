package com.shdata.osp.web.plugin.dubbo;

import cn.hutool.core.lang.Assert;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.config.Environment;
import org.apache.dubbo.common.config.configcenter.DynamicConfiguration;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.metadata.report.identifier.KeyTypeEnum;
import org.apache.dubbo.metadata.report.identifier.MetadataIdentifier;
import org.apache.dubbo.registry.NotifyListener;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

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
public class DubboRegistryServerSync implements NotifyListener {

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
    public void getProvider() {
        //获取所有服务 dubbo的提供者服务是以providers打头
        List<String> services = discoveryClient.getServices();
        List<String> dubboProviderServices = services.stream().filter(x -> x.startsWith(CommonConstants.PROVIDER)).collect(Collectors.toList());

        Environment environment = ApplicationModel.getEnvironment();
        DynamicConfiguration dynamicConfiguration = environment.getDynamicConfiguration().get();

        for (String k : dubboProviderServices) {
            MetaData metaData = new MetaData();
            ServiceInstance serviceInstance = discoveryClient.getInstances(k).stream().findFirst().get();
            metaData.setInterfaceName(serviceInstance.getMetadata().getOrDefault("interface", ""));
            metaData.setVersion(serviceInstance.getMetadata().getOrDefault("version", ""));
            metaData.setApplication(serviceInstance.getMetadata().getOrDefault("application", ""));
            //获取元数据
            MetadataIdentifier metadataIdentifier = new MetadataIdentifier(
                    metaData.getInterfaceName(),
                    metaData.getVersion(),
                    metaData.getGroup(),
                    CommonConstants.PROVIDER_SIDE,
                    metaData.getApplication());

            String config = dynamicConfiguration.getConfig(metadataIdentifier.getUniqueKey(KeyTypeEnum.UNIQUE_KEY), CommonConstants.DUBBO);
            log.info("[{}]获取配置如下:\r\n" + config, metadataIdentifier.getUniqueKey(KeyTypeEnum.UNIQUE_KEY));
            Assert.notBlank(config, String.format("[%s]配置获取失败,请检查!", metadataIdentifier.getUniqueKey(KeyTypeEnum.UNIQUE_KEY)));

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                DubboSourceConfig dubboSourceConfig = objectMapper.readValue(config, DubboSourceConfig.class);
                metaData.setMethods(dubboSourceConfig.getMethods().stream().collect(Collectors.toMap(Methods::getName, Methods -> Methods)));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            registryMetaCache.put(metaData.getInterfaceName(), metaData);
        }
    }


    @Override
    public void notify(List<URL> urls) {
        System.out.println(urls);
    }
}
