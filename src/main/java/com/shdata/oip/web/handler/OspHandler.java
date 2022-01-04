package com.shdata.oip.web.handler;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/29
 */
public interface OspHandler {


    ModelAndView handle(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws IOException;
}
