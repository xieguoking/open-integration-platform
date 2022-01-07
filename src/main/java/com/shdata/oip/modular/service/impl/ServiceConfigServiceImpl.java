package com.shdata.oip.modular.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shdata.oip.core.common.OipConstants;
import com.shdata.oip.core.spi.VirtualService;
import com.shdata.oip.core.vs.DubboVirtualService;
import com.shdata.oip.modular.dao.ServiceConfigMapper;
import com.shdata.oip.modular.model.po.ServiceConfig;
import com.shdata.oip.modular.model.po.ServiceConfigMetadata;
import com.shdata.oip.modular.model.po.ServiceConfigPlugins;
import com.shdata.oip.modular.service.IServiceConfigMetadataService;
import com.shdata.oip.modular.service.IServiceConfigPluginsService;
import com.shdata.oip.modular.service.IServiceConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wangwj
 * @since 2022-01-05
 */
@Service
@RequiredArgsConstructor
public class ServiceConfigServiceImpl extends ServiceImpl<ServiceConfigMapper, ServiceConfig> implements IServiceConfigService {


    private final IServiceConfigPluginsService iServiceConfigPluginsService;
    private final IServiceConfigMetadataService iServiceConfigMetadataService;


    @Transactional
    @Override
    public void analysisIntoDb(VirtualService virtualService) {
        ServiceConfig isExist = this.getOne(new QueryWrapper<ServiceConfig>().lambda().eq(ServiceConfig::getServiceID, virtualService.getService()));
        if (Objects.nonNull(isExist)) {
            this.removeById(isExist);
            iServiceConfigPluginsService.remove(new QueryWrapper<ServiceConfigPlugins>().lambda().eq(ServiceConfigPlugins::getSid, isExist.getId()));
            iServiceConfigMetadataService.remove(new QueryWrapper<ServiceConfigMetadata>().lambda().eq(ServiceConfigMetadata::getSid, isExist.getId()));
        }

        //服务整合配置表
        ServiceConfig serviceConfig = transFormServiceConfig(virtualService);
        this.save(serviceConfig);
        //服务插件信息表
        List<ServiceConfigPlugins> serviceConfigPlugins = transFormServiceConfigPlugins(serviceConfig.getId(), virtualService);
        iServiceConfigPluginsService.saveBatch(serviceConfigPlugins);
        //服务元数据信息表
        List<ServiceConfigMetadata> serviceConfigMetadata = transFormServiceConfigMetadata(serviceConfig.getId(), virtualService);
        iServiceConfigMetadataService.saveBatch(serviceConfigMetadata);
    }

    @Override
    public List<VirtualService> analysisReadDb() {
        List<ServiceConfig> serviceConfigs = this.list();
        List<VirtualService> virtualServices = serviceConfigs
                .stream()
                .map(this::buildVirtualService)
                .collect(Collectors.toList());
        return virtualServices;
    }

    private VirtualService buildVirtualService(ServiceConfig serviceConfig) {
        if (serviceConfig == null) {
            return null;
        }

        Map<String, String> metaData = JSONUtil.toBean(serviceConfig.getMetadata(), Map.class);
        DubboVirtualService virtualService = new DubboVirtualService();
        virtualService.setService(serviceConfig.getServiceID());
        virtualService.setPackagePrefix(metaData.getOrDefault(OipConstants.KEY_PACKAGE_PREFIX, ""));
        virtualService.setServiceName(serviceConfig.getServiceName());
        virtualService.setIp(serviceConfig.getVip());
        virtualService.setPort(serviceConfig.getVPort());
        virtualService.setServiceType(serviceConfig.getServiceType());
        virtualService.setTransformStrategy(metaData.getOrDefault(OipConstants.KEY_SERVICE_STRATEGY, ""));
        virtualService.setServiceDesc(metaData.getOrDefault(OipConstants.KEY_SERVICE_DESC, ""));
        return virtualService;
    }

    private ServiceConfig transFormServiceConfig(VirtualService virtualService) {
        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setServiceType(virtualService.getServiceType());
        serviceConfig.setServiceID(virtualService.getService());
        serviceConfig.setServiceName(virtualService.getServiceName());
        serviceConfig.setCreateBy("init");
        serviceConfig.setCreateTime(LocalDateTime.now());
        serviceConfig.setUpdateBy("init");
        serviceConfig.setUpdateTime(LocalDateTime.now());
        serviceConfig.setVip(virtualService.getIp());
        serviceConfig.setVPort(virtualService.getPort());
        serviceConfig.setMetadata(JSONUtil.toJsonStr(virtualService.getMetadata()));
        return serviceConfig;
    }


    private List<ServiceConfigPlugins> transFormServiceConfigPlugins(Long sid, VirtualService virtualService) {
        List<ServiceConfigPlugins> list = new ArrayList<>();
        list.add(transFormPluginType(sid, virtualService.getServiceType()));
        list.add(transFormPluginType(sid, virtualService.getMetadata().get(OipConstants.KEY_SERVICE_STRATEGY)));
        list.remove(null);
        return list;
    }

    private ServiceConfigPlugins transFormPluginType(Long sid, String pluginType) {
        if (Objects.isNull(sid) || StrUtil.isBlank(pluginType)) {
            return null;
        }

        ServiceConfigPlugins serviceConfigPlugins = new ServiceConfigPlugins();
        serviceConfigPlugins.setPluginName(pluginType);
        serviceConfigPlugins.setPluginPriority(1);
        serviceConfigPlugins.setPluginType(pluginType);
        serviceConfigPlugins.setSid(sid);
        serviceConfigPlugins.setCreateBy("init");
        serviceConfigPlugins.setCreateTime(LocalDateTime.now());
        serviceConfigPlugins.setUpdateBy("init");
        serviceConfigPlugins.setUpdateTime(LocalDateTime.now());
        return serviceConfigPlugins;
    }


    private List<ServiceConfigMetadata> transFormServiceConfigMetadata(Long sid, VirtualService virtualService) {
        List<ServiceConfigMetadata> list = virtualService.getMetadata().keySet().stream().map(k -> {
            ServiceConfigMetadata serviceConfigMetadata = new ServiceConfigMetadata();
            serviceConfigMetadata.setSid(sid);
            serviceConfigMetadata.setCreateBy("init");
            serviceConfigMetadata.setCreateTime(LocalDateTime.now());
            serviceConfigMetadata.setUpdateBy("init");
            serviceConfigMetadata.setUpdateTime(LocalDateTime.now());
            serviceConfigMetadata.setMetaCode(k);
            serviceConfigMetadata.setMetaName(k);
            serviceConfigMetadata.setMetaValue(virtualService.getMetadata().get(k));
            return serviceConfigMetadata;
        }).collect(Collectors.toList());
        return list;
    }
}
