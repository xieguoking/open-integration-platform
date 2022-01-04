package com.shdata.osp.web.plugin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wangwj
 * @version 1.0
 * @date 2022/1/3
 */

public interface OspPluginChain {

    /**
     * 责任链：执行
     */
    void execute(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse);
}
