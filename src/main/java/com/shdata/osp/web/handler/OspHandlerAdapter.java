package com.shdata.osp.web.handler;

import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/29
 */

public class OspHandlerAdapter implements HandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return handler instanceof OspHandler;
    }

    @Override
    public ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        OspHandler ospHandler = (OspHandler) handler;
        return ospHandler.handle(httpServletRequest, httpServletResponse);
    }

    @Override
    public long getLastModified(HttpServletRequest httpServletRequest, Object o) {
        return 0;
    }
}
