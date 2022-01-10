package com.shdata.oip.core.web.plugin.strategy;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.shdata.oip.core.common.OipConstants;
import com.shdata.oip.core.vs.DefaultVirtualService;
import com.shdata.oip.core.web.plugin.OspPlugin;
import com.shdata.oip.core.web.plugin.OspPluginChain;
import com.shdata.oip.core.web.plugin.base.OspConstants;
import com.shdata.oip.core.web.plugin.base.PluginEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/30
 * 默认地址转换策略
 */

public class DefaultStrategyPlugin implements OspPlugin {

    @Override
    public String named() {
        return PluginEnum.STRATEGY_DEFAULT.getName();
    }


    @Override
    public int getOrder() {
        return PluginEnum.STRATEGY_DEFAULT.getOrder();
    }

    @Override
    public boolean skip(final HttpServletRequest httpServletRequest) {
        return skip(httpServletRequest, OipConstants.KEY_SERVICE_STRATEGY, PluginEnum.STRATEGY_DEFAULT);
    }


    /**
     * 完整的请求路径/dubbo-service/public/v1/com/shdata/a/b/UserService/add
     *
     * @param httpServletRequest
     */
    @Override
    public void execute(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse,final OspPluginChain ospPluginChain) throws IOException {
        httpServletRequest.setAttribute(OspConstants.STRATEGY_RULE_PARIS_KEY, resolveUrlToReferenceRule(httpServletRequest.getRequestURI()));
        ospPluginChain.execute(httpServletRequest, httpServletResponse);
    }

    /**
     * TODO 优化
     */
    private Map<String, String> resolveUrlToReferenceRule(final String url) {
        String removePrefixString = StrUtil.removePrefix(url, "/");
        String[] removePrefixStringArr = removePrefixString.split("\\/");
        String serviceId = removePrefixStringArr[0];
        String group = removePrefixStringArr[1];
        String version = removePrefixStringArr[2];
        String method = removePrefixStringArr[removePrefixStringArr.length - 1];

        String interfaceName = Arrays.asList(removePrefixStringArr).stream()
                .filter(x -> !ArrayUtil.contains(new String[]{serviceId, group, version, method}, x))
                .collect(Collectors.joining("."));

        Map<String, String> map = new HashMap();
        map.put(OspConstants.INTERFACE_NAME_KEY, interfaceName);
        map.put(OspConstants.VERSION_KEY, "~".equals(version) ? "" : version);
        map.put(OspConstants.GROUP_KEY, "~".equals(group) ? "" : group);
        map.put(OspConstants.SERVICE_ID_KEY, serviceId);
        map.put(OspConstants.METHOD_KEY, method);
        return map;
    }
}
