package com.shdata.oip.core.config;

import com.shdata.oip.core.dubbo.DubboMetaDataManager;
import com.shdata.oip.core.dubbo.NacosDubboMetaDataManager;
import com.shdata.oip.core.manage.NacosServiceInstanceManager;
import com.shdata.oip.core.manage.ServiceInstanceManager;
import com.shdata.oip.core.web.handler.OspHandler;
import com.shdata.oip.core.web.handler.OspHandlerAdapter;
import com.shdata.oip.core.web.handler.OspHandlerMapping;
import com.shdata.oip.core.web.handler.OspWebHandler;
import com.shdata.oip.core.web.plugin.OspPlugin;
import com.shdata.oip.core.web.plugin.dubbo.DubboPlugin;
import com.shdata.oip.core.web.plugin.param.RpcParamTransformPlugin;
import com.shdata.oip.core.web.plugin.strategy.DefaultStrategyPlugin;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/29
 */
@Configuration
public class OipConfig {

    /**
     * 适配器
     */
    @Bean
    public OspHandlerAdapter ospHandlerAdapter() {
        return new OspHandlerAdapter();
    }

    /**
     * 映射
     */
    @Bean
    public OspHandlerMapping ospHandlerMapping(OspHandler ospWebHandler, ServiceInstanceManager serviceInstanceManager) {
        return new OspHandlerMapping(ospWebHandler, serviceInstanceManager);
    }

    /**
     * 处理器
     */
    @Bean
    public OspHandler ospWebHandler(final ServiceInstanceManager serviceInstanceManager, final List<OspPlugin> plugins) {
        //排序
        List<OspPlugin> ospPlugins = plugins.stream()
                .sorted(Comparator.comparingInt(OspPlugin::getOrder))
                .collect(Collectors.toList());
        return new OspWebHandler(serviceInstanceManager, ospPlugins);
    }


    /**
     * ==================================插件类型===============================================================
     */

    @Bean
    public DubboPlugin dubboOspPlugin(DubboMetaDataManager dubboMetaDataManager) {
        return new DubboPlugin(dubboMetaDataManager);
    }


    @Bean
    public DubboMetaDataManager dubboMetaDataManager(ServiceInstanceManager serviceInstanceManager) {
        return new NacosDubboMetaDataManager(serviceInstanceManager);
    }


    @Bean
    public ServiceInstanceManager serviceInstanceManager(DiscoveryClient discoveryClient) {
        return new NacosServiceInstanceManager(discoveryClient);
    }

    @Bean
    public RpcParamTransformPlugin rpcParamTransformPlugin() {
        return new RpcParamTransformPlugin();
    }

    @Bean
    public DefaultStrategyPlugin defaultStrategyPlugin() {
        return new DefaultStrategyPlugin();
    }
}
