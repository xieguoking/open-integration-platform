package com.shdata.oip.modular.api;

import com.shdata.oip.core.spi.VirtualServiceRegistry;
import com.shdata.oip.core.vs.DubboVirtualService;
import com.shdata.oip.modular.model.dto.ServiceConfigDTO;
import com.shdata.oip.modular.service.IServiceConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author xieguojun
 * @author (2021 / 12 / 22 add by xieguojun)
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/")
@Api("服务自注册")
@RequiredArgsConstructor
public class RegistryController {


    private final VirtualServiceRegistry virtualServiceRegistry;
    private final IServiceConfigService iServiceConfigService;

    /**
     * api  list
     *
     * 服务配置添加
     * -----------
     * 服务配置删除
     * ---------- 下线虚服务
     * 服务配置修改
     * ---------- 重新注册虚服务
     *
     * 发布生效
     * 发布关闭（已注册服务，需要手动下线）
     *
     * 服务下线
     * 服务上线(参数 service Id,调用注册逻辑)
     *
     * 接口信息api
     * ----------name
     * ----------uri
     *
     */


    /**
     * 服务配置添加
     */
    @PostMapping("serviceConfig")
    @ApiOperation("服务注册")
    public Object serviceConfig(@Valid ServiceConfigDTO serviceConfigDTO) {
        DubboVirtualService virtualService = new DubboVirtualService();
        virtualService.setService(serviceConfigDTO.getServiceId());
        virtualService.setPackagePrefix(serviceConfigDTO.getPackagePrefix());
        virtualService.setServiceName(serviceConfigDTO.getServiceName());
        virtualService.setIp(serviceConfigDTO.getOspIp());
        virtualService.setPort(serviceConfigDTO.getOspPort());
        virtualService.setServiceType(serviceConfigDTO.getServiceType());
        virtualService.setTransformStrategy(serviceConfigDTO.getTransformStrategy());
        virtualService.setServiceDesc(serviceConfigDTO.getServiceDesc());
        iServiceConfigService.analysisIntoDb(virtualService);
        return serviceConfigDTO;
    }

    /**
     *
     */


    /**
     * 服务下线：实时
     */
    @PostMapping("serviceDown")
    @ApiOperation("服务下线")
    public void deRegistry(@RequestParam String serviceId) {
        virtualServiceRegistry.deRegister(serviceId);
    }


    /**
     * 服务发布上线:30 - 60 秒延迟
     */
    @PostMapping("serviceUp")
    @ApiOperation("服务上线")
    public void registry(@RequestParam String serviceId) {
        //
//        iServiceConfigService
    }

}
