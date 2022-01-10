package com.shdata.oip.modular.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.shdata.oip.core.common.OipConstants;
import com.shdata.oip.core.dubbo.DubboMetaDataManager;
import com.shdata.oip.core.dubbo.po.MetaData;
import com.shdata.oip.core.dubbo.po.Methods;
import com.shdata.oip.core.manage.ServiceInstanceManager;
import com.shdata.oip.modular.model.po.ApiArgs;
import com.shdata.oip.modular.model.po.Apis;
import com.shdata.oip.modular.model.po.ServiceConfig;
import com.shdata.oip.modular.service.IApiArgsService;
import com.shdata.oip.modular.service.IApisService;
import com.shdata.oip.modular.service.IServiceConfigService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wangwj
 * @version 1.0
 * @date 2022/1/6
 * manage
 */
@Slf4j
public class VirtualMetaDataHandler {

    @Autowired
    private IServiceConfigService iServiceConfigService;
    @Autowired
    private IApisService iApisService;
    @Autowired
    private IApiArgsService iApisArgsService;
    @Autowired
    private ServiceInstanceManager serviceInstanceManager;
    @Autowired
    private DubboMetaDataManager dubboMetaDataManager;

    private ScheduledExecutorService refreshMetaDataExecutor;
    private final long refreshMetaDataInternal = TimeUnit.SECONDS.toMillis(60);
    private final long initialDelay = TimeUnit.SECONDS.toMillis(60);
    private long lastMetaDataRefreshTime = 0L;

    @PostConstruct
    public void init() {
        refreshMetaDataExecutor = new ScheduledThreadPoolExecutor(1);
        refreshMetaDataExecutor.scheduleWithFixedDelay(() -> refreshMetaDataIfNeed(), initialDelay, refreshMetaDataInternal, TimeUnit.MILLISECONDS);
    }

    private void refreshMetaDataIfNeed() {
        try {
            log.info("Start refresh dubbo MetaData List");
            if (System.currentTimeMillis() - lastMetaDataRefreshTime < refreshMetaDataInternal) {
                return;
            }
            buildVsApiList();
            lastMetaDataRefreshTime = System.currentTimeMillis();
        } catch (Throwable e) {
            log.warn("failed to update server list", e);
        }
    }


    private void buildVsApiList() {
        //vs config services is empty
        List<ServiceConfig> serviceIds = iServiceConfigService.list(new QueryWrapper<ServiceConfig>());

        if (CollUtil.isEmpty(serviceIds)) {
            return;
        }

        //onLine services is empty
        List<String> allServices = serviceInstanceManager.allService();
        if (CollUtil.isEmpty(allServices)) {
            return;
        }


        for (int i = 0; i < serviceIds.size(); i++) {
            ServiceInstance serviceInstance = serviceInstanceManager.getServiceInstanceOne(serviceIds.get(i).getServiceID());
            if (Objects.isNull(serviceInstance)) {
                continue;
            }

            //example : com.wonders ,com.shdata
            String keyPackAgePrefix = serviceInstance.getMetadata().get(OipConstants.KEY_PACKAGE_PREFIX);
            if (StrUtil.isBlank(keyPackAgePrefix)) {
                //if not config page prefix ,continue because no target
                continue;
            }

            VirtualMetaDataDto virtualMetaDataDto = new VirtualMetaDataDto(serviceIds.get(i).getId(), serviceIds.get(i).getServiceID());
            allServices.parallelStream()
                    .filter(o -> o.startsWith(CommonConstants.PROVIDER))
                    .filter(o -> o.contains(keyPackAgePrefix))
                    .forEach(x -> this.buildVsApiMeta(x, virtualMetaDataDto));
        }
    }

    private void buildVsApiMeta(String provider, VirtualMetaDataDto virtualMetaDataDto) {
        try {
            String interfaceName = StrUtil.replace(provider, "providers:", "");
            MetaData metaData = dubboMetaDataManager.getMetaData(interfaceName);
            if (Objects.isNull(metaData)) {
                throw new Exception("[" + provider + "]无法获取到元数据");
            }

            List<Apis> apisList = iApisService.list(new QueryWrapper<Apis>().lambda().eq(Apis::getSid, virtualMetaDataDto.getSid()));
            if (CollUtil.isNotEmpty(apisList)) {
                List<Long> apisIds = apisList.stream().map(x -> x.getApiid()).collect(Collectors.toList());
                iApisArgsService.removeByIds(apisIds);
                iApisService.removeByIds(apisIds);
            }

            virtualMetaDataDto.setMetaData(metaData);
            buildVsApiMeta(virtualMetaDataDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    private void buildVsApiMeta(VirtualMetaDataDto virtualMetaDataDto) {
        List<Apis> intoDbApis = new ArrayList<>();
        List<ApiArgs> intoDbApiArgs = new ArrayList<>();

        MetaData metaData = virtualMetaDataDto.getMetaData();
        //do it
        metaData.getMethods().keySet().stream().forEach(k -> {
            Methods methods = metaData.getMethods().get(k);
            //only one
            Long apiId = IdWorker.getId();

            //build
            Apis apis = new Apis();
            apis.setApiid(apiId);
            apis.setApiName(String.format("%s.%s", metaData.getInterfaceName(), k));
            apis.setServiceID(virtualMetaDataDto.getServiceId());
            apis.setSid(virtualMetaDataDto.getSid());
            apis.setApiStatus("1");
            intoDbApis.add(apis);

            for (int i = 0; i < methods.getParameterTypes().size(); i++) {
                ApiArgs apiArgs = new ApiArgs();
                apiArgs.setApiid(apiId);
                apiArgs.setArgOrder(i);
                apiArgs.setArgName(methods.getParameterTypes().get(i));
                apiArgs.setArgType(methods.getParameterTypes().get(i));
                apiArgs.setSid(virtualMetaDataDto.getSid());
                apiArgs.setServiceID(virtualMetaDataDto.getServiceId());
                intoDbApiArgs.add(apiArgs);
            }
        });
        iApisService.saveBatch(intoDbApis);
        iApisArgsService.saveBatch(intoDbApiArgs);
    }


    @Setter
    @Getter
    class VirtualMetaDataDto {
        private Long sid;
        private String serviceId;
        private MetaData metaData;

        public VirtualMetaDataDto(Long sid, String serviceId) {
            this.sid = sid;
            this.serviceId = serviceId;
        }
    }
}
