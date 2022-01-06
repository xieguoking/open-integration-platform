package com.shdata.oip.modular.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shdata.oip.core.spi.VirtualService;
import com.shdata.oip.core.vs.DefaultVirtualService;
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
        long count = this.count(new QueryWrapper<ServiceConfig>().lambda().eq(ServiceConfig::getServiceID, virtualService.getService()));
        Assert.isTrue(count == 0, "【{}】，配置已存在", virtualService.getService());

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
        return serviceConfig;
    }


    private List<ServiceConfigPlugins> transFormServiceConfigPlugins(Long sid, VirtualService virtualService) {
        List<ServiceConfigPlugins> list = new ArrayList<>();
        list.add(transFormPluginType(sid, virtualService.getServiceType()));
        list.add(transFormPluginType(sid, virtualService.getMetadata().get(DefaultVirtualService.KEY_SERVICE_STRATEGY)));
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
