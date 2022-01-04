package com.shdata.oip.config;

import com.shdata.oip.web.handler.OspHandler;
import com.shdata.oip.web.handler.OspHandlerAdapter;
import com.shdata.oip.web.handler.OspHandlerMapping;
import com.shdata.oip.web.handler.OspWebHandler;
import com.shdata.oip.web.plugin.OspPlugin;
import com.shdata.oip.web.plugin.dubbo.DubboPlugin;
import com.shdata.oip.web.plugin.dubbo.meta.DubboRegistryServerSync;
import com.shdata.oip.web.plugin.param.RpcParamTransformPlugin;
import com.shdata.oip.web.plugin.strategy.DefaultStrategyPlugin;
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
public class OspConfiguration {

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
    public OspHandlerMapping ospHandlerMapping(OspHandler ospWebHandler, DiscoveryClient discoveryClient) {
        return new OspHandlerMapping(ospWebHandler, discoveryClient);
    }

    /**
     * 处理器
     */
    @Bean
    public OspHandler ospWebHandler(final DiscoveryClient discoveryClient, final List<OspPlugin> plugins) {
        //排序
        List<OspPlugin> ospPlugins = plugins.stream()
                .sorted(Comparator.comparingInt(OspPlugin::getOrder))
                .collect(Collectors.toList());
        return new OspWebHandler(discoveryClient, ospPlugins);
    }


    /**
     * ==================================插件类型===============================================================
     */

    @Bean
    public DubboPlugin dubboOspPlugin(DubboRegistryServerSync dubboRegistryServerSync) {
        return new DubboPlugin(dubboRegistryServerSync);
    }

    @Bean
    public DubboRegistryServerSync dubboRegistryServerSync(DiscoveryClient discoveryClient) {
        return new DubboRegistryServerSync(discoveryClient);
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
