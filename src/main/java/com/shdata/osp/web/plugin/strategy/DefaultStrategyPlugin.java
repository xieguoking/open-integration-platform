package com.shdata.osp.web.plugin.strategy;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.shdata.osp.web.plugin.OspPlugin;
import com.shdata.osp.web.plugin.OspPluginChain;
import com.shdata.osp.web.plugin.base.PluginEnum;

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
    public boolean skip(HttpServletRequest httpServletRequest) {
        return false;
    }


    /**
     * 完整的请求路径/dubbo-service/public/v1/com/shdata/a/b/UserService/add
     *
     * @param httpServletRequest
     */
    @Override
    public void execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, OspPluginChain ospPluginChain) throws IOException {
        httpServletRequest.setAttribute("strategy_rule_paris", resolveUrlToReferenceRule(httpServletRequest.getRequestURI()));
        ospPluginChain.execute(httpServletRequest, httpServletResponse);
    }


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
        map.put("interfaceName", interfaceName);
        map.put("version", "~".equals(version) ? "" : version);
        map.put("group", "~".equals(group) ? "" : group);
        map.put("serviceId", serviceId);
        map.put("method", method);
        return map;
    }
}
