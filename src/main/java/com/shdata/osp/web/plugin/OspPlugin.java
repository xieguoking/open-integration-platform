package com.shdata.osp.web.plugin;

import com.shdata.osp.web.strategy.TransformStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface OspPlugin {

    /**
     * 名称
     *
     * @return
     */
    String named();

    /**
     * 执行
     *
     * @param httpServletRequest
     * @param httpServletResponse
     */
    void execute(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, TransformStrategy transformStrategy) throws IOException;
}
