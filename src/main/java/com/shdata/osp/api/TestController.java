package com.shdata.osp.api;

import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/28
 */
@RestController
public class TestController {


    @Autowired
    private DiscoveryClient discoveryClient;


    private Map<String, GenericService> genericServiceCache = new ConcurrentHashMap();


    @GetMapping("sayHello")
    public Object sayHello(@RequestParam(value = "name") String name) {
        System.out.println("收到请求：" + name);
        return "Hello," + name;
    }


    @GetMapping("dubboTest")
    public Object dubboTest(@RequestParam(value = "name") String name) {
        List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances("dubbo-message");
        Map<String, String> metadata = serviceInstanceList.get(0).getMetadata();
        String interfaceName = metadata.get("interface");
        String version = metadata.get("version");

        GenericService genericService = genericServiceCache.get("dubbo-message");
        if (genericService == null) {
            // 引用远程服务
            // 该实例很重量，里面封装了所有与注册中心及服务提供方连接，请缓存
            ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
            // 弱类型接口名
            reference.setInterface(interfaceName);
            reference.setVersion(version);
            // 声明为泛化接口
            reference.setGeneric(true);

            // 用org.apache.dubbo.rpc.service.GenericService可以替代所有接口引用
            genericService = reference.get();
            genericServiceCache.put("dubbo-message", genericService);
        }
        return genericService.$invoke("sayHello", new String[]{"java.lang.String"}, new String[]{name});
    }
}
