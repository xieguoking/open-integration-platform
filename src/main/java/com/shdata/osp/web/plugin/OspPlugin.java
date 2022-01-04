package com.shdata.osp.web.plugin;


import cn.hutool.core.lang.Assert;
import com.shdata.osp.web.plugin.base.OspConstants;
import com.shdata.osp.web.plugin.base.PluginEnum;
import org.apache.commons.lang3.ArrayUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

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
    boolean skip(final HttpServletRequest httpServletRequest);


    default boolean skip(final HttpServletRequest httpServletRequest, String matchKey, PluginEnum... pluginEnum) {
        if (ArrayUtils.isEmpty(pluginEnum)) {
            return true;
        }
        Map<String, String> ospContext = (Map<String, String>) httpServletRequest.getAttribute(OspConstants.OSP_CONTEXT);
        Assert.notNull(ospContext, "开放服务平台插件上下文不能为空");
        String matchValue = ospContext.getOrDefault(matchKey, "");
        for (final PluginEnum type : pluginEnum) {
            if (Objects.equals(matchValue, type.getName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 执行
     *
     * @param httpServletRequest
     * @param httpServletResponse
     */
    void execute(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final OspPluginChain ospPluginChain) throws IOException;
}
