package com.shdata.oip.core.web.handler;

import cn.hutool.core.util.StrUtil;
import com.shdata.oip.core.manage.ServiceInstanceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/29
 */

@Slf4j
public class OspHandlerMapping extends AbstractHandlerMapping {

    private final OspHandler ospWebHandler;
    private final ServiceInstanceManager serviceInstanceManager;

    public OspHandlerMapping(final OspHandler ospWebHandler, ServiceInstanceManager serviceInstanceManager) {
        this.ospWebHandler = ospWebHandler;
        this.serviceInstanceManager = serviceInstanceManager;
        setOrder(1);
    }


    @Override
    protected OspHandler getHandlerInternal(final HttpServletRequest var1) {
        String removePrefixString = StrUtil.removePrefix(var1.getRequestURI(), "/");
        String serviceId = removePrefixString.split("\\/")[0];
        if (!serviceInstanceManager.allService().contains(serviceId)) {
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
