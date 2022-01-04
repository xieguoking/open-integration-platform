package com.shdata.osp.web.handler;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/29
 */

@Slf4j
public class OspHandlerMapping extends AbstractHandlerMapping {

    private final OspHandler ospWebHandler;
    private final DiscoveryClient discoveryClient;

    public OspHandlerMapping(final OspHandler ospWebHandler, DiscoveryClient discoveryClient) {
        this.ospWebHandler = ospWebHandler;
        this.discoveryClient = discoveryClient;
        setOrder(1);
    }


    @Override
    protected OspHandler getHandlerInternal(final HttpServletRequest var1) {
        String removePrefixString = StrUtil.removePrefix(var1.getRequestURI(), "/");
        String serviceId = removePrefixString.split("\\/")[0];

        //只接受存在服务注册中心的适配请求 每一次都会请求，这里是不是要优化一下
        List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances(serviceId);
        if (serviceInstanceList.isEmpty()) {
            log.warn("{}:注册中心不存在，不进行转发", serviceId);
            return null;
        }
        return ospWebHandler;
    }


    @Override
    protected CorsConfiguration getCorsConfiguration(final Object handler, final HttpServletRequest exchange) {
        return super.getCorsConfiguration(handler, exchange);
    }

}
