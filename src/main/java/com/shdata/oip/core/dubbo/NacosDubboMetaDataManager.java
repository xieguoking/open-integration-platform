package com.shdata.oip.core.dubbo;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.shdata.oip.core.dubbo.po.MetaConfig;
import com.shdata.oip.core.dubbo.po.MetaData;
import com.shdata.oip.core.dubbo.po.Methods;
import com.shdata.oip.core.manage.ServiceInstanceManager;
import com.shdata.oip.core.web.plugin.dubbo.cache.DubboConfigCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.config.configcenter.DynamicConfiguration;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.metadata.report.identifier.KeyTypeEnum;
import org.apache.dubbo.metadata.report.identifier.MetadataIdentifier;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.springframework.cloud.client.ServiceInstance;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author wangwj
 * @version 1.0
 * @date 2022/1/7
 */
@Slf4j
public class NacosDubboMetaDataManager implements DubboMetaDataManager {

    /**
     * cache max count.
     */
    private static final int CACHE_MAX_COUNT = 1000;

    private ServiceInstanceManager serviceInstanceManager;

    public NacosDubboMetaDataManager(ServiceInstanceManager serviceInstanceManager) {
        this.serviceInstanceManager = serviceInstanceManager;
    }

    private final LoadingCache<String, MetaData> metaDataInfos = CacheBuilder.newBuilder()
            .maximumSize(CACHE_MAX_COUNT)//controller 1000 , no up
            .build(new CacheLoader<String, MetaData>() {
                @Override
                @Nonnull
                public MetaData load(@Nonnull final String key) {
                    return new MetaData();
                }
            });


    @Override
    public MetaData getMetaData(String key) {
        MetaData metaData = null;
        try {
            metaData = metaDataInfos.get(key);
            if (Objects.isNull(metaData) || StrUtil.isBlank(metaData.getInterfaceName())) {
                //dubbo key is providers:interfaceName:version:group
                String serviceId = String.format("providers:%s", key);
                ServiceInstance serviceInstance = serviceInstanceManager.getServiceInstanceOne(serviceId);

                if (Objects.isNull(serviceInstance)) {
                    log.debug("【{}】，未找到该实例，获取服务实例失败", serviceId);
                    return null;
                }
                return buildMetaData(serviceInstance);
            }
        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        return metaData;
    }

    private MetaData buildMetaData(ServiceInstance serviceInstance) {
        MetaData metaData = new MetaData();
        metaData.setInterfaceName(serviceInstance.getMetadata().getOrDefault(CommonConstants.INTERFACE_KEY, ""));
        metaData.setVersion(serviceInstance.getMetadata().getOrDefault(CommonConstants.VERSION_KEY, ""));
        metaData.setApplication(serviceInstance.getMetadata().getOrDefault(CommonConstants.APPLICATION_KEY, ""));
        metaData.setGroup(serviceInstance.getMetadata().getOrDefault(CommonConstants.GROUP_KEY, ""));

        MetadataIdentifier metadataIdentifier = new MetadataIdentifier(metaData.getInterfaceName(), metaData.getVersion(),
                metaData.getGroup(), CommonConstants.PROVIDER_SIDE, metaData.getApplication());

        //import dubbo service has ,so i use it
        DynamicConfiguration dynamicConfiguration = ApplicationModel.getEnvironment().getDynamicConfiguration().get();
        String config = dynamicConfiguration.getConfig(metadataIdentifier.getUniqueKey(KeyTypeEnum.UNIQUE_KEY), CommonConstants.DUBBO);

        log.debug("[{}]获取配置如下:\r\n" + config, metadataIdentifier.getUniqueKey(KeyTypeEnum.UNIQUE_KEY));
        Assert.notBlank(config, String.format("[%s]配置获取失败,请检查!", metadataIdentifier.getUniqueKey(KeyTypeEnum.UNIQUE_KEY)));

        dynamicConfiguration.addListener(metadataIdentifier.getUniqueKey(KeyTypeEnum.UNIQUE_KEY), event -> {
            //listener key ,has change remove it ,get is over new
            log.info("key config 【{}】 has change,do remove event", metadataIdentifier.getUniqueKey(KeyTypeEnum.UNIQUE_KEY));

            String interfaceMergerKey = cacheKeyTransform(metaData);
            metaDataInfos.invalidate(interfaceMergerKey);
            //dubbo cache remove key ,make it reload cache
            DubboConfigCache.getInstance().invalidate(interfaceMergerKey);
        });


        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MetaConfig dubboSourceConfig = objectMapper.readValue(config, MetaConfig.class);
            metaData.setMethods(dubboSourceConfig.getMethods().stream().collect(Collectors.toMap(Methods::getName, Methods -> Methods)));
        } catch (Exception e) {
            log.info("[{}]解析报错，获取配置如下:\r\n" + config, metadataIdentifier.getUniqueKey(KeyTypeEnum.UNIQUE_KEY));
            log.error(e.getMessage(), e);
        }
        metaDataInfos.put(cacheKeyTransform(metaData), metaData);
        return metaData;
    }

    private String cacheKeyTransform(MetaData metaData) {
        return String.format("%s:%s:%s", metaData.getInterfaceName(), metaData.getVersion(), metaData.getGroup());
    }
}
