package com.shdata.osp.api;

import com.shdata.osp.spi.VirtualServiceRegistry;
import com.shdata.osp.vs.DubboVirtualService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class RegistryController {


    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private VirtualServiceRegistry virtualServiceRegistry;


    @GetMapping("app/{name}/{ip}")
    @ApiOperation("服务注册")
    public Object registry(@ApiParam("服务名") @PathVariable String name,@ApiParam("IP") @PathVariable(required = false, value = "127.0.0.1") String ip) {
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

}
