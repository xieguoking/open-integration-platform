package com.shdata.osp.config;

import com.shdata.osp.web.handler.OspHandler;
import com.shdata.osp.web.handler.OspHandlerAdapter;
import com.shdata.osp.web.handler.OspHandlerMapping;
import com.shdata.osp.web.handler.OspWebHandler;
import com.shdata.osp.web.plugin.DubboOspPlugin;
import com.shdata.osp.web.plugin.OspPlugin;
import com.shdata.osp.web.strategy.DefaultTransformStrategy;
import com.shdata.osp.web.strategy.TransformStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/29
 */
@Configuration
public class OspConfiguration {

    /**
     * logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(OspConfiguration.class);


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
        return new OspHandlerMapping(ospWebHandler,discoveryClient);
    }

    /**
     * 处理器
     */
    @Bean
    public OspHandler ospWebHandler(final DiscoveryClient discoveryClient, final List<OspPlugin> plugins, final List<TransformStrategy> strategies) {
        return new OspWebHandler(discoveryClient, plugins, strategies);
    }


    /**
     * ==================================插件类型===============================================================
     */

    /**
     * dubbo 插件类型
     */
    @Bean
    public DubboOspPlugin dubboOspPlugin() {
        return new DubboOspPlugin();
    }

    /**
     * ==================================转换策略===============================================================
     */

    /**
     * 默认请求转换策略
     */
    @Bean
    public DefaultTransformStrategy defaultTransformStrategy() {
        return new DefaultTransformStrategy();
    }

    /**
     * ==================================end===============================================================
     */


}
