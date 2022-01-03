package com.shdata.osp.web.plugin;


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
     * 排序
     *
     * @return
     */
    int getOrder();


    /**
     * 是否不执行
     *
     * @param httpServletRequest
     * @return
     */
    boolean skip(HttpServletRequest httpServletRequest);

    /**
     * 执行
     *
     * @param httpServletRequest
     * @param httpServletResponse
     */
    void execute(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, OspPluginChain ospPluginChain) throws IOException;
}
