package com.shdata.osp.web.handler;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/29
 */

public class OspHandlerMapping extends AbstractHandlerMapping {

    private final OspHandler ospWebHandler;

    public OspHandlerMapping(final OspHandler ospWebHandler) {
        this.ospWebHandler = ospWebHandler;
        setOrder(1);
    }


    @Override
    protected OspHandler getHandlerInternal(final HttpServletRequest var1) {
        //这里后续判断 如果请求第一段 服务注册中心没有就走原来的逻辑


        return ospWebHandler;
    }


    @Override
    protected CorsConfiguration getCorsConfiguration(final Object handler, final HttpServletRequest exchange) {
        return super.getCorsConfiguration(handler, exchange);
    }

}
