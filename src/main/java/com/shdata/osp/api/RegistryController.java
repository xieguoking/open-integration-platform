package com.shdata.osp.api;

import com.shdata.osp.dto.ServiceConfigDTO;
import com.shdata.osp.spi.VirtualServiceRegistry;
import com.shdata.osp.vs.DubboVirtualService;
import com.shdata.osp.vs.shenyu.nacos.ShenYuNacosCenterServiceRegistry;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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


    private final DiscoveryClient discoveryClient;
    private final VirtualServiceRegistry virtualServiceRegistry;
    private final ShenYuNacosCenterServiceRegistry shenYuNacosCenterServiceRegistry;


    @GetMapping("app/{name}/{ip}")
    @ApiOperation("服务注册")
    public Object registry(@ApiParam("服务名") @PathVariable String name, @ApiParam("IP") @PathVariable(required = false, value = "127.0.0.1") String ip) {
        DubboVirtualService virtualService = new DubboVirtualService();
        virtualService.setPackagePrefix("com.shdata");
        virtualService.setService(name);
        virtualService.setServiceName(name);
        virtualService.setIp(ip);
        virtualService.setServiceType("dubbo");

        virtualServiceRegistry.register(virtualService);

        List<String> services = discoveryClient.getServices();
        return services;
    }


    @PostMapping("serviceConfig")
    @ApiOperation("服务注册")
    public Object serviceConfig(@Valid ServiceConfigDTO serviceConfigDTO) {
        DubboVirtualService virtualService = new DubboVirtualService();
        virtualService.setService(serviceConfigDTO.getServiceId());
        virtualService.setServiceName(serviceConfigDTO.getServiceName());
        virtualService.setIp(serviceConfigDTO.getOspIp());
        virtualService.setPort(serviceConfigDTO.getOspPort());
        virtualService.setServiceType(serviceConfigDTO.getServiceType());
        virtualService.setTransformStrategy(serviceConfigDTO.getTransformStrategy());

        virtualServiceRegistry.register(virtualService);
        shenYuNacosCenterServiceRegistry.register(serviceConfigDTO);
//        TODO 持久化落库
        return serviceConfigDTO;
    }

}
